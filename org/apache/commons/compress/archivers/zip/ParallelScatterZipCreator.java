package org.apache.commons.compress.archivers.zip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;

public class ParallelScatterZipCreator {
    private final ScatterGatherBackingStoreSupplier backingStoreSupplier;
    private long compressionDoneAt;
    private final ExecutorService es;
    private final List<Future<Object>> futures;
    private long scatterDoneAt;
    private final long startedAt;
    private final List<ScatterZipOutputStream> streams;
    private final ThreadLocal<ScatterZipOutputStream> tlScatterStreams;

    class C07931 extends ThreadLocal<ScatterZipOutputStream> {
        C07931() {
        }

        protected ScatterZipOutputStream initialValue() {
            try {
                ScatterZipOutputStream scatterStream = ParallelScatterZipCreator.this.createDeferred(ParallelScatterZipCreator.this.backingStoreSupplier);
                ParallelScatterZipCreator.this.streams.add(scatterStream);
                return scatterStream;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class DefaultBackingStoreSupplier implements ScatterGatherBackingStoreSupplier {
        final AtomicInteger storeNum;

        private DefaultBackingStoreSupplier() {
            this.storeNum = new AtomicInteger(0);
        }

        public ScatterGatherBackingStore get() throws IOException {
            return new FileBasedScatterGatherBackingStore(File.createTempFile("parallelscatter", "n" + this.storeNum.incrementAndGet()));
        }
    }

    private ScatterZipOutputStream createDeferred(ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier) throws IOException {
        ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
        return new ScatterZipOutputStream(bs, StreamCompressor.create(-1, bs));
    }

    public ParallelScatterZipCreator() {
        this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    public ParallelScatterZipCreator(ExecutorService executorService) {
        this(executorService, new DefaultBackingStoreSupplier());
    }

    public ParallelScatterZipCreator(ExecutorService executorService, ScatterGatherBackingStoreSupplier backingStoreSupplier) {
        this.streams = Collections.synchronizedList(new ArrayList());
        this.futures = new ArrayList();
        this.startedAt = System.currentTimeMillis();
        this.compressionDoneAt = 0;
        this.tlScatterStreams = new C07931();
        this.backingStoreSupplier = backingStoreSupplier;
        this.es = executorService;
    }

    public void addArchiveEntry(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source) {
        submit(createCallable(zipArchiveEntry, source));
    }

    public void addArchiveEntry(ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
        submit(createCallable(zipArchiveEntryRequestSupplier));
    }

    public final void submit(Callable<Object> callable) {
        this.futures.add(this.es.submit(callable));
    }

    public final Callable<Object> createCallable(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source) {
        if (zipArchiveEntry.getMethod() == -1) {
            throw new IllegalArgumentException("Method must be set on zipArchiveEntry: " + zipArchiveEntry);
        }
        final ZipArchiveEntryRequest zipArchiveEntryRequest = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source);
        return new Callable<Object>() {
            public Object call() throws Exception {
                ((ScatterZipOutputStream) ParallelScatterZipCreator.this.tlScatterStreams.get()).addArchiveEntry(zipArchiveEntryRequest);
                return null;
            }
        };
    }

    public final Callable<Object> createCallable(final ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
        return new Callable<Object>() {
            public Object call() throws Exception {
                ((ScatterZipOutputStream) ParallelScatterZipCreator.this.tlScatterStreams.get()).addArchiveEntry(zipArchiveEntryRequestSupplier.get());
                return null;
            }
        };
    }

    public void writeTo(ZipArchiveOutputStream targetStream) throws IOException, InterruptedException, ExecutionException {
        for (Future<?> future : this.futures) {
            future.get();
        }
        this.es.shutdown();
        this.es.awaitTermination(60000, TimeUnit.SECONDS);
        this.compressionDoneAt = System.currentTimeMillis();
        for (ScatterZipOutputStream scatterStream : this.streams) {
            scatterStream.writeTo(targetStream);
            scatterStream.close();
        }
        this.scatterDoneAt = System.currentTimeMillis();
    }

    public ScatterStatistics getStatisticsMessage() {
        return new ScatterStatistics(this.compressionDoneAt - this.startedAt, this.scatterDoneAt - this.compressionDoneAt);
    }
}

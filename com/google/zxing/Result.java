package com.google.zxing;

import java.util.Enumeration;
import java.util.Hashtable;

public final class Result {
    private final BarcodeFormat format;
    private final byte[] rawBytes;
    private Hashtable resultMetadata;
    private ResultPoint[] resultPoints;
    private final String text;
    private final long timestamp;

    public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints, BarcodeFormat format) {
        this(text, rawBytes, resultPoints, format, System.currentTimeMillis());
    }

    public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints, BarcodeFormat format, long timestamp) {
        if (text == null && rawBytes == null) {
            throw new IllegalArgumentException("Text and bytes are null");
        }
        this.text = text;
        this.rawBytes = rawBytes;
        this.resultPoints = resultPoints;
        this.format = format;
        this.resultMetadata = null;
        this.timestamp = timestamp;
    }

    public String getText() {
        return this.text;
    }

    public byte[] getRawBytes() {
        return this.rawBytes;
    }

    public ResultPoint[] getResultPoints() {
        return this.resultPoints;
    }

    public BarcodeFormat getBarcodeFormat() {
        return this.format;
    }

    public Hashtable getResultMetadata() {
        return this.resultMetadata;
    }

    public void putMetadata(ResultMetadataType type, Object value) {
        if (this.resultMetadata == null) {
            this.resultMetadata = new Hashtable(3);
        }
        this.resultMetadata.put(type, value);
    }

    public void putAllMetadata(Hashtable metadata) {
        if (metadata == null) {
            return;
        }
        if (this.resultMetadata == null) {
            this.resultMetadata = metadata;
            return;
        }
        Enumeration e = metadata.keys();
        while (e.hasMoreElements()) {
            ResultMetadataType key = (ResultMetadataType) e.nextElement();
            this.resultMetadata.put(key, metadata.get(key));
        }
    }

    public void addResultPoints(ResultPoint[] newPoints) {
        if (this.resultPoints == null) {
            this.resultPoints = newPoints;
        } else if (newPoints != null && newPoints.length > 0) {
            ResultPoint[] allPoints = new ResultPoint[(this.resultPoints.length + newPoints.length)];
            System.arraycopy(this.resultPoints, 0, allPoints, 0, this.resultPoints.length);
            System.arraycopy(newPoints, 0, allPoints, this.resultPoints.length, newPoints.length);
            this.resultPoints = allPoints;
        }
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String toString() {
        if (this.text == null) {
            return new StringBuffer().append("[").append(this.rawBytes.length).append(" bytes]").toString();
        }
        return this.text;
    }
}

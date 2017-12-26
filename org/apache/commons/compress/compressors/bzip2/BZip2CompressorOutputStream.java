package org.apache.commons.compress.compressors.bzip2;

import android.support.v4.view.InputDeviceCompat;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class BZip2CompressorOutputStream extends CompressorOutputStream implements BZip2Constants {
    private static final int GREATER_ICOST = 15;
    private static final int LESSER_ICOST = 0;
    public static final int MAX_BLOCKSIZE = 9;
    public static final int MIN_BLOCKSIZE = 1;
    private final int allowableBlockSize;
    private int blockCRC;
    private final int blockSize100k;
    private BlockSort blockSorter;
    private int bsBuff;
    private int bsLive;
    private volatile boolean closed;
    private int combinedCRC;
    private final CRC crc;
    private int currentChar;
    private Data data;
    private int last;
    private int nInUse;
    private int nMTF;
    private OutputStream out;
    private int runLength;

    static final class Data {
        final byte[] block;
        final int[] fmap;
        final byte[] generateMTFValues_yy = new byte[256];
        final int[] heap = new int[260];
        final boolean[] inUse = new boolean[256];
        final int[] mtfFreq = new int[BZip2Constants.MAX_ALPHA_SIZE];
        int origPtr;
        final int[] parent = new int[516];
        final byte[] selector = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] selectorMtf = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] sendMTFValues2_pos = new byte[6];
        final int[][] sendMTFValues_code = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final short[] sendMTFValues_cost = new short[6];
        final int[] sendMTFValues_fave = new int[6];
        final byte[][] sendMTFValues_len = ((byte[][]) Array.newInstance(Byte.TYPE, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final int[][] sendMTFValues_rfreq = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final boolean[] sentMTFValues4_inUse16 = new boolean[16];
        final char[] sfmap;
        final byte[] unseqToSeq = new byte[256];
        final int[] weight = new int[516];

        Data(int blockSize100k) {
            int n = blockSize100k * BZip2Constants.BASEBLOCKSIZE;
            this.block = new byte[((n + 1) + 20)];
            this.fmap = new int[n];
            this.sfmap = new char[(n * 2)];
        }
    }

    private static void hbMakeCodeLengths(byte[] len, int[] freq, Data dat, int alphaSize, int maxLen) {
        int[] heap = dat.heap;
        int[] weight = dat.weight;
        int[] parent = dat.parent;
        int i = alphaSize;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            weight[i + 1] = (freq[i] == 0 ? 1 : freq[i]) << 8;
        }
        boolean tooLong = true;
        while (tooLong) {
            tooLong = false;
            int nNodes = alphaSize;
            int nHeap = 0;
            heap[0] = 0;
            weight[0] = 0;
            parent[0] = -2;
            for (i = 1; i <= alphaSize; i++) {
                parent[i] = -1;
                nHeap++;
                heap[nHeap] = i;
                int zz = nHeap;
                int tmp = heap[zz];
                while (weight[tmp] < weight[heap[zz >> 1]]) {
                    heap[zz] = heap[zz >> 1];
                    zz >>= 1;
                }
                heap[zz] = tmp;
            }
            while (nHeap > 1) {
                int i2;
                int n1 = heap[1];
                heap[1] = heap[nHeap];
                nHeap--;
                zz = 1;
                tmp = heap[1];
                while (true) {
                    int yy = zz << 1;
                    if (yy > nHeap) {
                        break;
                    }
                    if (yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]]) {
                        yy++;
                    }
                    if (weight[tmp] < weight[heap[yy]]) {
                        break;
                    }
                    heap[zz] = heap[yy];
                    zz = yy;
                }
                heap[zz] = tmp;
                int n2 = heap[1];
                heap[1] = heap[nHeap];
                nHeap--;
                zz = 1;
                tmp = heap[1];
                while (true) {
                    yy = zz << 1;
                    if (yy > nHeap) {
                        break;
                    }
                    if (yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]]) {
                        yy++;
                    }
                    if (weight[tmp] < weight[heap[yy]]) {
                        break;
                    }
                    heap[zz] = heap[yy];
                    zz = yy;
                }
                heap[zz] = tmp;
                nNodes++;
                parent[n2] = nNodes;
                parent[n1] = nNodes;
                int weight_n1 = weight[n1];
                int weight_n2 = weight[n2];
                int i3 = (weight_n2 & InputDeviceCompat.SOURCE_ANY) + (weight_n1 & InputDeviceCompat.SOURCE_ANY);
                if ((weight_n1 & 255) > (weight_n2 & 255)) {
                    i2 = weight_n1 & 255;
                } else {
                    i2 = weight_n2 & 255;
                }
                weight[nNodes] = (i2 + 1) | i3;
                parent[nNodes] = -1;
                nHeap++;
                heap[nHeap] = nNodes;
                zz = nHeap;
                tmp = heap[zz];
                int weight_tmp = weight[tmp];
                while (weight_tmp < weight[heap[zz >> 1]]) {
                    heap[zz] = heap[zz >> 1];
                    zz >>= 1;
                }
                heap[zz] = tmp;
            }
            for (i = 1; i <= alphaSize; i++) {
                int j = 0;
                int k = i;
                while (true) {
                    int parent_k = parent[k];
                    if (parent_k < 0) {
                        break;
                    }
                    k = parent_k;
                    j++;
                }
                len[i - 1] = (byte) j;
                if (j > maxLen) {
                    tooLong = true;
                }
            }
            if (tooLong) {
                for (i = 1; i < alphaSize; i++) {
                    weight[i] = (((weight[i] >> 8) >> 1) + 1) << 8;
                }
            }
        }
    }

    public static int chooseBlockSize(long inputLength) {
        return inputLength > 0 ? (int) Math.min((inputLength / 132000) + 1, 9) : 9;
    }

    public BZip2CompressorOutputStream(OutputStream out) throws IOException {
        this(out, 9);
    }

    public BZip2CompressorOutputStream(OutputStream out, int blockSize) throws IOException {
        this.crc = new CRC();
        this.currentChar = -1;
        this.runLength = 0;
        if (blockSize < 1) {
            throw new IllegalArgumentException("blockSize(" + blockSize + ") < 1");
        } else if (blockSize > 9) {
            throw new IllegalArgumentException("blockSize(" + blockSize + ") > 9");
        } else {
            this.blockSize100k = blockSize;
            this.out = out;
            this.allowableBlockSize = (this.blockSize100k * BZip2Constants.BASEBLOCKSIZE) - 20;
            init();
        }
    }

    public void write(int b) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        write0(b);
    }

    private void writeRun() throws IOException {
        int lastShadow = this.last;
        if (lastShadow < this.allowableBlockSize) {
            int currentCharShadow = this.currentChar;
            Data dataShadow = this.data;
            dataShadow.inUse[currentCharShadow] = true;
            byte ch = (byte) currentCharShadow;
            int runLengthShadow = this.runLength;
            this.crc.updateCRC(currentCharShadow, runLengthShadow);
            byte[] block;
            switch (runLengthShadow) {
                case 1:
                    dataShadow.block[lastShadow + 2] = ch;
                    this.last = lastShadow + 1;
                    return;
                case 2:
                    dataShadow.block[lastShadow + 2] = ch;
                    dataShadow.block[lastShadow + 3] = ch;
                    this.last = lastShadow + 2;
                    return;
                case 3:
                    block = dataShadow.block;
                    block[lastShadow + 2] = ch;
                    block[lastShadow + 3] = ch;
                    block[lastShadow + 4] = ch;
                    this.last = lastShadow + 3;
                    return;
                default:
                    runLengthShadow -= 4;
                    dataShadow.inUse[runLengthShadow] = true;
                    block = dataShadow.block;
                    block[lastShadow + 2] = ch;
                    block[lastShadow + 3] = ch;
                    block[lastShadow + 4] = ch;
                    block[lastShadow + 5] = ch;
                    block[lastShadow + 6] = (byte) runLengthShadow;
                    this.last = lastShadow + 5;
                    return;
            }
        }
        endBlock();
        initBlock();
        writeRun();
    }

    protected void finalize() throws Throwable {
        if (!this.closed) {
            System.err.println("Unclosed BZip2CompressorOutputStream detected, will *not* close it");
        }
        super.finalize();
    }

    public void finish() throws IOException {
        if (!this.closed) {
            this.closed = true;
            try {
                if (this.runLength > 0) {
                    writeRun();
                }
                this.currentChar = -1;
                endBlock();
                endCompression();
            } finally {
                this.out = null;
                this.blockSorter = null;
                this.data = null;
            }
        }
    }

    public void close() throws IOException {
        if (!this.closed) {
            OutputStream outShadow = this.out;
            finish();
            outShadow.close();
        }
    }

    public void flush() throws IOException {
        OutputStream outShadow = this.out;
        if (outShadow != null) {
            outShadow.flush();
        }
    }

    private void init() throws IOException {
        bsPutUByte(66);
        bsPutUByte(90);
        this.data = new Data(this.blockSize100k);
        this.blockSorter = new BlockSort(this.data);
        bsPutUByte(104);
        bsPutUByte(this.blockSize100k + 48);
        this.combinedCRC = 0;
        initBlock();
    }

    private void initBlock() {
        this.crc.initialiseCRC();
        this.last = -1;
        boolean[] inUse = this.data.inUse;
        int i = 256;
        while (true) {
            i--;
            if (i >= 0) {
                inUse[i] = false;
            } else {
                return;
            }
        }
    }

    private void endBlock() throws IOException {
        this.blockCRC = this.crc.getFinalCRC();
        this.combinedCRC = (this.combinedCRC << 1) | (this.combinedCRC >>> 31);
        this.combinedCRC ^= this.blockCRC;
        if (this.last != -1) {
            blockSort();
            bsPutUByte(49);
            bsPutUByte(65);
            bsPutUByte(89);
            bsPutUByte(38);
            bsPutUByte(83);
            bsPutUByte(89);
            bsPutInt(this.blockCRC);
            bsW(1, 0);
            moveToFrontCodeAndSend();
        }
    }

    private void endCompression() throws IOException {
        bsPutUByte(23);
        bsPutUByte(114);
        bsPutUByte(69);
        bsPutUByte(56);
        bsPutUByte(80);
        bsPutUByte(144);
        bsPutInt(this.combinedCRC);
        bsFinishedWithStream();
    }

    public final int getBlockSize() {
        return this.blockSize100k;
    }

    public void write(byte[] buf, int offs, int len) throws IOException {
        if (offs < 0) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
        } else if (len < 0) {
            throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
        } else if (offs + len > buf.length) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > buf.length(" + buf.length + ").");
        } else if (this.closed) {
            throw new IOException("stream closed");
        } else {
            int hi = offs + len;
            int offs2 = offs;
            while (offs2 < hi) {
                offs = offs2 + 1;
                write0(buf[offs2]);
                offs2 = offs;
            }
        }
    }

    private void write0(int b) throws IOException {
        if (this.currentChar != -1) {
            b &= 255;
            if (this.currentChar == b) {
                int i = this.runLength + 1;
                this.runLength = i;
                if (i > 254) {
                    writeRun();
                    this.currentChar = -1;
                    this.runLength = 0;
                    return;
                }
                return;
            }
            writeRun();
            this.runLength = 1;
            this.currentChar = b;
            return;
        }
        this.currentChar = b & 255;
        this.runLength++;
    }

    private static void hbAssignCodes(int[] code, byte[] length, int minLen, int maxLen, int alphaSize) {
        int vec = 0;
        for (int n = minLen; n <= maxLen; n++) {
            for (int i = 0; i < alphaSize; i++) {
                if ((length[i] & 255) == n) {
                    code[i] = vec;
                    vec++;
                }
            }
            vec <<= 1;
        }
    }

    private void bsFinishedWithStream() throws IOException {
        while (this.bsLive > 0) {
            this.out.write(this.bsBuff >> 24);
            this.bsBuff <<= 8;
            this.bsLive -= 8;
        }
    }

    private void bsW(int n, int v) throws IOException {
        OutputStream outShadow = this.out;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        while (bsLiveShadow >= 8) {
            outShadow.write(bsBuffShadow >> 24);
            bsBuffShadow <<= 8;
            bsLiveShadow -= 8;
        }
        this.bsBuff = (v << ((32 - bsLiveShadow) - n)) | bsBuffShadow;
        this.bsLive = bsLiveShadow + n;
    }

    private void bsPutUByte(int c) throws IOException {
        bsW(8, c);
    }

    private void bsPutInt(int u) throws IOException {
        bsW(8, (u >> 24) & 255);
        bsW(8, (u >> 16) & 255);
        bsW(8, (u >> 8) & 255);
        bsW(8, u & 255);
    }

    private void sendMTFValues() throws IOException {
        byte[][] len = this.data.sendMTFValues_len;
        int alphaSize = this.nInUse + 2;
        int t = 6;
        while (true) {
            t--;
            if (t < 0) {
                break;
            }
            byte[] len_t = len[t];
            int v = alphaSize;
            while (true) {
                v--;
                if (v >= 0) {
                    len_t[v] = (byte) 15;
                }
            }
        }
        int nGroups = this.nMTF < 200 ? 2 : this.nMTF < 600 ? 3 : this.nMTF < 1200 ? 4 : this.nMTF < 2400 ? 5 : 6;
        sendMTFValues0(nGroups, alphaSize);
        int nSelectors = sendMTFValues1(nGroups, alphaSize);
        sendMTFValues2(nGroups, nSelectors);
        sendMTFValues3(nGroups, alphaSize);
        sendMTFValues4();
        sendMTFValues5(nGroups, nSelectors);
        sendMTFValues6(nGroups, alphaSize);
        sendMTFValues7();
    }

    private void sendMTFValues0(int nGroups, int alphaSize) {
        byte[][] len = this.data.sendMTFValues_len;
        int[] mtfFreq = this.data.mtfFreq;
        int remF = this.nMTF;
        int gs = 0;
        int nPart = nGroups;
        while (nPart > 0) {
            int ge;
            int tFreq = remF / nPart;
            int aFreq = 0;
            int a = alphaSize - 1;
            int ge2 = gs - 1;
            while (aFreq < tFreq && ge2 < a) {
                ge = ge2 + 1;
                aFreq += mtfFreq[ge];
                ge2 = ge;
            }
            if (ge2 <= gs || nPart == nGroups || nPart == 1 || ((nGroups - nPart) & 1) == 0) {
                ge = ge2;
            } else {
                ge = ge2 - 1;
                aFreq -= mtfFreq[ge2];
            }
            byte[] len_np = len[nPart - 1];
            int v = alphaSize;
            while (true) {
                v--;
                if (v < 0) {
                    break;
                } else if (v < gs || v > ge) {
                    len_np[v] = (byte) 15;
                } else {
                    len_np[v] = (byte) 0;
                }
            }
            gs = ge + 1;
            remF -= aFreq;
            nPart--;
        }
    }

    private int sendMTFValues1(int nGroups, int alphaSize) {
        Data dataShadow = this.data;
        int[][] rfreq = dataShadow.sendMTFValues_rfreq;
        int[] fave = dataShadow.sendMTFValues_fave;
        short[] cost = dataShadow.sendMTFValues_cost;
        char[] sfmap = dataShadow.sfmap;
        byte[] selector = dataShadow.selector;
        byte[][] len = dataShadow.sendMTFValues_len;
        byte[] len_0 = len[0];
        byte[] len_1 = len[1];
        byte[] len_2 = len[2];
        byte[] len_3 = len[3];
        byte[] len_4 = len[4];
        byte[] len_5 = len[5];
        int nMTFShadow = this.nMTF;
        int nSelectors = 0;
        for (int iter = 0; iter < 4; iter++) {
            int t = nGroups;
            while (true) {
                t--;
                if (t < 0) {
                    break;
                }
                fave[t] = 0;
                int[] rfreqt = rfreq[t];
                int i = alphaSize;
                while (true) {
                    i--;
                    if (i >= 0) {
                        rfreqt[i] = 0;
                    }
                }
            }
            nSelectors = 0;
            int gs = 0;
            while (gs < this.nMTF) {
                int ge = Math.min((gs + 50) - 1, nMTFShadow - 1);
                int icv;
                if (nGroups == 6) {
                    short cost0 = (short) 0;
                    short cost1 = (short) 0;
                    short cost2 = (short) 0;
                    short cost3 = (short) 0;
                    short cost4 = (short) 0;
                    short cost5 = (short) 0;
                    for (i = gs; i <= ge; i++) {
                        icv = sfmap[i];
                        cost0 = (short) ((len_0[icv] & 255) + cost0);
                        cost1 = (short) ((len_1[icv] & 255) + cost1);
                        cost2 = (short) ((len_2[icv] & 255) + cost2);
                        cost3 = (short) ((len_3[icv] & 255) + cost3);
                        cost4 = (short) ((len_4[icv] & 255) + cost4);
                        cost5 = (short) ((len_5[icv] & 255) + cost5);
                    }
                    cost[0] = cost0;
                    cost[1] = cost1;
                    cost[2] = cost2;
                    cost[3] = cost3;
                    cost[4] = cost4;
                    cost[5] = cost5;
                } else {
                    t = nGroups;
                    while (true) {
                        t--;
                        if (t < 0) {
                            break;
                        }
                        cost[t] = (short) 0;
                    }
                    for (i = gs; i <= ge; i++) {
                        icv = sfmap[i];
                        t = nGroups;
                        while (true) {
                            t--;
                            if (t < 0) {
                                break;
                            }
                            cost[t] = (short) (cost[t] + (len[t][icv] & 255));
                        }
                    }
                }
                int bt = -1;
                t = nGroups;
                int bc = 999999999;
                while (true) {
                    t--;
                    if (t < 0) {
                        break;
                    }
                    int cost_t = cost[t];
                    if (cost_t < bc) {
                        bc = cost_t;
                        bt = t;
                    }
                }
                fave[bt] = fave[bt] + 1;
                selector[nSelectors] = (byte) bt;
                nSelectors++;
                int[] rfreq_bt = rfreq[bt];
                for (i = gs; i <= ge; i++) {
                    char c = sfmap[i];
                    rfreq_bt[c] = rfreq_bt[c] + 1;
                }
                gs = ge + 1;
            }
            for (t = 0; t < nGroups; t++) {
                hbMakeCodeLengths(len[t], rfreq[t], this.data, alphaSize, 20);
            }
        }
        return nSelectors;
    }

    private void sendMTFValues2(int nGroups, int nSelectors) {
        Data dataShadow = this.data;
        byte[] pos = dataShadow.sendMTFValues2_pos;
        int i = nGroups;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            pos[i] = (byte) i;
        }
        for (i = 0; i < nSelectors; i++) {
            byte ll_i = dataShadow.selector[i];
            byte tmp = pos[0];
            int j = 0;
            while (ll_i != tmp) {
                j++;
                byte tmp2 = tmp;
                tmp = pos[j];
                pos[j] = tmp2;
            }
            pos[0] = tmp;
            dataShadow.selectorMtf[i] = (byte) j;
        }
    }

    private void sendMTFValues3(int nGroups, int alphaSize) {
        int[][] code = this.data.sendMTFValues_code;
        byte[][] len = this.data.sendMTFValues_len;
        for (int t = 0; t < nGroups; t++) {
            int minLen = 32;
            int maxLen = 0;
            byte[] len_t = len[t];
            int i = alphaSize;
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                int l = len_t[i] & 255;
                if (l > maxLen) {
                    maxLen = l;
                }
                if (l < minLen) {
                    minLen = l;
                }
            }
            hbAssignCodes(code[t], len[t], minLen, maxLen, alphaSize);
        }
    }

    private void sendMTFValues4() throws IOException {
        boolean[] inUse = this.data.inUse;
        boolean[] inUse16 = this.data.sentMTFValues4_inUse16;
        int i = 16;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            inUse16[i] = false;
            int i16 = i * 16;
            int j = 16;
            while (true) {
                j--;
                if (j >= 0) {
                    if (inUse[i16 + j]) {
                        inUse16[i] = true;
                    }
                }
            }
        }
        for (i = 0; i < 16; i++) {
            int i2;
            if (inUse16[i]) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            bsW(1, i2);
        }
        OutputStream outShadow = this.out;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        for (i = 0; i < 16; i++) {
            if (inUse16[i]) {
                i16 = i * 16;
                for (j = 0; j < 16; j++) {
                    while (bsLiveShadow >= 8) {
                        outShadow.write(bsBuffShadow >> 24);
                        bsBuffShadow <<= 8;
                        bsLiveShadow -= 8;
                    }
                    if (inUse[i16 + j]) {
                        bsBuffShadow |= 1 << ((32 - bsLiveShadow) - 1);
                    }
                    bsLiveShadow++;
                }
            }
        }
        this.bsBuff = bsBuffShadow;
        this.bsLive = bsLiveShadow;
    }

    private void sendMTFValues5(int nGroups, int nSelectors) throws IOException {
        bsW(3, nGroups);
        bsW(15, nSelectors);
        OutputStream outShadow = this.out;
        byte[] selectorMtf = this.data.selectorMtf;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        for (int i = 0; i < nSelectors; i++) {
            int hj = selectorMtf[i] & 255;
            for (int j = 0; j < hj; j++) {
                while (bsLiveShadow >= 8) {
                    outShadow.write(bsBuffShadow >> 24);
                    bsBuffShadow <<= 8;
                    bsLiveShadow -= 8;
                }
                bsBuffShadow |= 1 << ((32 - bsLiveShadow) - 1);
                bsLiveShadow++;
            }
            while (bsLiveShadow >= 8) {
                outShadow.write(bsBuffShadow >> 24);
                bsBuffShadow <<= 8;
                bsLiveShadow -= 8;
            }
            bsLiveShadow++;
        }
        this.bsBuff = bsBuffShadow;
        this.bsLive = bsLiveShadow;
    }

    private void sendMTFValues6(int nGroups, int alphaSize) throws IOException {
        byte[][] len = this.data.sendMTFValues_len;
        OutputStream outShadow = this.out;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        for (int t = 0; t < nGroups; t++) {
            byte[] len_t = len[t];
            int curr = len_t[0] & 255;
            while (bsLiveShadow >= 8) {
                outShadow.write(bsBuffShadow >> 24);
                bsBuffShadow <<= 8;
                bsLiveShadow -= 8;
            }
            bsBuffShadow |= curr << ((32 - bsLiveShadow) - 5);
            bsLiveShadow += 5;
            for (int i = 0; i < alphaSize; i++) {
                int lti = len_t[i] & 255;
                while (curr < lti) {
                    while (bsLiveShadow >= 8) {
                        outShadow.write(bsBuffShadow >> 24);
                        bsBuffShadow <<= 8;
                        bsLiveShadow -= 8;
                    }
                    bsBuffShadow |= 2 << ((32 - bsLiveShadow) - 2);
                    bsLiveShadow += 2;
                    curr++;
                }
                while (curr > lti) {
                    while (bsLiveShadow >= 8) {
                        outShadow.write(bsBuffShadow >> 24);
                        bsBuffShadow <<= 8;
                        bsLiveShadow -= 8;
                    }
                    bsBuffShadow |= 3 << ((32 - bsLiveShadow) - 2);
                    bsLiveShadow += 2;
                    curr--;
                }
                while (bsLiveShadow >= 8) {
                    outShadow.write(bsBuffShadow >> 24);
                    bsBuffShadow <<= 8;
                    bsLiveShadow -= 8;
                }
                bsLiveShadow++;
            }
        }
        this.bsBuff = bsBuffShadow;
        this.bsLive = bsLiveShadow;
    }

    private void sendMTFValues7() throws IOException {
        Data dataShadow = this.data;
        byte[][] len = dataShadow.sendMTFValues_len;
        int[][] code = dataShadow.sendMTFValues_code;
        OutputStream outShadow = this.out;
        byte[] selector = dataShadow.selector;
        char[] sfmap = dataShadow.sfmap;
        int nMTFShadow = this.nMTF;
        int selCtr = 0;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        int gs = 0;
        while (gs < nMTFShadow) {
            int ge = Math.min((gs + 50) - 1, nMTFShadow - 1);
            int selector_selCtr = selector[selCtr] & 255;
            int[] code_selCtr = code[selector_selCtr];
            byte[] len_selCtr = len[selector_selCtr];
            while (gs <= ge) {
                int sfmap_i = sfmap[gs];
                while (bsLiveShadow >= 8) {
                    outShadow.write(bsBuffShadow >> 24);
                    bsBuffShadow <<= 8;
                    bsLiveShadow -= 8;
                }
                int n = len_selCtr[sfmap_i] & 255;
                bsBuffShadow |= code_selCtr[sfmap_i] << ((32 - bsLiveShadow) - n);
                bsLiveShadow += n;
                gs++;
            }
            gs = ge + 1;
            selCtr++;
        }
        this.bsBuff = bsBuffShadow;
        this.bsLive = bsLiveShadow;
    }

    private void moveToFrontCodeAndSend() throws IOException {
        bsW(24, this.data.origPtr);
        generateMTFValues();
        sendMTFValues();
    }

    private void blockSort() {
        this.blockSorter.blockSort(this.data, this.last);
    }

    private void generateMTFValues() {
        int i;
        int lastShadow = this.last;
        Data dataShadow = this.data;
        boolean[] inUse = dataShadow.inUse;
        byte[] block = dataShadow.block;
        int[] fmap = dataShadow.fmap;
        char[] sfmap = dataShadow.sfmap;
        int[] mtfFreq = dataShadow.mtfFreq;
        byte[] unseqToSeq = dataShadow.unseqToSeq;
        byte[] yy = dataShadow.generateMTFValues_yy;
        int nInUseShadow = 0;
        for (i = 0; i < 256; i++) {
            if (inUse[i]) {
                unseqToSeq[i] = (byte) nInUseShadow;
                nInUseShadow++;
            }
        }
        this.nInUse = nInUseShadow;
        int eob = nInUseShadow + 1;
        for (i = eob; i >= 0; i--) {
            mtfFreq[i] = 0;
        }
        i = nInUseShadow;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            yy[i] = (byte) i;
        }
        int wr = 0;
        int zPend = 0;
        for (i = 0; i <= lastShadow; i++) {
            byte ll_i = unseqToSeq[block[fmap[i]] & 255];
            byte tmp = yy[0];
            int j = 0;
            while (ll_i != tmp) {
                j++;
                byte tmp2 = tmp;
                tmp = yy[j];
                yy[j] = tmp2;
            }
            yy[0] = tmp;
            if (j == 0) {
                zPend++;
            } else {
                if (zPend > 0) {
                    zPend--;
                    while (true) {
                        if ((zPend & 1) == 0) {
                            sfmap[wr] = '\u0000';
                            wr++;
                            mtfFreq[0] = mtfFreq[0] + 1;
                        } else {
                            sfmap[wr] = '\u0001';
                            wr++;
                            mtfFreq[1] = mtfFreq[1] + 1;
                        }
                        if (zPend < 2) {
                            break;
                        }
                        zPend = (zPend - 2) >> 1;
                    }
                    zPend = 0;
                }
                sfmap[wr] = (char) (j + 1);
                wr++;
                int i2 = j + 1;
                mtfFreq[i2] = mtfFreq[i2] + 1;
            }
        }
        if (zPend > 0) {
            zPend--;
            while (true) {
                if ((zPend & 1) == 0) {
                    sfmap[wr] = '\u0000';
                    wr++;
                    mtfFreq[0] = mtfFreq[0] + 1;
                } else {
                    sfmap[wr] = '\u0001';
                    wr++;
                    mtfFreq[1] = mtfFreq[1] + 1;
                }
                if (zPend < 2) {
                    break;
                }
                zPend = (zPend - 2) >> 1;
            }
        }
        sfmap[wr] = (char) eob;
        mtfFreq[eob] = mtfFreq[eob] + 1;
        this.nMTF = wr + 1;
    }
}

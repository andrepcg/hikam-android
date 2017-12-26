package org.apache.commons.compress.compressors.bzip2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BitInputStream;
import org.apache.commons.compress.utils.CloseShieldFilterInputStream;

public class BZip2CompressorInputStream extends CompressorInputStream implements BZip2Constants {
    private static final int EOF = 0;
    private static final int NO_RAND_PART_A_STATE = 5;
    private static final int NO_RAND_PART_B_STATE = 6;
    private static final int NO_RAND_PART_C_STATE = 7;
    private static final int RAND_PART_A_STATE = 2;
    private static final int RAND_PART_B_STATE = 3;
    private static final int RAND_PART_C_STATE = 4;
    private static final int START_BLOCK_STATE = 1;
    private BitInputStream bin;
    private boolean blockRandomised;
    private int blockSize100k;
    private int computedBlockCRC;
    private int computedCombinedCRC;
    private final CRC crc;
    private int currentState;
    private Data data;
    private final boolean decompressConcatenated;
    private int last;
    private int nInUse;
    private int origPtr;
    private int storedBlockCRC;
    private int storedCombinedCRC;
    private int su_ch2;
    private int su_chPrev;
    private int su_count;
    private int su_i2;
    private int su_j2;
    private int su_rNToGo;
    private int su_rTPos;
    private int su_tPos;
    private char su_z;

    private static final class Data {
        final int[][] base = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final int[] cftab = new int[257];
        final char[] getAndMoveToFrontDecode_yy = new char[256];
        final boolean[] inUse = new boolean[256];
        final int[][] limit = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        byte[] ll8;
        final int[] minLens = new int[6];
        final int[][] perm = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final byte[] recvDecodingTables_pos = new byte[6];
        final byte[] selector = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] selectorMtf = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] seqToUnseq = new byte[256];
        final char[][] temp_charArray2d = ((char[][]) Array.newInstance(Character.TYPE, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        int[] tt;
        final int[] unzftab = new int[256];

        Data(int blockSize100k) {
            this.ll8 = new byte[(BZip2Constants.BASEBLOCKSIZE * blockSize100k)];
        }

        int[] initTT(int length) {
            int[] ttShadow = this.tt;
            if (ttShadow != null && ttShadow.length >= length) {
                return ttShadow;
            }
            ttShadow = new int[length];
            this.tt = ttShadow;
            return ttShadow;
        }
    }

    public BZip2CompressorInputStream(InputStream in) throws IOException {
        this(in, false);
    }

    public BZip2CompressorInputStream(InputStream in, boolean decompressConcatenated) throws IOException {
        this.crc = new CRC();
        this.currentState = 1;
        if (in == System.in) {
            in = new CloseShieldFilterInputStream(in);
        }
        this.bin = new BitInputStream(in, ByteOrder.BIG_ENDIAN);
        this.decompressConcatenated = decompressConcatenated;
        init(true);
        initBlock();
    }

    public int read() throws IOException {
        if (this.bin != null) {
            int r = read0();
            count(r < 0 ? -1 : 1);
            return r;
        }
        throw new IOException("stream closed");
    }

    public int read(byte[] dest, int offs, int len) throws IOException {
        if (offs < 0) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
        } else if (len < 0) {
            throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
        } else if (offs + len > dest.length) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > dest.length(" + dest.length + ").");
        } else if (this.bin == null) {
            throw new IOException("stream closed");
        } else if (len == 0) {
            return 0;
        } else {
            int hi = offs + len;
            int destOffs = offs;
            while (destOffs < hi) {
                int b = read0();
                if (b < 0) {
                    break;
                }
                int destOffs2 = destOffs + 1;
                dest[destOffs] = (byte) b;
                count(1);
                destOffs = destOffs2;
            }
            return destOffs == offs ? -1 : destOffs - offs;
        }
    }

    private void makeMaps() {
        boolean[] inUse = this.data.inUse;
        byte[] seqToUnseq = this.data.seqToUnseq;
        int i = 0;
        int nInUseShadow = 0;
        while (i < 256) {
            int nInUseShadow2;
            if (inUse[i]) {
                nInUseShadow2 = nInUseShadow + 1;
                seqToUnseq[nInUseShadow] = (byte) i;
            } else {
                nInUseShadow2 = nInUseShadow;
            }
            i++;
            nInUseShadow = nInUseShadow2;
        }
        this.nInUse = nInUseShadow;
    }

    private int read0() throws IOException {
        switch (this.currentState) {
            case 0:
                return -1;
            case 1:
                return setupBlock();
            case 2:
                throw new IllegalStateException();
            case 3:
                return setupRandPartB();
            case 4:
                return setupRandPartC();
            case 5:
                throw new IllegalStateException();
            case 6:
                return setupNoRandPartB();
            case 7:
                return setupNoRandPartC();
            default:
                throw new IllegalStateException();
        }
    }

    private int readNextByte(BitInputStream in) throws IOException {
        return (int) in.readBits(8);
    }

    private boolean init(boolean isFirstStream) throws IOException {
        if (this.bin == null) {
            throw new IOException("No InputStream");
        }
        if (!isFirstStream) {
            this.bin.clearBitCache();
        }
        int magic0 = readNextByte(this.bin);
        if (magic0 == -1 && !isFirstStream) {
            return false;
        }
        int magic1 = readNextByte(this.bin);
        int magic2 = readNextByte(this.bin);
        if (magic0 == 66 && magic1 == 90 && magic2 == 104) {
            int blockSize = readNextByte(this.bin);
            if (blockSize < 49 || blockSize > 57) {
                throw new IOException("BZip2 block size is invalid");
            }
            this.blockSize100k = blockSize - 48;
            this.computedCombinedCRC = 0;
            return true;
        }
        throw new IOException(isFirstStream ? "Stream is not in the BZip2 format" : "Garbage after a valid BZip2 stream");
    }

    private void initBlock() throws IOException {
        boolean z = false;
        BitInputStream bin = this.bin;
        do {
            char magic0 = bsGetUByte(bin);
            char magic1 = bsGetUByte(bin);
            char magic2 = bsGetUByte(bin);
            char magic3 = bsGetUByte(bin);
            char magic4 = bsGetUByte(bin);
            char magic5 = bsGetUByte(bin);
            if (magic0 != '\u0017' || magic1 != 'r' || magic2 != 'E' || magic3 != '8' || magic4 != 'P' || magic5 != '') {
                if (magic0 == '1' && magic1 == 'A' && magic2 == 'Y' && magic3 == '&' && magic4 == 'S' && magic5 == 'Y') {
                    this.storedBlockCRC = bsGetInt(bin);
                    if (bsR(bin, 1) == 1) {
                        z = true;
                    }
                    this.blockRandomised = z;
                    if (this.data == null) {
                        this.data = new Data(this.blockSize100k);
                    }
                    getAndMoveToFrontDecode();
                    this.crc.initialiseCRC();
                    this.currentState = 1;
                    return;
                }
                this.currentState = 0;
                throw new IOException("bad block header");
            }
        } while (!complete());
    }

    private void endBlock() throws IOException {
        this.computedBlockCRC = this.crc.getFinalCRC();
        if (this.storedBlockCRC != this.computedBlockCRC) {
            this.computedCombinedCRC = (this.storedCombinedCRC << 1) | (this.storedCombinedCRC >>> 31);
            this.computedCombinedCRC ^= this.storedBlockCRC;
            throw new IOException("BZip2 CRC error");
        }
        this.computedCombinedCRC = (this.computedCombinedCRC << 1) | (this.computedCombinedCRC >>> 31);
        this.computedCombinedCRC ^= this.computedBlockCRC;
    }

    private boolean complete() throws IOException {
        this.storedCombinedCRC = bsGetInt(this.bin);
        this.currentState = 0;
        this.data = null;
        if (this.storedCombinedCRC != this.computedCombinedCRC) {
            throw new IOException("BZip2 CRC error");
        } else if (this.decompressConcatenated && init(false)) {
            return false;
        } else {
            return true;
        }
    }

    public void close() throws IOException {
        BitInputStream inShadow = this.bin;
        if (inShadow != null) {
            try {
                inShadow.close();
            } finally {
                this.data = null;
                this.bin = null;
            }
        }
    }

    private static int bsR(BitInputStream bin, int n) throws IOException {
        long thech = bin.readBits(n);
        if (thech >= 0) {
            return (int) thech;
        }
        throw new IOException("unexpected end of stream");
    }

    private static boolean bsGetBit(BitInputStream bin) throws IOException {
        return bsR(bin, 1) != 0;
    }

    private static char bsGetUByte(BitInputStream bin) throws IOException {
        return (char) bsR(bin, 8);
    }

    private static int bsGetInt(BitInputStream bin) throws IOException {
        return bsR(bin, 32);
    }

    private static void hbCreateDecodeTables(int[] limit, int[] base, int[] perm, char[] length, int minLen, int maxLen, int alphaSize) {
        char i = minLen;
        int pp = 0;
        while (i <= maxLen) {
            int j = 0;
            int pp2 = pp;
            while (j < alphaSize) {
                if (length[j] == i) {
                    pp = pp2 + 1;
                    perm[pp2] = j;
                } else {
                    pp = pp2;
                }
                j++;
                pp2 = pp;
            }
            i++;
            pp = pp2;
        }
        int i2 = 23;
        while (true) {
            i2--;
            if (i2 <= 0) {
                break;
            }
            base[i2] = 0;
            limit[i2] = 0;
        }
        for (i2 = 0; i2 < alphaSize; i2++) {
            int i3 = length[i2] + 1;
            base[i3] = base[i3] + 1;
        }
        int b = base[0];
        for (i2 = 1; i2 < 23; i2++) {
            b += base[i2];
            base[i2] = b;
        }
        i2 = minLen;
        int vec = 0;
        b = base[i2];
        while (i2 <= maxLen) {
            int nb = base[i2 + 1];
            vec += nb - b;
            b = nb;
            limit[i2] = vec - 1;
            vec <<= 1;
            i2++;
        }
        for (i2 = minLen + 1; i2 <= maxLen; i2++) {
            base[i2] = ((limit[i2 - 1] + 1) << 1) - base[i2];
        }
    }

    private void recvDecodingTables() throws IOException {
        int i;
        int j;
        BitInputStream bin = this.bin;
        Data dataShadow = this.data;
        boolean[] inUse = dataShadow.inUse;
        byte[] pos = dataShadow.recvDecodingTables_pos;
        byte[] selector = dataShadow.selector;
        byte[] selectorMtf = dataShadow.selectorMtf;
        int inUse16 = 0;
        for (i = 0; i < 16; i++) {
            if (bsGetBit(bin)) {
                inUse16 |= 1 << i;
            }
        }
        Arrays.fill(inUse, false);
        for (i = 0; i < 16; i++) {
            if (((1 << i) & inUse16) != 0) {
                int i16 = i << 4;
                for (j = 0; j < 16; j++) {
                    if (bsGetBit(bin)) {
                        inUse[i16 + j] = true;
                    }
                }
            }
        }
        makeMaps();
        int alphaSize = this.nInUse + 2;
        int nGroups = bsR(bin, 3);
        int nSelectors = bsR(bin, 15);
        for (i = 0; i < nSelectors; i++) {
            j = 0;
            while (bsGetBit(bin)) {
                j++;
            }
            selectorMtf[i] = (byte) j;
        }
        int v = nGroups;
        while (true) {
            v--;
            if (v < 0) {
                break;
            }
            pos[v] = (byte) v;
        }
        for (i = 0; i < nSelectors; i++) {
            v = selectorMtf[i] & 255;
            byte tmp = pos[v];
            while (v > 0) {
                pos[v] = pos[v - 1];
                v--;
            }
            pos[0] = tmp;
            selector[i] = tmp;
        }
        char[][] len = dataShadow.temp_charArray2d;
        for (int t = 0; t < nGroups; t++) {
            int curr = bsR(bin, 5);
            char[] len_t = len[t];
            for (i = 0; i < alphaSize; i++) {
                while (bsGetBit(bin)) {
                    curr += bsGetBit(bin) ? -1 : 1;
                }
                len_t[i] = (char) curr;
            }
        }
        createHuffmanDecodingTables(alphaSize, nGroups);
    }

    private void createHuffmanDecodingTables(int alphaSize, int nGroups) {
        Data dataShadow = this.data;
        char[][] len = dataShadow.temp_charArray2d;
        int[] minLens = dataShadow.minLens;
        int[][] limit = dataShadow.limit;
        int[][] base = dataShadow.base;
        int[][] perm = dataShadow.perm;
        for (int t = 0; t < nGroups; t++) {
            int minLen = 32;
            int maxLen = 0;
            char[] len_t = len[t];
            int i = alphaSize;
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                char lent = len_t[i];
                if (lent > maxLen) {
                    maxLen = lent;
                }
                if (lent < minLen) {
                    minLen = lent;
                }
            }
            hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen, maxLen, alphaSize);
            minLens[t] = minLen;
        }
    }

    private void getAndMoveToFrontDecode() throws IOException {
        BitInputStream bin = this.bin;
        this.origPtr = bsR(bin, 24);
        recvDecodingTables();
        Data dataShadow = this.data;
        byte[] ll8 = dataShadow.ll8;
        int[] unzftab = dataShadow.unzftab;
        byte[] selector = dataShadow.selector;
        byte[] seqToUnseq = dataShadow.seqToUnseq;
        Object yy = dataShadow.getAndMoveToFrontDecode_yy;
        int[] minLens = dataShadow.minLens;
        int[][] limit = dataShadow.limit;
        int[][] base = dataShadow.base;
        int[][] perm = dataShadow.perm;
        int limitLast = this.blockSize100k * BZip2Constants.BASEBLOCKSIZE;
        int i = 256;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            yy[i] = (char) i;
            unzftab[i] = 0;
        }
        int groupNo = 0;
        int groupPos = 49;
        int eob = this.nInUse + 1;
        int nextSym = getAndMoveToFrontDecode0(0);
        int lastShadow = -1;
        int zt = selector[0] & 255;
        int[] base_zt = base[zt];
        int[] limit_zt = limit[zt];
        int[] perm_zt = perm[zt];
        int minLens_zt = minLens[zt];
        while (nextSym != eob) {
            int zn;
            int zvec;
            int i2;
            if (nextSym == 0 || nextSym == 1) {
                int s = -1;
                int n = 1;
                while (true) {
                    if (nextSym != 0) {
                        if (nextSym != 1) {
                            break;
                        }
                        s += n << 1;
                    } else {
                        s += n;
                    }
                    if (groupPos == 0) {
                        groupPos = 49;
                        groupNo++;
                        zt = selector[groupNo] & 255;
                        base_zt = base[zt];
                        limit_zt = limit[zt];
                        perm_zt = perm[zt];
                        minLens_zt = minLens[zt];
                    } else {
                        groupPos--;
                    }
                    zn = minLens_zt;
                    zvec = bsR(bin, zn);
                    while (zvec > limit_zt[zn]) {
                        zn++;
                        zvec = (zvec << 1) | bsR(bin, 1);
                    }
                    nextSym = perm_zt[zvec - base_zt[zn]];
                    n <<= 1;
                }
                byte ch = seqToUnseq[yy[0]];
                i2 = ch & 255;
                unzftab[i2] = unzftab[i2] + (s + 1);
                int s2 = s;
                while (true) {
                    s = s2 - 1;
                    if (s2 < 0) {
                        break;
                    }
                    lastShadow++;
                    ll8[lastShadow] = ch;
                    s2 = s;
                }
                if (lastShadow >= limitLast) {
                    throw new IOException("block overrun");
                }
            } else {
                lastShadow++;
                if (lastShadow >= limitLast) {
                    throw new IOException("block overrun");
                }
                char tmp = yy[nextSym - 1];
                i2 = seqToUnseq[tmp] & 255;
                unzftab[i2] = unzftab[i2] + 1;
                ll8[lastShadow] = seqToUnseq[tmp];
                if (nextSym <= 16) {
                    int i3 = nextSym - 1;
                    while (i3 > 0) {
                        int j = i3 - 1;
                        yy[i3] = yy[j];
                        i3 = j;
                    }
                } else {
                    System.arraycopy(yy, 0, yy, 1, nextSym - 1);
                }
                yy[0] = tmp;
                if (groupPos == 0) {
                    groupPos = 49;
                    groupNo++;
                    zt = selector[groupNo] & 255;
                    base_zt = base[zt];
                    limit_zt = limit[zt];
                    perm_zt = perm[zt];
                    minLens_zt = minLens[zt];
                } else {
                    groupPos--;
                }
                zn = minLens_zt;
                zvec = bsR(bin, zn);
                while (zvec > limit_zt[zn]) {
                    zn++;
                    zvec = (zvec << 1) | bsR(bin, 1);
                }
                nextSym = perm_zt[zvec - base_zt[zn]];
            }
        }
        this.last = lastShadow;
    }

    private int getAndMoveToFrontDecode0(int groupNo) throws IOException {
        Data dataShadow = this.data;
        int zt = dataShadow.selector[groupNo] & 255;
        int[] limit_zt = dataShadow.limit[zt];
        int zn = dataShadow.minLens[zt];
        int zvec = bsR(this.bin, zn);
        while (zvec > limit_zt[zn]) {
            zn++;
            zvec = (zvec << 1) | bsR(this.bin, 1);
        }
        return dataShadow.perm[zt][zvec - dataShadow.base[zt][zn]];
    }

    private int setupBlock() throws IOException {
        if (this.currentState == 0 || this.data == null) {
            return -1;
        }
        int i;
        int[] cftab = this.data.cftab;
        int[] tt = this.data.initTT(this.last + 1);
        byte[] ll8 = this.data.ll8;
        cftab[0] = 0;
        System.arraycopy(this.data.unzftab, 0, cftab, 1, 256);
        int c = cftab[0];
        for (i = 1; i <= 256; i++) {
            c += cftab[i];
            cftab[i] = c;
        }
        int lastShadow = this.last;
        for (i = 0; i <= lastShadow; i++) {
            int i2 = ll8[i] & 255;
            int i3 = cftab[i2];
            cftab[i2] = i3 + 1;
            tt[i3] = i;
        }
        if (this.origPtr < 0 || this.origPtr >= tt.length) {
            throw new IOException("stream corrupted");
        }
        this.su_tPos = tt[this.origPtr];
        this.su_count = 0;
        this.su_i2 = 0;
        this.su_ch2 = 256;
        if (!this.blockRandomised) {
            return setupNoRandPartA();
        }
        this.su_rNToGo = 0;
        this.su_rTPos = 0;
        return setupRandPartA();
    }

    private int setupRandPartA() throws IOException {
        int i = 1;
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            int su_ch2Shadow = this.data.ll8[this.su_tPos] & 255;
            this.su_tPos = this.data.tt[this.su_tPos];
            if (this.su_rNToGo == 0) {
                this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
                int i2 = this.su_rTPos + 1;
                this.su_rTPos = i2;
                if (i2 == 512) {
                    this.su_rTPos = 0;
                }
            } else {
                this.su_rNToGo--;
            }
            if (this.su_rNToGo != 1) {
                i = 0;
            }
            su_ch2Shadow ^= i;
            this.su_ch2 = su_ch2Shadow;
            this.su_i2++;
            this.currentState = 3;
            this.crc.updateCRC(su_ch2Shadow);
            return su_ch2Shadow;
        }
        endBlock();
        initBlock();
        return setupBlock();
    }

    private int setupNoRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            int su_ch2Shadow = this.data.ll8[this.su_tPos] & 255;
            this.su_ch2 = su_ch2Shadow;
            this.su_tPos = this.data.tt[this.su_tPos];
            this.su_i2++;
            this.currentState = 6;
            this.crc.updateCRC(su_ch2Shadow);
            return su_ch2Shadow;
        }
        this.currentState = 5;
        endBlock();
        initBlock();
        return setupBlock();
    }

    private int setupRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.currentState = 2;
            this.su_count = 1;
            return setupRandPartA();
        }
        int i = this.su_count + 1;
        this.su_count = i;
        if (i >= 4) {
            this.su_z = (char) (this.data.ll8[this.su_tPos] & 255);
            this.su_tPos = this.data.tt[this.su_tPos];
            if (this.su_rNToGo == 0) {
                this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
                i = this.su_rTPos + 1;
                this.su_rTPos = i;
                if (i == 512) {
                    this.su_rTPos = 0;
                }
            } else {
                this.su_rNToGo--;
            }
            this.su_j2 = 0;
            this.currentState = 4;
            if (this.su_rNToGo == 1) {
                this.su_z = (char) (this.su_z ^ 1);
            }
            return setupRandPartC();
        }
        this.currentState = 2;
        return setupRandPartA();
    }

    private int setupRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            this.crc.updateCRC(this.su_ch2);
            this.su_j2++;
            return this.su_ch2;
        }
        this.currentState = 2;
        this.su_i2++;
        this.su_count = 0;
        return setupRandPartA();
    }

    private int setupNoRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.su_count = 1;
            return setupNoRandPartA();
        }
        int i = this.su_count + 1;
        this.su_count = i;
        if (i < 4) {
            return setupNoRandPartA();
        }
        this.su_z = (char) (this.data.ll8[this.su_tPos] & 255);
        this.su_tPos = this.data.tt[this.su_tPos];
        this.su_j2 = 0;
        return setupNoRandPartC();
    }

    private int setupNoRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            int su_ch2Shadow = this.su_ch2;
            this.crc.updateCRC(su_ch2Shadow);
            this.su_j2++;
            this.currentState = 7;
            return su_ch2Shadow;
        }
        this.su_i2++;
        this.su_count = 0;
        return setupNoRandPartA();
    }

    public static boolean matches(byte[] signature, int length) {
        if (length >= 3 && signature[0] == (byte) 66 && signature[1] == (byte) 90 && signature[2] == (byte) 104) {
            return true;
        }
        return false;
    }
}

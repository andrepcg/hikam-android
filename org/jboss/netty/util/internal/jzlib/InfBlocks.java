package org.jboss.netty.util.internal.jzlib;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import org.apache.commons.compress.archivers.zip.UnixStat;

final class InfBlocks {
    private static final int BAD = 9;
    private static final int BTREE = 4;
    private static final int CODES = 6;
    private static final int DONE = 8;
    private static final int DRY = 7;
    private static final int DTREE = 5;
    private static final int LENS = 1;
    private static final int STORED = 2;
    private static final int TABLE = 3;
    private static final int TYPE = 0;
    private static final int[] border = new int[]{16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
    private static final int[] inflate_mask = new int[]{0, 1, 3, 7, 15, 31, 63, TransportMediator.KEYCODE_MEDIA_PAUSE, 255, UnixStat.DEFAULT_LINK_PERM, 1023, 2047, UnixStat.PERM_MASK, 8191, 16383, 32767, SupportMenu.USER_MASK};
    private final int[] bb = new int[1];
    int bitb;
    int bitk;
    private int[] blens;
    private long check;
    private final Object checkfn;
    private final InfCodes codes = new InfCodes();
    final int end;
    private int[] hufts = new int[4320];
    private int index;
    private final InfTree inftree = new InfTree();
    private int last;
    private int left;
    private int mode;
    int read;
    private int table;
    private final int[] tb = new int[1];
    byte[] window;
    int write;

    InfBlocks(ZStream z, Object checkfn, int w) {
        this.window = new byte[w];
        this.end = w;
        this.checkfn = checkfn;
        this.mode = 0;
        reset(z, null);
    }

    void reset(ZStream z, long[] c) {
        if (c != null) {
            c[0] = this.check;
        }
        this.mode = 0;
        this.bitk = 0;
        this.bitb = 0;
        this.write = 0;
        this.read = 0;
        if (this.checkfn != null) {
            long adler32 = Adler32.adler32(0, null, 0, 0);
            this.check = adler32;
            z.adler = adler32;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    int proc(org.jboss.netty.util.internal.jzlib.ZStream r32, int r33) {
        /*
        r31 = this;
        r0 = r32;
        r0 = r0.next_in_index;
        r27 = r0;
        r0 = r32;
        r0 = r0.avail_in;
        r26 = r0;
        r0 = r31;
        r0 = r0.bitb;
        r19 = r0;
        r0 = r31;
        r0 = r0.bitk;
        r24 = r0;
        r0 = r31;
        r0 = r0.write;
        r29 = r0;
        r0 = r31;
        r4 = r0.read;
        r0 = r29;
        if (r0 >= r4) goto L_0x006a;
    L_0x0026:
        r0 = r31;
        r4 = r0.read;
        r4 = r4 - r29;
        r25 = r4 + -1;
    L_0x002e:
        r0 = r31;
        r4 = r0.mode;
        switch(r4) {
            case 0: goto L_0x094c;
            case 1: goto L_0x0948;
            case 2: goto L_0x0233;
            case 3: goto L_0x0944;
            case 4: goto L_0x0447;
            case 5: goto L_0x056d;
            case 6: goto L_0x07f0;
            case 7: goto L_0x0880;
            case 8: goto L_0x08d4;
            case 9: goto L_0x090a;
            default: goto L_0x0035;
        };
    L_0x0035:
        r33 = -2;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
    L_0x0069:
        return r4;
    L_0x006a:
        r0 = r31;
        r4 = r0.end;
        r25 = r4 - r29;
        goto L_0x002e;
    L_0x0071:
        r4 = 3;
        r0 = r24;
        if (r0 >= r4) goto L_0x00c4;
    L_0x0076:
        if (r26 == 0) goto L_0x008f;
    L_0x0078:
        r33 = 0;
        r26 = r26 + -1;
        r0 = r32;
        r4 = r0.next_in;
        r27 = r28 + 1;
        r4 = r4[r28];
        r4 = r4 & 255;
        r4 = r4 << r24;
        r19 = r19 | r4;
        r24 = r24 + 8;
        r28 = r27;
        goto L_0x0071;
    L_0x008f:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x00c4:
        r30 = r19 & 7;
        r4 = r30 & 1;
        r0 = r31;
        r0.last = r4;
        r4 = r30 >>> 1;
        switch(r4) {
            case 0: goto L_0x00d5;
            case 1: goto L_0x00e5;
            case 2: goto L_0x0113;
            case 3: goto L_0x011d;
            default: goto L_0x00d1;
        };
    L_0x00d1:
        r27 = r28;
        goto L_0x002e;
    L_0x00d5:
        r19 = r19 >>> 3;
        r24 = r24 + -3;
        r30 = r24 & 7;
        r19 = r19 >>> r30;
        r24 = r24 - r30;
        r4 = 1;
        r0 = r31;
        r0.mode = r4;
        goto L_0x00d1;
    L_0x00e5:
        r4 = 1;
        r8 = new int[r4];
        r4 = 1;
        r9 = new int[r4];
        r4 = 1;
        r10 = new int[r4][];
        r4 = 1;
        r11 = new int[r4][];
        org.jboss.netty.util.internal.jzlib.InfTree.inflate_trees_fixed(r8, r9, r10, r11);
        r0 = r31;
        r4 = r0.codes;
        r5 = 0;
        r5 = r8[r5];
        r6 = 0;
        r6 = r9[r6];
        r7 = 0;
        r7 = r10[r7];
        r8 = 0;
        r12 = 0;
        r9 = r11[r12];
        r10 = 0;
        r4.init(r5, r6, r7, r8, r9, r10);
        r19 = r19 >>> 3;
        r24 = r24 + -3;
        r4 = 6;
        r0 = r31;
        r0.mode = r4;
        goto L_0x00d1;
    L_0x0113:
        r19 = r19 >>> 3;
        r24 = r24 + -3;
        r4 = 3;
        r0 = r31;
        r0.mode = r4;
        goto L_0x00d1;
    L_0x011d:
        r19 = r19 >>> 3;
        r24 = r24 + -3;
        r4 = 9;
        r0 = r31;
        r0.mode = r4;
        r4 = "invalid block type";
        r0 = r32;
        r0.msg = r4;
        r33 = -3;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x0165:
        r4 = 32;
        r0 = r24;
        if (r0 >= r4) goto L_0x01ba;
    L_0x016b:
        if (r26 == 0) goto L_0x0184;
    L_0x016d:
        r33 = 0;
        r26 = r26 + -1;
        r0 = r32;
        r4 = r0.next_in;
        r27 = r28 + 1;
        r4 = r4[r28];
        r4 = r4 & 255;
        r4 = r4 << r24;
        r19 = r19 | r4;
        r24 = r24 + 8;
        r28 = r27;
        goto L_0x0165;
    L_0x0184:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x01ba:
        r4 = r19 ^ -1;
        r4 = r4 >>> 16;
        r5 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r4 = r4 & r5;
        r5 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r5 = r5 & r19;
        if (r4 == r5) goto L_0x020d;
    L_0x01c9:
        r4 = 9;
        r0 = r31;
        r0.mode = r4;
        r4 = "invalid stored block lengths";
        r0 = r32;
        r0.msg = r4;
        r33 = -3;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x020d:
        r4 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r4 = r4 & r19;
        r0 = r31;
        r0.left = r4;
        r24 = 0;
        r19 = r24;
        r0 = r31;
        r4 = r0.left;
        if (r4 == 0) goto L_0x0229;
    L_0x0220:
        r4 = 2;
    L_0x0221:
        r0 = r31;
        r0.mode = r4;
        r27 = r28;
        goto L_0x002e;
    L_0x0229:
        r0 = r31;
        r4 = r0.last;
        if (r4 == 0) goto L_0x0231;
    L_0x022f:
        r4 = 7;
        goto L_0x0221;
    L_0x0231:
        r4 = 0;
        goto L_0x0221;
    L_0x0233:
        if (r26 != 0) goto L_0x0268;
    L_0x0235:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r4 = 0;
        r0 = r32;
        r0.avail_in = r4;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x0268:
        if (r25 != 0) goto L_0x0317;
    L_0x026a:
        r0 = r31;
        r4 = r0.end;
        r0 = r29;
        if (r0 != r4) goto L_0x028a;
    L_0x0272:
        r0 = r31;
        r4 = r0.read;
        if (r4 == 0) goto L_0x028a;
    L_0x0278:
        r29 = 0;
        r0 = r31;
        r4 = r0.read;
        r0 = r29;
        if (r0 >= r4) goto L_0x0302;
    L_0x0282:
        r0 = r31;
        r4 = r0.read;
        r4 = r4 - r29;
        r25 = r4 + -1;
    L_0x028a:
        if (r25 != 0) goto L_0x0317;
    L_0x028c:
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r33 = r31.inflate_flush(r32, r33);
        r0 = r31;
        r0 = r0.write;
        r29 = r0;
        r0 = r31;
        r4 = r0.read;
        r0 = r29;
        if (r0 >= r4) goto L_0x0309;
    L_0x02a4:
        r0 = r31;
        r4 = r0.read;
        r4 = r4 - r29;
        r25 = r4 + -1;
    L_0x02ac:
        r0 = r31;
        r4 = r0.end;
        r0 = r29;
        if (r0 != r4) goto L_0x02cc;
    L_0x02b4:
        r0 = r31;
        r4 = r0.read;
        if (r4 == 0) goto L_0x02cc;
    L_0x02ba:
        r29 = 0;
        r0 = r31;
        r4 = r0.read;
        r0 = r29;
        if (r0 >= r4) goto L_0x0310;
    L_0x02c4:
        r0 = r31;
        r4 = r0.read;
        r4 = r4 - r29;
        r25 = r4 + -1;
    L_0x02cc:
        if (r25 != 0) goto L_0x0317;
    L_0x02ce:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x0302:
        r0 = r31;
        r4 = r0.end;
        r25 = r4 - r29;
        goto L_0x028a;
    L_0x0309:
        r0 = r31;
        r4 = r0.end;
        r25 = r4 - r29;
        goto L_0x02ac;
    L_0x0310:
        r0 = r31;
        r4 = r0.end;
        r25 = r4 - r29;
        goto L_0x02cc;
    L_0x0317:
        r33 = 0;
        r0 = r31;
        r0 = r0.left;
        r30 = r0;
        r0 = r30;
        r1 = r26;
        if (r0 <= r1) goto L_0x0327;
    L_0x0325:
        r30 = r26;
    L_0x0327:
        r0 = r30;
        r1 = r25;
        if (r0 <= r1) goto L_0x032f;
    L_0x032d:
        r30 = r25;
    L_0x032f:
        r0 = r32;
        r4 = r0.next_in;
        r0 = r31;
        r5 = r0.window;
        r0 = r27;
        r1 = r29;
        r2 = r30;
        java.lang.System.arraycopy(r4, r0, r5, r1, r2);
        r27 = r27 + r30;
        r26 = r26 - r30;
        r29 = r29 + r30;
        r25 = r25 - r30;
        r0 = r31;
        r4 = r0.left;
        r4 = r4 - r30;
        r0 = r31;
        r0.left = r4;
        if (r4 != 0) goto L_0x002e;
    L_0x0354:
        r0 = r31;
        r4 = r0.last;
        if (r4 == 0) goto L_0x0361;
    L_0x035a:
        r4 = 7;
    L_0x035b:
        r0 = r31;
        r0.mode = r4;
        goto L_0x002e;
    L_0x0361:
        r4 = 0;
        goto L_0x035b;
    L_0x0363:
        r4 = 14;
        r0 = r24;
        if (r0 >= r4) goto L_0x03b8;
    L_0x0369:
        if (r26 == 0) goto L_0x0382;
    L_0x036b:
        r33 = 0;
        r26 = r26 + -1;
        r0 = r32;
        r4 = r0.next_in;
        r27 = r28 + 1;
        r4 = r4[r28];
        r4 = r4 & 255;
        r4 = r4 << r24;
        r19 = r19 | r4;
        r24 = r24 + 8;
        r28 = r27;
        goto L_0x0363;
    L_0x0382:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x03b8:
        r0 = r19;
        r0 = r0 & 16383;
        r30 = r0;
        r0 = r30;
        r1 = r31;
        r1.table = r0;
        r4 = r30 & 31;
        r5 = 29;
        if (r4 > r5) goto L_0x03d2;
    L_0x03ca:
        r4 = r30 >> 5;
        r4 = r4 & 31;
        r5 = 29;
        if (r4 <= r5) goto L_0x0416;
    L_0x03d2:
        r4 = 9;
        r0 = r31;
        r0.mode = r4;
        r4 = "too many length or distance symbols";
        r0 = r32;
        r0.msg = r4;
        r33 = -3;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x0416:
        r4 = r30 & 31;
        r4 = r4 + 258;
        r5 = r30 >> 5;
        r5 = r5 & 31;
        r30 = r4 + r5;
        r0 = r31;
        r4 = r0.blens;
        if (r4 == 0) goto L_0x042f;
    L_0x0426:
        r0 = r31;
        r4 = r0.blens;
        r4 = r4.length;
        r0 = r30;
        if (r4 >= r0) goto L_0x0475;
    L_0x042f:
        r0 = r30;
        r4 = new int[r0];
        r0 = r31;
        r0.blens = r4;
    L_0x0437:
        r19 = r19 >>> 14;
        r24 = r24 + -14;
        r4 = 0;
        r0 = r31;
        r0.index = r4;
        r4 = 4;
        r0 = r31;
        r0.mode = r4;
        r27 = r28;
    L_0x0447:
        r0 = r31;
        r4 = r0.index;
        r0 = r31;
        r5 = r0.table;
        r5 = r5 >>> 10;
        r5 = r5 + 4;
        if (r4 >= r5) goto L_0x04db;
    L_0x0455:
        r28 = r27;
    L_0x0457:
        r4 = 3;
        r0 = r24;
        if (r0 >= r4) goto L_0x04bd;
    L_0x045c:
        if (r26 == 0) goto L_0x0487;
    L_0x045e:
        r33 = 0;
        r26 = r26 + -1;
        r0 = r32;
        r4 = r0.next_in;
        r27 = r28 + 1;
        r4 = r4[r28];
        r4 = r4 & 255;
        r4 = r4 << r24;
        r19 = r19 | r4;
        r24 = r24 + 8;
        r28 = r27;
        goto L_0x0457;
    L_0x0475:
        r21 = 0;
    L_0x0477:
        r0 = r21;
        r1 = r30;
        if (r0 >= r1) goto L_0x0437;
    L_0x047d:
        r0 = r31;
        r4 = r0.blens;
        r5 = 0;
        r4[r21] = r5;
        r21 = r21 + 1;
        goto L_0x0477;
    L_0x0487:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x04bd:
        r0 = r31;
        r4 = r0.blens;
        r5 = border;
        r0 = r31;
        r6 = r0.index;
        r7 = r6 + 1;
        r0 = r31;
        r0.index = r7;
        r5 = r5[r6];
        r6 = r19 & 7;
        r4[r5] = r6;
        r19 = r19 >>> 3;
        r24 = r24 + -3;
        r27 = r28;
        goto L_0x0447;
    L_0x04db:
        r0 = r31;
        r4 = r0.index;
        r5 = 19;
        if (r4 >= r5) goto L_0x04f9;
    L_0x04e3:
        r0 = r31;
        r4 = r0.blens;
        r5 = border;
        r0 = r31;
        r6 = r0.index;
        r7 = r6 + 1;
        r0 = r31;
        r0.index = r7;
        r5 = r5[r6];
        r6 = 0;
        r4[r5] = r6;
        goto L_0x04db;
    L_0x04f9:
        r0 = r31;
        r4 = r0.bb;
        r5 = 0;
        r6 = 7;
        r4[r5] = r6;
        r0 = r31;
        r4 = r0.inftree;
        r0 = r31;
        r5 = r0.blens;
        r0 = r31;
        r6 = r0.bb;
        r0 = r31;
        r7 = r0.tb;
        r0 = r31;
        r8 = r0.hufts;
        r9 = r32;
        r30 = r4.inflate_trees_bits(r5, r6, r7, r8, r9);
        if (r30 == 0) goto L_0x0563;
    L_0x051d:
        r33 = r30;
        r4 = -3;
        r0 = r33;
        if (r0 != r4) goto L_0x052f;
    L_0x0524:
        r4 = 0;
        r0 = r31;
        r0.blens = r4;
        r4 = 9;
        r0 = r31;
        r0.mode = r4;
    L_0x052f:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x0563:
        r4 = 0;
        r0 = r31;
        r0.index = r4;
        r4 = 5;
        r0 = r31;
        r0.mode = r4;
    L_0x056d:
        r0 = r31;
        r0 = r0.table;
        r30 = r0;
        r0 = r31;
        r4 = r0.index;
        r5 = r30 & 31;
        r5 = r5 + 258;
        r6 = r30 >> 5;
        r6 = r6 & 31;
        r5 = r5 + r6;
        if (r4 < r5) goto L_0x0609;
    L_0x0582:
        r0 = r31;
        r4 = r0.tb;
        r5 = 0;
        r6 = -1;
        r4[r5] = r6;
        r4 = 1;
        r8 = new int[r4];
        r4 = 1;
        r9 = new int[r4];
        r4 = 1;
        r10 = new int[r4];
        r4 = 1;
        r11 = new int[r4];
        r4 = 0;
        r5 = 9;
        r8[r4] = r5;
        r4 = 0;
        r5 = 6;
        r9[r4] = r5;
        r0 = r31;
        r0 = r0.table;
        r30 = r0;
        r0 = r31;
        r4 = r0.inftree;
        r5 = r30 & 31;
        r5 = r5 + 257;
        r6 = r30 >> 5;
        r6 = r6 & 31;
        r6 = r6 + 1;
        r0 = r31;
        r7 = r0.blens;
        r0 = r31;
        r12 = r0.hufts;
        r13 = r32;
        r30 = r4.inflate_trees_dynamic(r5, r6, r7, r8, r9, r10, r11, r12, r13);
        if (r30 == 0) goto L_0x07ce;
    L_0x05c3:
        r4 = -3;
        r0 = r30;
        if (r0 != r4) goto L_0x05d3;
    L_0x05c8:
        r4 = 0;
        r0 = r31;
        r0.blens = r4;
        r4 = 9;
        r0 = r31;
        r0.mode = r4;
    L_0x05d3:
        r33 = r30;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x0609:
        r0 = r31;
        r4 = r0.bb;
        r5 = 0;
        r30 = r4[r5];
        r28 = r27;
    L_0x0612:
        r0 = r24;
        r1 = r30;
        if (r0 >= r1) goto L_0x0667;
    L_0x0618:
        if (r26 == 0) goto L_0x0631;
    L_0x061a:
        r33 = 0;
        r26 = r26 + -1;
        r0 = r32;
        r4 = r0.next_in;
        r27 = r28 + 1;
        r4 = r4[r28];
        r4 = r4 & 255;
        r4 = r4 << r24;
        r19 = r19 | r4;
        r24 = r24 + 8;
        r28 = r27;
        goto L_0x0612;
    L_0x0631:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x0667:
        r0 = r31;
        r4 = r0.tb;
        r5 = 0;
        r4 = r4[r5];
        r5 = -1;
        if (r4 != r5) goto L_0x0671;
    L_0x0671:
        r0 = r31;
        r4 = r0.hufts;
        r0 = r31;
        r5 = r0.tb;
        r6 = 0;
        r5 = r5[r6];
        r6 = inflate_mask;
        r6 = r6[r30];
        r6 = r6 & r19;
        r5 = r5 + r6;
        r5 = r5 * 3;
        r5 = r5 + 1;
        r30 = r4[r5];
        r0 = r31;
        r4 = r0.hufts;
        r0 = r31;
        r5 = r0.tb;
        r6 = 0;
        r5 = r5[r6];
        r6 = inflate_mask;
        r6 = r6[r30];
        r6 = r6 & r19;
        r5 = r5 + r6;
        r5 = r5 * 3;
        r5 = r5 + 2;
        r20 = r4[r5];
        r4 = 16;
        r0 = r20;
        if (r0 >= r4) goto L_0x06bf;
    L_0x06a7:
        r19 = r19 >>> r30;
        r24 = r24 - r30;
        r0 = r31;
        r4 = r0.blens;
        r0 = r31;
        r5 = r0.index;
        r6 = r5 + 1;
        r0 = r31;
        r0.index = r6;
        r4[r5] = r20;
        r27 = r28;
        goto L_0x056d;
    L_0x06bf:
        r4 = 18;
        r0 = r20;
        if (r0 != r4) goto L_0x06ee;
    L_0x06c5:
        r21 = 7;
    L_0x06c7:
        r4 = 18;
        r0 = r20;
        if (r0 != r4) goto L_0x06f1;
    L_0x06cd:
        r23 = 11;
    L_0x06cf:
        r4 = r30 + r21;
        r0 = r24;
        if (r0 >= r4) goto L_0x072a;
    L_0x06d5:
        if (r26 == 0) goto L_0x06f4;
    L_0x06d7:
        r33 = 0;
        r26 = r26 + -1;
        r0 = r32;
        r4 = r0.next_in;
        r27 = r28 + 1;
        r4 = r4[r28];
        r4 = r4 & 255;
        r4 = r4 << r24;
        r19 = r19 | r4;
        r24 = r24 + 8;
        r28 = r27;
        goto L_0x06cf;
    L_0x06ee:
        r21 = r20 + -14;
        goto L_0x06c7;
    L_0x06f1:
        r23 = 3;
        goto L_0x06cf;
    L_0x06f4:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x072a:
        r19 = r19 >>> r30;
        r24 = r24 - r30;
        r4 = inflate_mask;
        r4 = r4[r21];
        r4 = r4 & r19;
        r23 = r23 + r4;
        r19 = r19 >>> r21;
        r24 = r24 - r21;
        r0 = r31;
        r0 = r0.index;
        r21 = r0;
        r0 = r31;
        r0 = r0.table;
        r30 = r0;
        r4 = r21 + r23;
        r5 = r30 & 31;
        r5 = r5 + 258;
        r6 = r30 >> 5;
        r6 = r6 & 31;
        r5 = r5 + r6;
        if (r4 > r5) goto L_0x075e;
    L_0x0753:
        r4 = 16;
        r0 = r20;
        if (r0 != r4) goto L_0x07a7;
    L_0x0759:
        r4 = 1;
        r0 = r21;
        if (r0 >= r4) goto L_0x07a7;
    L_0x075e:
        r4 = 0;
        r0 = r31;
        r0.blens = r4;
        r4 = 9;
        r0 = r31;
        r0.mode = r4;
        r4 = "invalid bit length repeat";
        r0 = r32;
        r0.msg = r4;
        r33 = -3;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r28 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r28;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        r27 = r28;
        goto L_0x0069;
    L_0x07a7:
        r4 = 16;
        r0 = r20;
        if (r0 != r4) goto L_0x07cb;
    L_0x07ad:
        r0 = r31;
        r4 = r0.blens;
        r5 = r21 + -1;
        r20 = r4[r5];
    L_0x07b5:
        r0 = r31;
        r4 = r0.blens;
        r22 = r21 + 1;
        r4[r21] = r20;
        r23 = r23 + -1;
        if (r23 != 0) goto L_0x0940;
    L_0x07c1:
        r0 = r22;
        r1 = r31;
        r1.index = r0;
        r27 = r28;
        goto L_0x056d;
    L_0x07cb:
        r20 = 0;
        goto L_0x07b5;
    L_0x07ce:
        r0 = r31;
        r12 = r0.codes;
        r4 = 0;
        r13 = r8[r4];
        r4 = 0;
        r14 = r9[r4];
        r0 = r31;
        r15 = r0.hufts;
        r4 = 0;
        r16 = r10[r4];
        r0 = r31;
        r0 = r0.hufts;
        r17 = r0;
        r4 = 0;
        r18 = r11[r4];
        r12.init(r13, r14, r15, r16, r17, r18);
        r4 = 6;
        r0 = r31;
        r0.mode = r4;
    L_0x07f0:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r0 = r31;
        r4 = r0.codes;
        r0 = r31;
        r1 = r32;
        r2 = r33;
        r33 = r4.proc(r0, r1, r2);
        r4 = 1;
        r0 = r33;
        if (r0 == r4) goto L_0x0837;
    L_0x0831:
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x0837:
        r33 = 0;
        r0 = r32;
        r0 = r0.next_in_index;
        r27 = r0;
        r0 = r32;
        r0 = r0.avail_in;
        r26 = r0;
        r0 = r31;
        r0 = r0.bitb;
        r19 = r0;
        r0 = r31;
        r0 = r0.bitk;
        r24 = r0;
        r0 = r31;
        r0 = r0.write;
        r29 = r0;
        r0 = r31;
        r4 = r0.read;
        r0 = r29;
        if (r0 >= r4) goto L_0x0874;
    L_0x085f:
        r0 = r31;
        r4 = r0.read;
        r4 = r4 - r29;
        r25 = r4 + -1;
    L_0x0867:
        r0 = r31;
        r4 = r0.last;
        if (r4 != 0) goto L_0x087b;
    L_0x086d:
        r4 = 0;
        r0 = r31;
        r0.mode = r4;
        goto L_0x002e;
    L_0x0874:
        r0 = r31;
        r4 = r0.end;
        r25 = r4 - r29;
        goto L_0x0867;
    L_0x087b:
        r4 = 7;
        r0 = r31;
        r0.mode = r4;
    L_0x0880:
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r33 = r31.inflate_flush(r32, r33);
        r0 = r31;
        r0 = r0.write;
        r29 = r0;
        r0 = r31;
        r4 = r0.read;
        r0 = r31;
        r5 = r0.write;
        if (r4 == r5) goto L_0x08ce;
    L_0x089a:
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x08ce:
        r4 = 8;
        r0 = r31;
        r0.mode = r4;
    L_0x08d4:
        r33 = 1;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x090a:
        r33 = -3;
        r0 = r19;
        r1 = r31;
        r1.bitb = r0;
        r0 = r24;
        r1 = r31;
        r1.bitk = r0;
        r0 = r26;
        r1 = r32;
        r1.avail_in = r0;
        r0 = r32;
        r4 = r0.total_in;
        r0 = r32;
        r6 = r0.next_in_index;
        r6 = r27 - r6;
        r6 = (long) r6;
        r4 = r4 + r6;
        r0 = r32;
        r0.total_in = r4;
        r0 = r27;
        r1 = r32;
        r1.next_in_index = r0;
        r0 = r29;
        r1 = r31;
        r1.write = r0;
        r4 = r31.inflate_flush(r32, r33);
        goto L_0x0069;
    L_0x0940:
        r21 = r22;
        goto L_0x07b5;
    L_0x0944:
        r28 = r27;
        goto L_0x0363;
    L_0x0948:
        r28 = r27;
        goto L_0x0165;
    L_0x094c:
        r28 = r27;
        goto L_0x0071;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.util.internal.jzlib.InfBlocks.proc(org.jboss.netty.util.internal.jzlib.ZStream, int):int");
    }

    void free(ZStream z) {
        reset(z, null);
        this.window = null;
        this.hufts = null;
    }

    void set_dictionary(byte[] d, int start, int n) {
        System.arraycopy(d, start, this.window, 0, n);
        this.write = n;
        this.read = n;
    }

    int sync_point() {
        return this.mode == 1 ? 1 : 0;
    }

    int inflate_flush(ZStream z, int r) {
        int p = z.next_out_index;
        int q = this.read;
        int n = (q <= this.write ? this.write : this.end) - q;
        if (n > z.avail_out) {
            n = z.avail_out;
        }
        if (n != 0 && r == -5) {
            r = 0;
        }
        z.avail_out -= n;
        z.total_out += (long) n;
        if (this.checkfn != null) {
            long adler32 = Adler32.adler32(this.check, this.window, q, n);
            this.check = adler32;
            z.adler = adler32;
        }
        System.arraycopy(this.window, q, z.next_out, p, n);
        p += n;
        q += n;
        if (q == this.end) {
            if (this.write == this.end) {
                this.write = 0;
            }
            n = this.write - 0;
            if (n > z.avail_out) {
                n = z.avail_out;
            }
            if (n != 0 && r == -5) {
                r = 0;
            }
            z.avail_out -= n;
            z.total_out += (long) n;
            if (this.checkfn != null) {
                adler32 = Adler32.adler32(this.check, this.window, 0, n);
                this.check = adler32;
                z.adler = adler32;
            }
            System.arraycopy(this.window, 0, z.next_out, p, n);
            p += n;
            q = 0 + n;
        }
        z.next_out_index = p;
        this.read = q;
        return r;
    }
}

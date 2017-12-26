package org.jboss.netty.util.internal.jzlib;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import org.apache.commons.compress.archivers.zip.UnixStat;

final class InfCodes {
    private static final int BADCODE = 9;
    private static final int COPY = 5;
    private static final int DIST = 3;
    private static final int DISTEXT = 4;
    private static final int END = 8;
    private static final int LEN = 1;
    private static final int LENEXT = 2;
    private static final int LIT = 6;
    private static final int START = 0;
    private static final int WASH = 7;
    private static final int[] inflate_mask = new int[]{0, 1, 3, 7, 15, 31, 63, TransportMediator.KEYCODE_MEDIA_PAUSE, 255, UnixStat.DEFAULT_LINK_PERM, 1023, 2047, UnixStat.PERM_MASK, 8191, 16383, 32767, SupportMenu.USER_MASK};
    private byte dbits;
    private int dist;
    private int[] dtree;
    private int dtree_index;
    private int get;
    private byte lbits;
    private int len;
    private int lit;
    private int[] ltree;
    private int ltree_index;
    private int mode;
    private int need;
    private int[] tree;
    private int tree_index;

    InfCodes() {
    }

    void init(int bl, int bd, int[] tl, int tl_index, int[] td, int td_index) {
        this.mode = 0;
        this.lbits = (byte) bl;
        this.dbits = (byte) bd;
        this.ltree = tl;
        this.ltree_index = tl_index;
        this.dtree = td;
        this.dtree_index = td_index;
        this.tree = null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    int proc(org.jboss.netty.util.internal.jzlib.InfBlocks r24, org.jboss.netty.util.internal.jzlib.ZStream r25, int r26) {
        /*
        r23 = this;
        r0 = r25;
        r0 = r0.next_in_index;
        r18 = r0;
        r0 = r25;
        r0 = r0.avail_in;
        r17 = r0;
        r0 = r24;
        r10 = r0.bitb;
        r0 = r24;
        r15 = r0.bitk;
        r0 = r24;
        r0 = r0.write;
        r20 = r0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x0062;
    L_0x0022:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x002a:
        r0 = r23;
        r2 = r0.mode;
        switch(r2) {
            case 0: goto L_0x0069;
            case 1: goto L_0x011e;
            case 2: goto L_0x0231;
            case 3: goto L_0x02b1;
            case 4: goto L_0x03a0;
            case 5: goto L_0x0408;
            case 6: goto L_0x04f3;
            case 7: goto L_0x05b8;
            case 8: goto L_0x0611;
            case 9: goto L_0x0643;
            default: goto L_0x0031;
        };
    L_0x0031:
        r26 = -2;
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r18 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r18;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
    L_0x0061:
        return r2;
    L_0x0062:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x002a;
    L_0x0069:
        r2 = 258; // 0x102 float:3.62E-43 double:1.275E-321;
        r0 = r16;
        if (r0 < r2) goto L_0x0101;
    L_0x006f:
        r2 = 10;
        r0 = r17;
        if (r0 < r2) goto L_0x0101;
    L_0x0075:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r18 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r18;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r0 = r23;
        r2 = r0.lbits;
        r0 = r23;
        r3 = r0.dbits;
        r0 = r23;
        r4 = r0.ltree;
        r0 = r23;
        r5 = r0.ltree_index;
        r0 = r23;
        r6 = r0.dtree;
        r0 = r23;
        r7 = r0.dtree_index;
        r8 = r24;
        r9 = r25;
        r26 = inflate_fast(r2, r3, r4, r5, r6, r7, r8, r9);
        r0 = r25;
        r0 = r0.next_in_index;
        r18 = r0;
        r0 = r25;
        r0 = r0.avail_in;
        r17 = r0;
        r0 = r24;
        r10 = r0.bitb;
        r0 = r24;
        r15 = r0.bitk;
        r0 = r24;
        r0 = r0.write;
        r20 = r0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x00f7;
    L_0x00e1:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x00e9:
        if (r26 == 0) goto L_0x0101;
    L_0x00eb:
        r2 = 1;
        r0 = r26;
        if (r0 != r2) goto L_0x00fe;
    L_0x00f0:
        r2 = 7;
    L_0x00f1:
        r0 = r23;
        r0.mode = r2;
        goto L_0x002a;
    L_0x00f7:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x00e9;
    L_0x00fe:
        r2 = 9;
        goto L_0x00f1;
    L_0x0101:
        r0 = r23;
        r2 = r0.lbits;
        r0 = r23;
        r0.need = r2;
        r0 = r23;
        r2 = r0.ltree;
        r0 = r23;
        r0.tree = r2;
        r0 = r23;
        r2 = r0.ltree_index;
        r0 = r23;
        r0.tree_index = r2;
        r2 = 1;
        r0 = r23;
        r0.mode = r2;
    L_0x011e:
        r0 = r23;
        r14 = r0.need;
        r19 = r18;
    L_0x0124:
        if (r15 >= r14) goto L_0x016f;
    L_0x0126:
        if (r17 == 0) goto L_0x013d;
    L_0x0128:
        r26 = 0;
        r17 = r17 + -1;
        r0 = r25;
        r2 = r0.next_in;
        r18 = r19 + 1;
        r2 = r2[r19];
        r2 = r2 & 255;
        r2 = r2 << r15;
        r10 = r10 | r2;
        r15 = r15 + 8;
        r19 = r18;
        goto L_0x0124;
    L_0x013d:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r19 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r19;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        r18 = r19;
        goto L_0x0061;
    L_0x016f:
        r0 = r23;
        r2 = r0.tree_index;
        r3 = inflate_mask;
        r3 = r3[r14];
        r3 = r3 & r10;
        r2 = r2 + r3;
        r22 = r2 * 3;
        r0 = r23;
        r2 = r0.tree;
        r3 = r22 + 1;
        r2 = r2[r3];
        r10 = r10 >>> r2;
        r0 = r23;
        r2 = r0.tree;
        r3 = r22 + 1;
        r2 = r2[r3];
        r15 = r15 - r2;
        r0 = r23;
        r2 = r0.tree;
        r11 = r2[r22];
        if (r11 != 0) goto L_0x01aa;
    L_0x0195:
        r0 = r23;
        r2 = r0.tree;
        r3 = r22 + 2;
        r2 = r2[r3];
        r0 = r23;
        r0.lit = r2;
        r2 = 6;
        r0 = r23;
        r0.mode = r2;
        r18 = r19;
        goto L_0x002a;
    L_0x01aa:
        r2 = r11 & 16;
        if (r2 == 0) goto L_0x01c9;
    L_0x01ae:
        r2 = r11 & 15;
        r0 = r23;
        r0.get = r2;
        r0 = r23;
        r2 = r0.tree;
        r3 = r22 + 2;
        r2 = r2[r3];
        r0 = r23;
        r0.len = r2;
        r2 = 2;
        r0 = r23;
        r0.mode = r2;
        r18 = r19;
        goto L_0x002a;
    L_0x01c9:
        r2 = r11 & 64;
        if (r2 != 0) goto L_0x01e4;
    L_0x01cd:
        r0 = r23;
        r0.need = r11;
        r2 = r22 / 3;
        r0 = r23;
        r3 = r0.tree;
        r4 = r22 + 2;
        r3 = r3[r4];
        r2 = r2 + r3;
        r0 = r23;
        r0.tree_index = r2;
        r18 = r19;
        goto L_0x002a;
    L_0x01e4:
        r2 = r11 & 32;
        if (r2 == 0) goto L_0x01f1;
    L_0x01e8:
        r2 = 7;
        r0 = r23;
        r0.mode = r2;
        r18 = r19;
        goto L_0x002a;
    L_0x01f1:
        r2 = 9;
        r0 = r23;
        r0.mode = r2;
        r2 = "invalid literal/length code";
        r0 = r25;
        r0.msg = r2;
        r26 = -3;
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r19 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r19;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        r18 = r19;
        goto L_0x0061;
    L_0x0231:
        r0 = r23;
        r14 = r0.get;
        r19 = r18;
    L_0x0237:
        if (r15 >= r14) goto L_0x0282;
    L_0x0239:
        if (r17 == 0) goto L_0x0250;
    L_0x023b:
        r26 = 0;
        r17 = r17 + -1;
        r0 = r25;
        r2 = r0.next_in;
        r18 = r19 + 1;
        r2 = r2[r19];
        r2 = r2 & 255;
        r2 = r2 << r15;
        r10 = r10 | r2;
        r15 = r15 + 8;
        r19 = r18;
        goto L_0x0237;
    L_0x0250:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r19 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r19;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        r18 = r19;
        goto L_0x0061;
    L_0x0282:
        r0 = r23;
        r2 = r0.len;
        r3 = inflate_mask;
        r3 = r3[r14];
        r3 = r3 & r10;
        r2 = r2 + r3;
        r0 = r23;
        r0.len = r2;
        r10 = r10 >> r14;
        r15 = r15 - r14;
        r0 = r23;
        r2 = r0.dbits;
        r0 = r23;
        r0.need = r2;
        r0 = r23;
        r2 = r0.dtree;
        r0 = r23;
        r0.tree = r2;
        r0 = r23;
        r2 = r0.dtree_index;
        r0 = r23;
        r0.tree_index = r2;
        r2 = 3;
        r0 = r23;
        r0.mode = r2;
        r18 = r19;
    L_0x02b1:
        r0 = r23;
        r14 = r0.need;
        r19 = r18;
    L_0x02b7:
        if (r15 >= r14) goto L_0x0302;
    L_0x02b9:
        if (r17 == 0) goto L_0x02d0;
    L_0x02bb:
        r26 = 0;
        r17 = r17 + -1;
        r0 = r25;
        r2 = r0.next_in;
        r18 = r19 + 1;
        r2 = r2[r19];
        r2 = r2 & 255;
        r2 = r2 << r15;
        r10 = r10 | r2;
        r15 = r15 + 8;
        r19 = r18;
        goto L_0x02b7;
    L_0x02d0:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r19 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r19;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        r18 = r19;
        goto L_0x0061;
    L_0x0302:
        r0 = r23;
        r2 = r0.tree_index;
        r3 = inflate_mask;
        r3 = r3[r14];
        r3 = r3 & r10;
        r2 = r2 + r3;
        r22 = r2 * 3;
        r0 = r23;
        r2 = r0.tree;
        r3 = r22 + 1;
        r2 = r2[r3];
        r10 = r10 >> r2;
        r0 = r23;
        r2 = r0.tree;
        r3 = r22 + 1;
        r2 = r2[r3];
        r15 = r15 - r2;
        r0 = r23;
        r2 = r0.tree;
        r11 = r2[r22];
        r2 = r11 & 16;
        if (r2 == 0) goto L_0x0345;
    L_0x032a:
        r2 = r11 & 15;
        r0 = r23;
        r0.get = r2;
        r0 = r23;
        r2 = r0.tree;
        r3 = r22 + 2;
        r2 = r2[r3];
        r0 = r23;
        r0.dist = r2;
        r2 = 4;
        r0 = r23;
        r0.mode = r2;
        r18 = r19;
        goto L_0x002a;
    L_0x0345:
        r2 = r11 & 64;
        if (r2 != 0) goto L_0x0360;
    L_0x0349:
        r0 = r23;
        r0.need = r11;
        r2 = r22 / 3;
        r0 = r23;
        r3 = r0.tree;
        r4 = r22 + 2;
        r3 = r3[r4];
        r2 = r2 + r3;
        r0 = r23;
        r0.tree_index = r2;
        r18 = r19;
        goto L_0x002a;
    L_0x0360:
        r2 = 9;
        r0 = r23;
        r0.mode = r2;
        r2 = "invalid distance code";
        r0 = r25;
        r0.msg = r2;
        r26 = -3;
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r19 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r19;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        r18 = r19;
        goto L_0x0061;
    L_0x03a0:
        r0 = r23;
        r14 = r0.get;
        r19 = r18;
    L_0x03a6:
        if (r15 >= r14) goto L_0x03f1;
    L_0x03a8:
        if (r17 == 0) goto L_0x03bf;
    L_0x03aa:
        r26 = 0;
        r17 = r17 + -1;
        r0 = r25;
        r2 = r0.next_in;
        r18 = r19 + 1;
        r2 = r2[r19];
        r2 = r2 & 255;
        r2 = r2 << r15;
        r10 = r10 | r2;
        r15 = r15 + 8;
        r19 = r18;
        goto L_0x03a6;
    L_0x03bf:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r19 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r19;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        r18 = r19;
        goto L_0x0061;
    L_0x03f1:
        r0 = r23;
        r2 = r0.dist;
        r3 = inflate_mask;
        r3 = r3[r14];
        r3 = r3 & r10;
        r2 = r2 + r3;
        r0 = r23;
        r0.dist = r2;
        r10 = r10 >> r14;
        r15 = r15 - r14;
        r2 = 5;
        r0 = r23;
        r0.mode = r2;
        r18 = r19;
    L_0x0408:
        r0 = r23;
        r2 = r0.dist;
        r12 = r20 - r2;
    L_0x040e:
        if (r12 >= 0) goto L_0x043b;
    L_0x0410:
        r0 = r24;
        r2 = r0.end;
        r12 = r12 + r2;
        goto L_0x040e;
    L_0x0416:
        r0 = r24;
        r2 = r0.window;
        r21 = r20 + 1;
        r0 = r24;
        r3 = r0.window;
        r13 = r12 + 1;
        r3 = r3[r12];
        r2[r20] = r3;
        r16 = r16 + -1;
        r0 = r24;
        r2 = r0.end;
        if (r13 != r2) goto L_0x0675;
    L_0x042e:
        r12 = 0;
    L_0x042f:
        r0 = r23;
        r2 = r0.len;
        r2 = r2 + -1;
        r0 = r23;
        r0.len = r2;
        r20 = r21;
    L_0x043b:
        r0 = r23;
        r2 = r0.len;
        if (r2 == 0) goto L_0x04ec;
    L_0x0441:
        if (r16 != 0) goto L_0x0416;
    L_0x0443:
        r0 = r24;
        r2 = r0.end;
        r0 = r20;
        if (r0 != r2) goto L_0x0463;
    L_0x044b:
        r0 = r24;
        r2 = r0.read;
        if (r2 == 0) goto L_0x0463;
    L_0x0451:
        r20 = 0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x04d7;
    L_0x045b:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x0463:
        if (r16 != 0) goto L_0x0416;
    L_0x0465:
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r26 = r24.inflate_flush(r25, r26);
        r0 = r24;
        r0 = r0.write;
        r20 = r0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x04de;
    L_0x047d:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x0485:
        r0 = r24;
        r2 = r0.end;
        r0 = r20;
        if (r0 != r2) goto L_0x04a5;
    L_0x048d:
        r0 = r24;
        r2 = r0.read;
        if (r2 == 0) goto L_0x04a5;
    L_0x0493:
        r20 = 0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x04e5;
    L_0x049d:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x04a5:
        if (r16 != 0) goto L_0x0416;
    L_0x04a7:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r18 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r18;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        goto L_0x0061;
    L_0x04d7:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x0463;
    L_0x04de:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x0485;
    L_0x04e5:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x04a5;
    L_0x04ec:
        r2 = 0;
        r0 = r23;
        r0.mode = r2;
        goto L_0x002a;
    L_0x04f3:
        if (r16 != 0) goto L_0x059e;
    L_0x04f5:
        r0 = r24;
        r2 = r0.end;
        r0 = r20;
        if (r0 != r2) goto L_0x0515;
    L_0x04fd:
        r0 = r24;
        r2 = r0.read;
        if (r2 == 0) goto L_0x0515;
    L_0x0503:
        r20 = 0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x0589;
    L_0x050d:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x0515:
        if (r16 != 0) goto L_0x059e;
    L_0x0517:
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r26 = r24.inflate_flush(r25, r26);
        r0 = r24;
        r0 = r0.write;
        r20 = r0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x0590;
    L_0x052f:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x0537:
        r0 = r24;
        r2 = r0.end;
        r0 = r20;
        if (r0 != r2) goto L_0x0557;
    L_0x053f:
        r0 = r24;
        r2 = r0.read;
        if (r2 == 0) goto L_0x0557;
    L_0x0545:
        r20 = 0;
        r0 = r24;
        r2 = r0.read;
        r0 = r20;
        if (r0 >= r2) goto L_0x0597;
    L_0x054f:
        r0 = r24;
        r2 = r0.read;
        r2 = r2 - r20;
        r16 = r2 + -1;
    L_0x0557:
        if (r16 != 0) goto L_0x059e;
    L_0x0559:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r18 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r18;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        goto L_0x0061;
    L_0x0589:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x0515;
    L_0x0590:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x0537;
    L_0x0597:
        r0 = r24;
        r2 = r0.end;
        r16 = r2 - r20;
        goto L_0x0557;
    L_0x059e:
        r26 = 0;
        r0 = r24;
        r2 = r0.window;
        r21 = r20 + 1;
        r0 = r23;
        r3 = r0.lit;
        r3 = (byte) r3;
        r2[r20] = r3;
        r16 = r16 + -1;
        r2 = 0;
        r0 = r23;
        r0.mode = r2;
        r20 = r21;
        goto L_0x002a;
    L_0x05b8:
        r2 = 7;
        if (r15 <= r2) goto L_0x05c1;
    L_0x05bb:
        r15 = r15 + -8;
        r17 = r17 + 1;
        r18 = r18 + -1;
    L_0x05c1:
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r26 = r24.inflate_flush(r25, r26);
        r0 = r24;
        r0 = r0.write;
        r20 = r0;
        r0 = r24;
        r2 = r0.read;
        r0 = r24;
        r3 = r0.write;
        if (r2 == r3) goto L_0x060b;
    L_0x05db:
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r18 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r18;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        goto L_0x0061;
    L_0x060b:
        r2 = 8;
        r0 = r23;
        r0.mode = r2;
    L_0x0611:
        r26 = 1;
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r18 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r18;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        goto L_0x0061;
    L_0x0643:
        r26 = -3;
        r0 = r24;
        r0.bitb = r10;
        r0 = r24;
        r0.bitk = r15;
        r0 = r17;
        r1 = r25;
        r1.avail_in = r0;
        r0 = r25;
        r2 = r0.total_in;
        r0 = r25;
        r4 = r0.next_in_index;
        r4 = r18 - r4;
        r4 = (long) r4;
        r2 = r2 + r4;
        r0 = r25;
        r0.total_in = r2;
        r0 = r18;
        r1 = r25;
        r1.next_in_index = r0;
        r0 = r20;
        r1 = r24;
        r1.write = r0;
        r2 = r24.inflate_flush(r25, r26);
        goto L_0x0061;
    L_0x0675:
        r12 = r13;
        goto L_0x042f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.util.internal.jzlib.InfCodes.proc(org.jboss.netty.util.internal.jzlib.InfBlocks, org.jboss.netty.util.internal.jzlib.ZStream, int):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int inflate_fast(int r30, int r31, int[] r32, int r33, int[] r34, int r35, org.jboss.netty.util.internal.jzlib.InfBlocks r36, org.jboss.netty.util.internal.jzlib.ZStream r37) {
        /*
        r0 = r37;
        r15 = r0.next_in_index;
        r0 = r37;
        r14 = r0.avail_in;
        r0 = r36;
        r6 = r0.bitb;
        r0 = r36;
        r10 = r0.bitk;
        r0 = r36;
        r0 = r0.write;
        r17 = r0;
        r0 = r36;
        r0 = r0.read;
        r25 = r0;
        r0 = r17;
        r1 = r25;
        if (r0 >= r1) goto L_0x0059;
    L_0x0022:
        r0 = r36;
        r0 = r0.read;
        r25 = r0;
        r25 = r25 - r17;
        r11 = r25 + -1;
    L_0x002c:
        r25 = inflate_mask;
        r13 = r25[r30];
        r25 = inflate_mask;
        r12 = r25[r31];
        r18 = r17;
        r16 = r15;
    L_0x0038:
        r25 = 20;
        r0 = r25;
        if (r10 >= r0) goto L_0x0062;
    L_0x003e:
        r14 = r14 + -1;
        r0 = r37;
        r0 = r0.next_in;
        r25 = r0;
        r15 = r16 + 1;
        r25 = r25[r16];
        r0 = r25;
        r0 = r0 & 255;
        r25 = r0;
        r25 = r25 << r10;
        r6 = r6 | r25;
        r10 = r10 + 8;
        r16 = r15;
        goto L_0x0038;
    L_0x0059:
        r0 = r36;
        r0 = r0.end;
        r25 = r0;
        r11 = r25 - r17;
        goto L_0x002c;
    L_0x0062:
        r21 = r6 & r13;
        r22 = r32;
        r23 = r33;
        r25 = r23 + r21;
        r24 = r25 * 3;
        r9 = r22[r24];
        if (r9 != 0) goto L_0x00e9;
    L_0x0070:
        r25 = r24 + 1;
        r25 = r22[r25];
        r6 = r6 >> r25;
        r25 = r24 + 1;
        r25 = r22[r25];
        r10 = r10 - r25;
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r17 = r18 + 1;
        r26 = r24 + 2;
        r26 = r22[r26];
        r0 = r26;
        r0 = (byte) r0;
        r26 = r0;
        r25[r18] = r26;
        r11 = r11 + -1;
        r15 = r16;
    L_0x0093:
        r25 = 258; // 0x102 float:3.62E-43 double:1.275E-321;
        r0 = r25;
        if (r11 < r0) goto L_0x009f;
    L_0x0099:
        r25 = 10;
        r0 = r25;
        if (r14 >= r0) goto L_0x03d3;
    L_0x009f:
        r0 = r37;
        r0 = r0.avail_in;
        r25 = r0;
        r7 = r25 - r14;
        r25 = r10 >> 3;
        r0 = r25;
        if (r0 >= r7) goto L_0x00af;
    L_0x00ad:
        r7 = r10 >> 3;
    L_0x00af:
        r14 = r14 + r7;
        r15 = r15 - r7;
        r25 = r7 << 3;
        r10 = r10 - r25;
        r0 = r36;
        r0.bitb = r6;
        r0 = r36;
        r0.bitk = r10;
        r0 = r37;
        r0.avail_in = r14;
        r0 = r37;
        r0 = r0.total_in;
        r26 = r0;
        r0 = r37;
        r0 = r0.next_in_index;
        r25 = r0;
        r25 = r15 - r25;
        r0 = r25;
        r0 = (long) r0;
        r28 = r0;
        r26 = r26 + r28;
        r0 = r26;
        r2 = r37;
        r2.total_in = r0;
        r0 = r37;
        r0.next_in_index = r15;
        r0 = r17;
        r1 = r36;
        r1.write = r0;
        r25 = 0;
    L_0x00e8:
        return r25;
    L_0x00e9:
        r25 = r24 + 1;
        r25 = r22[r25];
        r6 = r6 >> r25;
        r25 = r24 + 1;
        r25 = r22[r25];
        r10 = r10 - r25;
        r25 = r9 & 16;
        if (r25 == 0) goto L_0x02ec;
    L_0x00f9:
        r9 = r9 & 15;
        r25 = r24 + 2;
        r25 = r22[r25];
        r26 = inflate_mask;
        r26 = r26[r9];
        r26 = r26 & r6;
        r7 = r25 + r26;
        r6 = r6 >> r9;
        r10 = r10 - r9;
    L_0x0109:
        r25 = 15;
        r0 = r25;
        if (r10 >= r0) goto L_0x012a;
    L_0x010f:
        r14 = r14 + -1;
        r0 = r37;
        r0 = r0.next_in;
        r25 = r0;
        r15 = r16 + 1;
        r25 = r25[r16];
        r0 = r25;
        r0 = r0 & 255;
        r25 = r0;
        r25 = r25 << r10;
        r6 = r6 | r25;
        r10 = r10 + 8;
        r16 = r15;
        goto L_0x0109;
    L_0x012a:
        r21 = r6 & r12;
        r22 = r34;
        r23 = r35;
        r25 = r23 + r21;
        r24 = r25 * 3;
        r9 = r22[r24];
    L_0x0136:
        r25 = r24 + 1;
        r25 = r22[r25];
        r6 = r6 >> r25;
        r25 = r24 + 1;
        r25 = r22[r25];
        r10 = r10 - r25;
        r25 = r9 & 16;
        if (r25 == 0) goto L_0x027c;
    L_0x0146:
        r9 = r9 & 15;
    L_0x0148:
        if (r10 >= r9) goto L_0x0165;
    L_0x014a:
        r14 = r14 + -1;
        r0 = r37;
        r0 = r0.next_in;
        r25 = r0;
        r15 = r16 + 1;
        r25 = r25[r16];
        r0 = r25;
        r0 = r0 & 255;
        r25 = r0;
        r25 = r25 << r10;
        r6 = r6 | r25;
        r10 = r10 + 8;
        r16 = r15;
        goto L_0x0148;
    L_0x0165:
        r25 = r24 + 2;
        r25 = r22[r25];
        r26 = inflate_mask;
        r26 = r26[r9];
        r26 = r26 & r6;
        r8 = r25 + r26;
        r6 = r6 >> r9;
        r10 = r10 - r9;
        r11 = r11 - r7;
        r0 = r18;
        if (r0 < r8) goto L_0x01fe;
    L_0x0178:
        r19 = r18 - r8;
        r25 = r18 - r19;
        if (r25 <= 0) goto L_0x01dc;
    L_0x017e:
        r25 = 2;
        r26 = r18 - r19;
        r0 = r25;
        r1 = r26;
        if (r0 <= r1) goto L_0x01dc;
    L_0x0188:
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r17 = r18 + 1;
        r0 = r36;
        r0 = r0.window;
        r26 = r0;
        r20 = r19 + 1;
        r26 = r26[r19];
        r25[r18] = r26;
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r18 = r17 + 1;
        r0 = r36;
        r0 = r0.window;
        r26 = r0;
        r19 = r20 + 1;
        r26 = r26[r20];
        r25[r17] = r26;
        r7 = r7 + -2;
        r17 = r18;
    L_0x01b4:
        r25 = r17 - r19;
        if (r25 <= 0) goto L_0x025d;
    L_0x01b8:
        r25 = r17 - r19;
        r0 = r25;
        if (r7 <= r0) goto L_0x025d;
    L_0x01be:
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r18 = r17 + 1;
        r0 = r36;
        r0 = r0.window;
        r26 = r0;
        r20 = r19 + 1;
        r26 = r26[r19];
        r25[r17] = r26;
        r7 = r7 + -1;
        if (r7 != 0) goto L_0x03d9;
    L_0x01d6:
        r17 = r18;
        r15 = r16;
        goto L_0x0093;
    L_0x01dc:
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r0 = r36;
        r0 = r0.window;
        r26 = r0;
        r27 = 2;
        r0 = r25;
        r1 = r19;
        r2 = r26;
        r3 = r18;
        r4 = r27;
        java.lang.System.arraycopy(r0, r1, r2, r3, r4);
        r17 = r18 + 2;
        r19 = r19 + 2;
        r7 = r7 + -2;
        goto L_0x01b4;
    L_0x01fe:
        r19 = r18 - r8;
    L_0x0200:
        r0 = r36;
        r0 = r0.end;
        r25 = r0;
        r19 = r19 + r25;
        if (r19 < 0) goto L_0x0200;
    L_0x020a:
        r0 = r36;
        r0 = r0.end;
        r25 = r0;
        r9 = r25 - r19;
        if (r7 <= r9) goto L_0x03e5;
    L_0x0214:
        r7 = r7 - r9;
        r25 = r18 - r19;
        if (r25 <= 0) goto L_0x0241;
    L_0x0219:
        r25 = r18 - r19;
        r0 = r25;
        if (r9 <= r0) goto L_0x0241;
    L_0x021f:
        r17 = r18;
    L_0x0221:
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r18 = r17 + 1;
        r0 = r36;
        r0 = r0.window;
        r26 = r0;
        r20 = r19 + 1;
        r26 = r26[r19];
        r25[r17] = r26;
        r9 = r9 + -1;
        if (r9 != 0) goto L_0x03df;
    L_0x0239:
        r19 = r20;
        r17 = r18;
    L_0x023d:
        r19 = 0;
        goto L_0x01b4;
    L_0x0241:
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r0 = r36;
        r0 = r0.window;
        r26 = r0;
        r0 = r25;
        r1 = r19;
        r2 = r26;
        r3 = r18;
        java.lang.System.arraycopy(r0, r1, r2, r3, r9);
        r17 = r18 + r9;
        r19 = r19 + r9;
        goto L_0x023d;
    L_0x025d:
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r0 = r36;
        r0 = r0.window;
        r26 = r0;
        r0 = r25;
        r1 = r19;
        r2 = r26;
        r3 = r17;
        java.lang.System.arraycopy(r0, r1, r2, r3, r7);
        r17 = r17 + r7;
        r19 = r19 + r7;
        r15 = r16;
        goto L_0x0093;
    L_0x027c:
        r25 = r9 & 64;
        if (r25 != 0) goto L_0x0296;
    L_0x0280:
        r25 = r24 + 2;
        r25 = r22[r25];
        r21 = r21 + r25;
        r25 = inflate_mask;
        r25 = r25[r9];
        r25 = r25 & r6;
        r21 = r21 + r25;
        r25 = r23 + r21;
        r24 = r25 * 3;
        r9 = r22[r24];
        goto L_0x0136;
    L_0x0296:
        r25 = "invalid distance code";
        r0 = r25;
        r1 = r37;
        r1.msg = r0;
        r0 = r37;
        r0 = r0.avail_in;
        r25 = r0;
        r7 = r25 - r14;
        r25 = r10 >> 3;
        r0 = r25;
        if (r0 >= r7) goto L_0x02ae;
    L_0x02ac:
        r7 = r10 >> 3;
    L_0x02ae:
        r14 = r14 + r7;
        r15 = r16 - r7;
        r25 = r7 << 3;
        r10 = r10 - r25;
        r0 = r36;
        r0.bitb = r6;
        r0 = r36;
        r0.bitk = r10;
        r0 = r37;
        r0.avail_in = r14;
        r0 = r37;
        r0 = r0.total_in;
        r26 = r0;
        r0 = r37;
        r0 = r0.next_in_index;
        r25 = r0;
        r25 = r15 - r25;
        r0 = r25;
        r0 = (long) r0;
        r28 = r0;
        r26 = r26 + r28;
        r0 = r26;
        r2 = r37;
        r2.total_in = r0;
        r0 = r37;
        r0.next_in_index = r15;
        r0 = r18;
        r1 = r36;
        r1.write = r0;
        r25 = -3;
        r17 = r18;
        goto L_0x00e8;
    L_0x02ec:
        r25 = r9 & 64;
        if (r25 != 0) goto L_0x032b;
    L_0x02f0:
        r25 = r24 + 2;
        r25 = r22[r25];
        r21 = r21 + r25;
        r25 = inflate_mask;
        r25 = r25[r9];
        r25 = r25 & r6;
        r21 = r21 + r25;
        r25 = r23 + r21;
        r24 = r25 * 3;
        r9 = r22[r24];
        if (r9 != 0) goto L_0x00e9;
    L_0x0306:
        r25 = r24 + 1;
        r25 = r22[r25];
        r6 = r6 >> r25;
        r25 = r24 + 1;
        r25 = r22[r25];
        r10 = r10 - r25;
        r0 = r36;
        r0 = r0.window;
        r25 = r0;
        r17 = r18 + 1;
        r26 = r24 + 2;
        r26 = r22[r26];
        r0 = r26;
        r0 = (byte) r0;
        r26 = r0;
        r25[r18] = r26;
        r11 = r11 + -1;
        r15 = r16;
        goto L_0x0093;
    L_0x032b:
        r25 = r9 & 32;
        if (r25 == 0) goto L_0x037d;
    L_0x032f:
        r0 = r37;
        r0 = r0.avail_in;
        r25 = r0;
        r7 = r25 - r14;
        r25 = r10 >> 3;
        r0 = r25;
        if (r0 >= r7) goto L_0x033f;
    L_0x033d:
        r7 = r10 >> 3;
    L_0x033f:
        r14 = r14 + r7;
        r15 = r16 - r7;
        r25 = r7 << 3;
        r10 = r10 - r25;
        r0 = r36;
        r0.bitb = r6;
        r0 = r36;
        r0.bitk = r10;
        r0 = r37;
        r0.avail_in = r14;
        r0 = r37;
        r0 = r0.total_in;
        r26 = r0;
        r0 = r37;
        r0 = r0.next_in_index;
        r25 = r0;
        r25 = r15 - r25;
        r0 = r25;
        r0 = (long) r0;
        r28 = r0;
        r26 = r26 + r28;
        r0 = r26;
        r2 = r37;
        r2.total_in = r0;
        r0 = r37;
        r0.next_in_index = r15;
        r0 = r18;
        r1 = r36;
        r1.write = r0;
        r25 = 1;
        r17 = r18;
        goto L_0x00e8;
    L_0x037d:
        r25 = "invalid literal/length code";
        r0 = r25;
        r1 = r37;
        r1.msg = r0;
        r0 = r37;
        r0 = r0.avail_in;
        r25 = r0;
        r7 = r25 - r14;
        r25 = r10 >> 3;
        r0 = r25;
        if (r0 >= r7) goto L_0x0395;
    L_0x0393:
        r7 = r10 >> 3;
    L_0x0395:
        r14 = r14 + r7;
        r15 = r16 - r7;
        r25 = r7 << 3;
        r10 = r10 - r25;
        r0 = r36;
        r0.bitb = r6;
        r0 = r36;
        r0.bitk = r10;
        r0 = r37;
        r0.avail_in = r14;
        r0 = r37;
        r0 = r0.total_in;
        r26 = r0;
        r0 = r37;
        r0 = r0.next_in_index;
        r25 = r0;
        r25 = r15 - r25;
        r0 = r25;
        r0 = (long) r0;
        r28 = r0;
        r26 = r26 + r28;
        r0 = r26;
        r2 = r37;
        r2.total_in = r0;
        r0 = r37;
        r0.next_in_index = r15;
        r0 = r18;
        r1 = r36;
        r1.write = r0;
        r25 = -3;
        r17 = r18;
        goto L_0x00e8;
    L_0x03d3:
        r18 = r17;
        r16 = r15;
        goto L_0x0038;
    L_0x03d9:
        r19 = r20;
        r17 = r18;
        goto L_0x01be;
    L_0x03df:
        r19 = r20;
        r17 = r18;
        goto L_0x0221;
    L_0x03e5:
        r17 = r18;
        goto L_0x01b4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.util.internal.jzlib.InfCodes.inflate_fast(int, int, int[], int, int[], int, org.jboss.netty.util.internal.jzlib.InfBlocks, org.jboss.netty.util.internal.jzlib.ZStream):int");
    }
}

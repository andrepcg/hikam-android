package org.apache.commons.compress.compressors.bzip2;

import android.support.v4.media.session.PlaybackStateCompat;
import java.util.BitSet;

class BlockSort {
    private static final int CLEARMASK = -2097153;
    private static final int DEPTH_THRESH = 10;
    private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
    private static final int FALLBACK_QSORT_STACK_SIZE = 100;
    private static final int[] INCS = new int[]{1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484};
    private static final int QSORT_STACK_SIZE = 1000;
    private static final int SETMASK = 2097152;
    private static final int SMALL_THRESH = 20;
    private static final int STACK_SIZE = 1000;
    private static final int WORK_FACTOR = 30;
    private int[] eclass;
    private boolean firstAttempt;
    private final int[] ftab = new int[65537];
    private final boolean[] mainSort_bigDone = new boolean[256];
    private final int[] mainSort_copy = new int[256];
    private final int[] mainSort_runningOrder = new int[256];
    private final char[] quadrant;
    private final int[] stack_dd = new int[1000];
    private final int[] stack_hh = new int[1000];
    private final int[] stack_ll = new int[1000];
    private int workDone;
    private int workLimit;

    BlockSort(Data data) {
        this.quadrant = data.sfmap;
    }

    void blockSort(Data data, int last) {
        this.workLimit = last * 30;
        this.workDone = 0;
        this.firstAttempt = true;
        if (last + 1 < 10000) {
            fallbackSort(data, last);
        } else {
            mainSort(data, last);
            if (this.firstAttempt && this.workDone > this.workLimit) {
                fallbackSort(data, last);
            }
        }
        int[] fmap = data.fmap;
        data.origPtr = -1;
        for (int i = 0; i <= last; i++) {
            if (fmap[i] == 0) {
                data.origPtr = i;
                return;
            }
        }
    }

    final void fallbackSort(Data data, int last) {
        int i;
        data.block[0] = data.block[last + 1];
        fallbackSort(data.fmap, data.block, last + 1);
        for (i = 0; i < last + 1; i++) {
            int[] iArr = data.fmap;
            iArr[i] = iArr[i] - 1;
        }
        for (i = 0; i < last + 1; i++) {
            if (data.fmap[i] == -1) {
                data.fmap[i] = last;
                return;
            }
        }
    }

    private void fallbackSimpleSort(int[] fmap, int[] eclass, int lo, int hi) {
        if (lo != hi) {
            int i;
            int tmp;
            int ec_tmp;
            int j;
            if (hi - lo > 3) {
                for (i = hi - 4; i >= lo; i--) {
                    tmp = fmap[i];
                    ec_tmp = eclass[tmp];
                    j = i + 4;
                    while (j <= hi && ec_tmp > eclass[fmap[j]]) {
                        fmap[j - 4] = fmap[j];
                        j += 4;
                    }
                    fmap[j - 4] = tmp;
                }
            }
            for (i = hi - 1; i >= lo; i--) {
                tmp = fmap[i];
                ec_tmp = eclass[tmp];
                j = i + 1;
                while (j <= hi && ec_tmp > eclass[fmap[j]]) {
                    fmap[j - 1] = fmap[j];
                    j++;
                }
                fmap[j - 1] = tmp;
            }
        }
    }

    private void fswap(int[] fmap, int zz1, int zz2) {
        int zztmp = fmap[zz1];
        fmap[zz1] = fmap[zz2];
        fmap[zz2] = zztmp;
    }

    private void fvswap(int[] fmap, int yyp1, int yyp2, int yyn) {
        while (yyn > 0) {
            fswap(fmap, yyp1, yyp2);
            yyp1++;
            yyp2++;
            yyn--;
        }
    }

    private int fmin(int a, int b) {
        return a < b ? a : b;
    }

    private void fpush(int sp, int lz, int hz) {
        this.stack_ll[sp] = lz;
        this.stack_hh[sp] = hz;
    }

    private int[] fpop(int sp) {
        return new int[]{this.stack_ll[sp], this.stack_hh[sp]};
    }

    private void fallbackQSort3(int[] fmap, int[] eclass, int loSt, int hiSt) {
        long r = 0;
        int sp = 0 + 1;
        fpush(0, loSt, hiSt);
        int sp2 = sp;
        while (sp2 > 0) {
            sp2--;
            int[] s = fpop(sp2);
            int lo = s[0];
            int hi = s[1];
            if (hi - lo < 10) {
                fallbackSimpleSort(fmap, eclass, lo, hi);
            } else {
                long med;
                int n;
                r = ((7621 * r) + 1) % PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID;
                long r3 = r % 3;
                if (r3 == 0) {
                    med = (long) eclass[fmap[lo]];
                } else if (r3 == 1) {
                    med = (long) eclass[fmap[(lo + hi) >>> 1]];
                } else {
                    med = (long) eclass[fmap[hi]];
                }
                int ltLo = lo;
                int unLo = lo;
                int gtHi = hi;
                int unHi = hi;
                while (true) {
                    if (unLo <= unHi) {
                        n = eclass[fmap[unLo]] - ((int) med);
                        if (n == 0) {
                            fswap(fmap, unLo, ltLo);
                            ltLo++;
                            unLo++;
                        } else if (n <= 0) {
                            unLo++;
                        }
                    }
                    while (unLo <= unHi) {
                        n = eclass[fmap[unHi]] - ((int) med);
                        if (n != 0) {
                            if (n < 0) {
                                break;
                            }
                            unHi--;
                        } else {
                            fswap(fmap, unHi, gtHi);
                            gtHi--;
                            unHi--;
                        }
                    }
                    if (unLo > unHi) {
                        break;
                    }
                    fswap(fmap, unLo, unHi);
                    unLo++;
                    unHi--;
                }
                if (gtHi >= ltLo) {
                    n = fmin(ltLo - lo, unLo - ltLo);
                    fvswap(fmap, lo, unLo - n, n);
                    int m = fmin(hi - gtHi, gtHi - unHi);
                    fvswap(fmap, unHi + 1, (hi - m) + 1, m);
                    n = ((lo + unLo) - ltLo) - 1;
                    m = (hi - (gtHi - unHi)) + 1;
                    if (n - lo > hi - m) {
                        sp = sp2 + 1;
                        fpush(sp2, lo, n);
                        sp2 = sp + 1;
                        fpush(sp, m, hi);
                    } else {
                        sp = sp2 + 1;
                        fpush(sp2, m, hi);
                        sp2 = sp + 1;
                        fpush(sp, lo, n);
                    }
                }
            }
        }
    }

    private int[] getEclass() {
        if (this.eclass == null) {
            this.eclass = new int[(this.quadrant.length / 2)];
        }
        return this.eclass;
    }

    final void fallbackSort(int[] fmap, byte[] block, int nblock) {
        int i;
        int[] ftab = new int[257];
        int[] eclass = getEclass();
        for (i = 0; i < nblock; i++) {
            eclass[i] = 0;
        }
        for (i = 0; i < nblock; i++) {
            int i2 = block[i] & 255;
            ftab[i2] = ftab[i2] + 1;
        }
        for (i = 1; i < 257; i++) {
            ftab[i] = ftab[i] + ftab[i - 1];
        }
        for (i = 0; i < nblock; i++) {
            int j = block[i] & 255;
            int k = ftab[j] - 1;
            ftab[j] = k;
            fmap[k] = i;
        }
        BitSet bhtab = new BitSet(nblock + 64);
        for (i = 0; i < 256; i++) {
            bhtab.set(ftab[i]);
        }
        for (i = 0; i < 32; i++) {
            bhtab.set((i * 2) + nblock);
            bhtab.clear(((i * 2) + nblock) + 1);
        }
        int H = 1;
        int nNotDone;
        do {
            j = 0;
            for (i = 0; i < nblock; i++) {
                if (bhtab.get(i)) {
                    j = i;
                }
                k = fmap[i] - H;
                if (k < 0) {
                    k += nblock;
                }
                eclass[k] = j;
            }
            nNotDone = 0;
            int r = -1;
            while (true) {
                k = bhtab.nextClearBit(r + 1);
                int l = k - 1;
                if (l < nblock) {
                    r = bhtab.nextSetBit(k + 1) - 1;
                    if (r >= nblock) {
                        break;
                    } else if (r > l) {
                        nNotDone += (r - l) + 1;
                        fallbackQSort3(fmap, eclass, l, r);
                        int cc = -1;
                        for (i = l; i <= r; i++) {
                            int cc1 = eclass[fmap[i]];
                            if (cc != cc1) {
                                bhtab.set(i);
                                cc = cc1;
                            }
                        }
                    }
                } else {
                    break;
                }
            }
            H *= 2;
            if (H > nblock) {
                return;
            }
        } while (nNotDone != 0);
    }

    private boolean mainSimpleSort(Data dataShadow, int lo, int hi, int d, int lastShadow) {
        int bigN = (hi - lo) + 1;
        if (bigN >= 2) {
            int hp = 0;
            while (INCS[hp] < bigN) {
                hp++;
            }
            int[] fmap = dataShadow.fmap;
            char[] quadrant = this.quadrant;
            byte[] block = dataShadow.block;
            int lastPlus1 = lastShadow + 1;
            boolean firstAttemptShadow = this.firstAttempt;
            int workLimitShadow = this.workLimit;
            int workDoneShadow = this.workDone;
            loop1:
            while (true) {
                hp--;
                if (hp < 0) {
                    break;
                }
                int h = INCS[hp];
                int mj = (lo + h) - 1;
                int i = lo + h;
                while (i <= hi) {
                    int k = 3;
                    while (i <= hi) {
                        k--;
                        if (k < 0) {
                            break;
                        }
                        int v = fmap[i];
                        int vd = v + d;
                        int j = i;
                        boolean onceRunned = false;
                        int a = 0;
                        while (true) {
                            int i1;
                            int i2;
                            int x;
                            if (!onceRunned) {
                                onceRunned = true;
                                a = fmap[j - h];
                                i1 = a + d;
                                i2 = vd;
                                if (block[i1 + 1] != block[i2 + 1]) {
                                    if (block[i1 + 2] != block[i2 + 2]) {
                                        if (block[i1 + 3] != block[i2 + 3]) {
                                            if (block[i1 + 4] != block[i2 + 4]) {
                                                if (block[i1 + 5] != block[i2 + 5]) {
                                                    i1 += 6;
                                                    i2 += 6;
                                                    if (block[i1] != block[i2]) {
                                                        x = lastShadow;
                                                        while (x > 0) {
                                                            x -= 4;
                                                            if (block[i1 + 1] == block[i2 + 1]) {
                                                                if ((block[i1 + 1] & 255) > (block[i2 + 1] & 255)) {
                                                                    break;
                                                                }
                                                            } else {
                                                                if (quadrant[i1] == quadrant[i2]) {
                                                                    if (quadrant[i1] > quadrant[i2]) {
                                                                        break;
                                                                    }
                                                                } else {
                                                                    if (block[i1 + 2] == block[i2 + 2]) {
                                                                        if ((block[i1 + 2] & 255) > (block[i2 + 2] & 255)) {
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        if (quadrant[i1 + 1] == quadrant[i2 + 1]) {
                                                                            if (quadrant[i1 + 1] > quadrant[i2 + 1]) {
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            if (block[i1 + 3] == block[i2 + 3]) {
                                                                                if ((block[i1 + 3] & 255) > (block[i2 + 3] & 255)) {
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                if (quadrant[i1 + 2] == quadrant[i2 + 2]) {
                                                                                    if (quadrant[i1 + 2] > quadrant[i2 + 2]) {
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    if (block[i1 + 4] == block[i2 + 4]) {
                                                                                        if ((block[i1 + 4] & 255) > (block[i2 + 4] & 255)) {
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        if (quadrant[i1 + 3] == quadrant[i2 + 3]) {
                                                                                            if (quadrant[i1 + 3] > quadrant[i2 + 3]) {
                                                                                                break;
                                                                                            }
                                                                                        } else {
                                                                                            i1 += 4;
                                                                                            if (i1 >= lastPlus1) {
                                                                                                i1 -= lastPlus1;
                                                                                            }
                                                                                            i2 += 4;
                                                                                            if (i2 >= lastPlus1) {
                                                                                                i2 -= lastPlus1;
                                                                                            }
                                                                                            workDoneShadow++;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    if ((block[i1] & 255) > (block[i2] & 255)) {
                                                        break;
                                                    }
                                                } else {
                                                    if ((block[i1 + 5] & 255) > (block[i2 + 5] & 255)) {
                                                        break;
                                                    }
                                                }
                                            } else {
                                                if ((block[i1 + 4] & 255) > (block[i2 + 4] & 255)) {
                                                    break;
                                                }
                                            }
                                        } else {
                                            if ((block[i1 + 3] & 255) > (block[i2 + 3] & 255)) {
                                                break;
                                            }
                                        }
                                    } else {
                                        if ((block[i1 + 2] & 255) > (block[i2 + 2] & 255)) {
                                            break;
                                        }
                                    }
                                } else {
                                    if ((block[i1 + 1] & 255) > (block[i2 + 1] & 255)) {
                                        break;
                                    }
                                }
                            } else {
                                fmap[j] = a;
                                j -= h;
                                if (j <= mj) {
                                    break;
                                }
                                a = fmap[j - h];
                                i1 = a + d;
                                i2 = vd;
                                if (block[i1 + 1] != block[i2 + 1]) {
                                    if ((block[i1 + 1] & 255) > (block[i2 + 1] & 255)) {
                                        break;
                                    }
                                } else {
                                    if (block[i1 + 2] != block[i2 + 2]) {
                                        if ((block[i1 + 2] & 255) > (block[i2 + 2] & 255)) {
                                            break;
                                        }
                                    } else {
                                        if (block[i1 + 3] != block[i2 + 3]) {
                                            if ((block[i1 + 3] & 255) > (block[i2 + 3] & 255)) {
                                                break;
                                            }
                                        } else {
                                            if (block[i1 + 4] != block[i2 + 4]) {
                                                if ((block[i1 + 4] & 255) > (block[i2 + 4] & 255)) {
                                                    break;
                                                }
                                            } else {
                                                if (block[i1 + 5] != block[i2 + 5]) {
                                                    if ((block[i1 + 5] & 255) > (block[i2 + 5] & 255)) {
                                                        break;
                                                    }
                                                } else {
                                                    i1 += 6;
                                                    i2 += 6;
                                                    if (block[i1] != block[i2]) {
                                                        if ((block[i1] & 255) > (block[i2] & 255)) {
                                                            break;
                                                        }
                                                    } else {
                                                        x = lastShadow;
                                                        while (x > 0) {
                                                            x -= 4;
                                                            if (block[i1 + 1] == block[i2 + 1]) {
                                                                if ((block[i1 + 1] & 255) > (block[i2 + 1] & 255)) {
                                                                    break;
                                                                }
                                                            } else {
                                                                if (quadrant[i1] == quadrant[i2]) {
                                                                    if (quadrant[i1] > quadrant[i2]) {
                                                                        break;
                                                                    }
                                                                } else {
                                                                    if (block[i1 + 2] == block[i2 + 2]) {
                                                                        if ((block[i1 + 2] & 255) > (block[i2 + 2] & 255)) {
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        if (quadrant[i1 + 1] == quadrant[i2 + 1]) {
                                                                            if (quadrant[i1 + 1] > quadrant[i2 + 1]) {
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            if (block[i1 + 3] == block[i2 + 3]) {
                                                                                if ((block[i1 + 3] & 255) > (block[i2 + 3] & 255)) {
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                if (quadrant[i1 + 2] == quadrant[i2 + 2]) {
                                                                                    if (quadrant[i1 + 2] > quadrant[i2 + 2]) {
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    if (block[i1 + 4] == block[i2 + 4]) {
                                                                                        if ((block[i1 + 4] & 255) > (block[i2 + 4] & 255)) {
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        if (quadrant[i1 + 3] == quadrant[i2 + 3]) {
                                                                                            if (quadrant[i1 + 3] > quadrant[i2 + 3]) {
                                                                                                break;
                                                                                            }
                                                                                        } else {
                                                                                            i1 += 4;
                                                                                            if (i1 >= lastPlus1) {
                                                                                                i1 -= lastPlus1;
                                                                                            }
                                                                                            i2 += 4;
                                                                                            if (i2 >= lastPlus1) {
                                                                                                i2 -= lastPlus1;
                                                                                            }
                                                                                            workDoneShadow++;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        fmap[j] = v;
                        i++;
                    }
                    if (firstAttemptShadow && i <= hi && workDoneShadow > workLimitShadow) {
                        break loop1;
                    }
                }
            }
            this.workDone = workDoneShadow;
            return firstAttemptShadow && workDoneShadow > workLimitShadow;
        } else if (!this.firstAttempt || this.workDone <= this.workLimit) {
            return false;
        } else {
            return true;
        }
    }

    private static void vswap(int[] fmap, int p1, int p2, int n) {
        n += p1;
        int p22 = p2;
        int i = p1;
        while (i < n) {
            int t = fmap[i];
            p1 = i + 1;
            fmap[i] = fmap[p22];
            p2 = p22 + 1;
            fmap[p22] = t;
            p22 = p2;
            i = p1;
        }
    }

    private static byte med3(byte a, byte b, byte c) {
        if (a < b) {
            if (b < c) {
                return b;
            }
            return a < c ? c : a;
        } else if (b <= c) {
            return a > c ? c : a;
        } else {
            return b;
        }
    }

    private void mainQSort3(Data dataShadow, int loSt, int hiSt, int dSt, int last) {
        int[] stack_ll = this.stack_ll;
        int[] stack_hh = this.stack_hh;
        int[] stack_dd = this.stack_dd;
        int[] fmap = dataShadow.fmap;
        byte[] block = dataShadow.block;
        stack_ll[0] = loSt;
        stack_hh[0] = hiSt;
        stack_dd[0] = dSt;
        int sp = 1;
        while (true) {
            sp--;
            if (sp >= 0) {
                int lo = stack_ll[sp];
                int hi = stack_hh[sp];
                int d = stack_dd[sp];
                if (hi - lo >= 20 && d <= 10) {
                    int n;
                    int gtHi;
                    int unHi;
                    int d1 = d + 1;
                    int med = med3(block[fmap[lo] + d1], block[fmap[hi] + d1], block[fmap[(lo + hi) >>> 1] + d1]) & 255;
                    int unHi2 = hi;
                    int gtHi2 = hi;
                    int ltLo = lo;
                    int unLo = lo;
                    while (true) {
                        int temp;
                        int unLo2;
                        if (unLo <= unHi2) {
                            int ltLo2;
                            n = (block[fmap[unLo] + d1] & 255) - med;
                            if (n == 0) {
                                temp = fmap[unLo];
                                unLo2 = unLo + 1;
                                fmap[unLo] = fmap[ltLo];
                                ltLo2 = ltLo + 1;
                                fmap[ltLo] = temp;
                            } else if (n < 0) {
                                unLo2 = unLo + 1;
                                ltLo2 = ltLo;
                            }
                            ltLo = ltLo2;
                            unLo = unLo2;
                        }
                        gtHi = gtHi2;
                        unHi = unHi2;
                        while (unLo <= unHi) {
                            n = (block[fmap[unHi] + d1] & 255) - med;
                            if (n != 0) {
                                if (n <= 0) {
                                    break;
                                }
                                unHi2 = unHi - 1;
                                gtHi2 = gtHi;
                            } else {
                                temp = fmap[unHi];
                                unHi2 = unHi - 1;
                                fmap[unHi] = fmap[gtHi];
                                gtHi2 = gtHi - 1;
                                fmap[gtHi] = temp;
                            }
                            gtHi = gtHi2;
                            unHi = unHi2;
                        }
                        if (unLo > unHi) {
                            break;
                        }
                        temp = fmap[unLo];
                        unLo2 = unLo + 1;
                        fmap[unLo] = fmap[unHi];
                        unHi2 = unHi - 1;
                        fmap[unHi] = temp;
                        gtHi2 = gtHi;
                        unLo = unLo2;
                    }
                    if (gtHi < ltLo) {
                        stack_ll[sp] = lo;
                        stack_hh[sp] = hi;
                        stack_dd[sp] = d1;
                        sp++;
                    } else {
                        n = ltLo - lo < unLo - ltLo ? ltLo - lo : unLo - ltLo;
                        vswap(fmap, lo, unLo - n, n);
                        int m = hi - gtHi < gtHi - unHi ? hi - gtHi : gtHi - unHi;
                        vswap(fmap, unLo, (hi - m) + 1, m);
                        n = ((lo + unLo) - ltLo) - 1;
                        m = (hi - (gtHi - unHi)) + 1;
                        stack_ll[sp] = lo;
                        stack_hh[sp] = n;
                        stack_dd[sp] = d;
                        sp++;
                        stack_ll[sp] = n + 1;
                        stack_hh[sp] = m - 1;
                        stack_dd[sp] = d1;
                        sp++;
                        stack_ll[sp] = m;
                        stack_hh[sp] = hi;
                        stack_dd[sp] = d;
                        sp++;
                    }
                } else if (mainSimpleSort(dataShadow, lo, hi, d, last)) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    final void mainSort(Data dataShadow, int lastShadow) {
        int i;
        int i2;
        int[] runningOrder = this.mainSort_runningOrder;
        int[] copy = this.mainSort_copy;
        boolean[] bigDone = this.mainSort_bigDone;
        int[] ftab = this.ftab;
        byte[] block = dataShadow.block;
        int[] fmap = dataShadow.fmap;
        char[] quadrant = this.quadrant;
        int workLimitShadow = this.workLimit;
        boolean firstAttemptShadow = this.firstAttempt;
        int i3 = 65537;
        while (true) {
            i3--;
            if (i3 < 0) {
                break;
            }
            ftab[i3] = 0;
        }
        for (i3 = 0; i3 < 20; i3++) {
            block[(lastShadow + i3) + 2] = block[(i3 % (lastShadow + 1)) + 1];
        }
        i3 = (lastShadow + 20) + 1;
        while (true) {
            i3--;
            if (i3 < 0) {
                break;
            }
            quadrant[i3] = '\u0000';
        }
        block[0] = block[lastShadow + 1];
        int c1 = block[0] & 255;
        for (i3 = 0; i3 <= lastShadow; i3++) {
            int c2 = block[i3 + 1] & 255;
            i = (c1 << 8) + c2;
            ftab[i] = ftab[i] + 1;
            c1 = c2;
        }
        for (i3 = 1; i3 <= 65536; i3++) {
            ftab[i3] = ftab[i3] + ftab[i3 - 1];
        }
        c1 = block[1] & 255;
        for (i3 = 0; i3 < lastShadow; i3++) {
            c2 = block[i3 + 2] & 255;
            i = (c1 << 8) + c2;
            i2 = ftab[i] - 1;
            ftab[i] = i2;
            fmap[i2] = i3;
            c1 = c2;
        }
        i = ((block[lastShadow + 1] & 255) << 8) + (block[1] & 255);
        i2 = ftab[i] - 1;
        ftab[i] = i2;
        fmap[i2] = lastShadow;
        i3 = 256;
        while (true) {
            i3--;
            if (i3 < 0) {
                break;
            }
            bigDone[i3] = false;
            runningOrder[i3] = i3;
        }
        int h = 364;
        while (h != 1) {
            h /= 3;
            for (i3 = h; i3 <= 255; i3++) {
                int vv = runningOrder[i3];
                int a = ftab[(vv + 1) << 8] - ftab[vv << 8];
                int b = h - 1;
                int j = i3;
                int ro = runningOrder[j - h];
                while (ftab[(ro + 1) << 8] - ftab[ro << 8] > a) {
                    runningOrder[j] = ro;
                    j -= h;
                    if (j <= b) {
                        break;
                    }
                    ro = runningOrder[j - h];
                }
                runningOrder[j] = vv;
            }
        }
        for (i3 = 0; i3 <= 255; i3++) {
            int ss = runningOrder[i3];
            for (j = 0; j <= 255; j++) {
                int sb = (ss << 8) + j;
                int ftab_sb = ftab[sb];
                if ((2097152 & ftab_sb) != 2097152) {
                    int lo = ftab_sb & CLEARMASK;
                    int hi = (ftab[sb + 1] & CLEARMASK) - 1;
                    if (hi > lo) {
                        mainQSort3(dataShadow, lo, hi, 2, lastShadow);
                        if (firstAttemptShadow && this.workDone > workLimitShadow) {
                            return;
                        }
                    }
                    ftab[sb] = 2097152 | ftab_sb;
                }
            }
            for (j = 0; j <= 255; j++) {
                copy[j] = ftab[(j << 8) + ss] & CLEARMASK;
            }
            int hj = ftab[(ss + 1) << 8] & CLEARMASK;
            for (j = ftab[ss << 8] & CLEARMASK; j < hj; j++) {
                int fmap_j = fmap[j];
                c1 = block[fmap_j] & 255;
                if (!bigDone[c1]) {
                    fmap[copy[c1]] = fmap_j == 0 ? lastShadow : fmap_j - 1;
                    copy[c1] = copy[c1] + 1;
                }
            }
            j = 256;
            while (true) {
                j--;
                if (j < 0) {
                    break;
                }
                i = (j << 8) + ss;
                ftab[i] = ftab[i] | 2097152;
            }
            bigDone[ss] = true;
            if (i3 < 255) {
                int bbStart = ftab[ss << 8] & CLEARMASK;
                int bbSize = (ftab[(ss + 1) << 8] & CLEARMASK) - bbStart;
                int shifts = 0;
                while ((bbSize >> shifts) > 65534) {
                    shifts++;
                }
                for (j = 0; j < bbSize; j++) {
                    int a2update = fmap[bbStart + j];
                    char qVal = (char) (j >> shifts);
                    quadrant[a2update] = qVal;
                    if (a2update < 20) {
                        quadrant[(a2update + lastShadow) + 1] = qVal;
                    }
                }
            }
        }
    }
}

package org.jboss.netty.util.internal.jzlib;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.InputDeviceCompat;
import com.hikam.BuildConfig;
import com.jwkj.widget.MyImageView;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.http.HttpStatus;

final class InfTree {
    static final int BMAX = 15;
    static final int[] cpdext = new int[]{0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13};
    static final int[] cpdist = new int[]{1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, InputDeviceCompat.SOURCE_DPAD, 769, InputDeviceCompat.SOURCE_GAMEPAD, 1537, 2049, 3073, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, 6145, 8193, 12289, 16385, 24577};
    static final int[] cplens = new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, TarConstants.PREFIXLEN_XSTAR, 163, 195, 227, BZip2Constants.MAX_ALPHA_SIZE, 0, 0};
    static final int[] cplext = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 112, 112};
    static final int fixed_bd = 5;
    static final int fixed_bl = 9;
    static final int[] fixed_td = new int[]{80, 5, 1, 87, 5, 257, 83, 5, 17, 91, 5, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, 81, 5, 5, 89, 5, InputDeviceCompat.SOURCE_GAMEPAD, 85, 5, 65, 93, 5, 16385, 80, 5, 3, 88, 5, InputDeviceCompat.SOURCE_DPAD, 84, 5, 33, 92, 5, 8193, 82, 5, 9, 90, 5, 2049, 86, 5, 129, 192, 5, 24577, 80, 5, 2, 87, 5, 385, 83, 5, 25, 91, 5, 6145, 81, 5, 7, 89, 5, 1537, 85, 5, 97, 93, 5, 24577, 80, 5, 4, 88, 5, 769, 84, 5, 49, 92, 5, 12289, 82, 5, 13, 90, 5, 3073, 86, 5, 193, 192, 5, 24577};
    static final int[] fixed_tl = new int[]{96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, 115, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9, 192, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, 160, 0, 8, 0, 0, 8, 128, 0, 8, 64, 0, 9, 224, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, 144, 83, 7, 59, 0, 8, MyImageView.IMAGE_WIDTH, 0, 8, 56, 0, 9, 208, 81, 7, 17, 0, 8, 104, 0, 8, 40, 0, 9, 176, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 240, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8, 227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, 200, 81, 7, 13, 0, 8, 100, 0, 8, 36, 0, 9, 168, 0, 8, 4, 0, 8, 132, 0, 8, 68, 0, 9, 232, 80, 7, 8, 0, 8, 92, 0, 8, 28, 0, 9, 152, 84, 7, 83, 0, 8, 124, 0, 8, 60, 0, 9, 216, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, 184, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9, 248, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, 163, 83, 7, 35, 0, 8, 114, 0, 8, 50, 0, 9, 196, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, 164, 0, 8, 2, 0, 8, TransportMediator.KEYCODE_MEDIA_RECORD, 0, 8, 66, 0, 9, 228, 80, 7, 7, 0, 8, 90, 0, 8, 26, 0, 9, TarConstants.CHKSUM_OFFSET, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 212, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9, 180, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, 244, 80, 7, 5, 0, 8, 86, 0, 8, 22, 192, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, HttpStatus.SC_NO_CONTENT, 81, 7, 15, 0, 8, 102, 0, 8, 38, 0, 9, 172, 0, 8, 6, 0, 8, 134, 0, 8, 70, 0, 9, 236, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, 156, 84, 7, 99, 0, 8, TransportMediator.KEYCODE_MEDIA_PLAY, 0, 8, 62, 0, 9, 220, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 188, 0, 8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 252, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, TarConstants.PREFIXLEN_XSTAR, 82, 7, 31, 0, 8, 113, 0, 8, 49, 0, 9, 194, 80, 7, 10, 0, 8, 97, 0, 8, 33, 0, 9, 162, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 226, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9, 146, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 210, 81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, 178, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 242, 80, 7, 4, 0, 8, 85, 0, 8, 21, 80, 8, BZip2Constants.MAX_ALPHA_SIZE, 83, 7, 43, 0, 8, 117, 0, 8, 53, 0, 9, HttpStatus.SC_ACCEPTED, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 170, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9, 234, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, 154, 84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, 218, 82, 7, 23, 0, 8, 109, 0, 8, 45, 0, 9, 186, 0, 8, 13, 0, 8, 141, 0, 8, 77, 0, 9, 250, 80, 7, 3, 0, 8, 83, 0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, 115, 0, 8, 51, 0, 9, BuildConfig.VERSION_CODE, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9, 166, 0, 8, 3, 0, 8, TarConstants.PREFIXLEN_XSTAR, 0, 8, 67, 0, 9, 230, 80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, 150, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 214, 82, 7, 19, 0, 8, 107, 0, 8, 43, 0, 9, 182, 0, 8, 11, 0, 8, 139, 0, 8, 75, 0, 9, 246, 80, 7, 5, 0, 8, 87, 0, 8, 23, 192, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9, HttpStatus.SC_PARTIAL_CONTENT, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 174, 0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, 238, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 158, 84, 7, 99, 0, 8, TransportMediator.KEYCODE_MEDIA_PAUSE, 0, 8, 63, 0, 9, 222, 82, 7, 27, 0, 8, 111, 0, 8, 47, 0, 9, 190, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 254, 96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, 115, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9, 193, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, 161, 0, 8, 0, 0, 8, 128, 0, 8, 64, 0, 9, 225, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, 145, 83, 7, 59, 0, 8, MyImageView.IMAGE_WIDTH, 0, 8, 56, 0, 9, 209, 81, 7, 17, 0, 8, 104, 0, 8, 40, 0, 9, 177, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 241, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8, 227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, HttpStatus.SC_CREATED, 81, 7, 13, 0, 8, 100, 0, 8, 36, 0, 9, 169, 0, 8, 4, 0, 8, 132, 0, 8, 68, 0, 9, 233, 80, 7, 8, 0, 8, 92, 0, 8, 28, 0, 9, 153, 84, 7, 83, 0, 8, 124, 0, 8, 60, 0, 9, 217, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, 185, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9, 249, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, 163, 83, 7, 35, 0, 8, 114, 0, 8, 50, 0, 9, 197, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, 165, 0, 8, 2, 0, 8, TransportMediator.KEYCODE_MEDIA_RECORD, 0, 8, 66, 0, 9, 229, 80, 7, 7, 0, 8, 90, 0, 8, 26, 0, 9, 149, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 213, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9, 181, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, 245, 80, 7, 5, 0, 8, 86, 0, 8, 22, 192, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, HttpStatus.SC_RESET_CONTENT, 81, 7, 15, 0, 8, 102, 0, 8, 38, 0, 9, 173, 0, 8, 6, 0, 8, 134, 0, 8, 70, 0, 9, 237, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, 157, 84, 7, 99, 0, 8, TransportMediator.KEYCODE_MEDIA_PLAY, 0, 8, 62, 0, 9, 221, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 189, 0, 8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 253, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, TarConstants.PREFIXLEN_XSTAR, 82, 7, 31, 0, 8, 113, 0, 8, 49, 0, 9, 195, 80, 7, 10, 0, 8, 97, 0, 8, 33, 0, 9, 163, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 227, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9, 147, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 211, 81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, 179, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 243, 80, 7, 4, 0, 8, 85, 0, 8, 21, 80, 8, BZip2Constants.MAX_ALPHA_SIZE, 83, 7, 43, 0, 8, 117, 0, 8, 53, 0, 9, HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 171, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9, 235, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, TarConstants.PREFIXLEN, 84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, 219, 82, 7, 23, 0, 8, 109, 0, 8, 45, 0, 9, 187, 0, 8, 13, 0, 8, 141, 0, 8, 77, 0, 9, 251, 80, 7, 3, 0, 8, 83, 0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, 115, 0, 8, 51, 0, 9, 199, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9, 167, 0, 8, 3, 0, 8, TarConstants.PREFIXLEN_XSTAR, 0, 8, 67, 0, 9, 231, 80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, 151, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 215, 82, 7, 19, 0, 8, 107, 0, 8, 43, 0, 9, 183, 0, 8, 11, 0, 8, 139, 0, 8, 75, 0, 9, 247, 80, 7, 5, 0, 8, 87, 0, 8, 23, 192, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9, HttpStatus.SC_MULTI_STATUS, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 175, 0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, 239, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 159, 84, 7, 99, 0, 8, TransportMediator.KEYCODE_MEDIA_PAUSE, 0, 8, 63, 0, 9, 223, 82, 7, 27, 0, 8, 111, 0, 8, 47, 0, 9, 191, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 255};
    private int[] f517c;
    private int[] hn;
    private int[] f518r;
    private int[] f519u;
    private int[] f520v;
    private int[] f521x;

    InfTree() {
    }

    private int huft_build(int[] b, int bindex, int n, int s, int[] d, int[] e, int[] t, int[] m, int[] hp, int[] hn, int[] v) {
        int p = 0;
        int i = n;
        do {
            int[] iArr = this.f517c;
            int i2 = b[bindex + p];
            iArr[i2] = iArr[i2] + 1;
            p++;
            i--;
        } while (i != 0);
        if (this.f517c[0] == n) {
            t[0] = -1;
            m[0] = 0;
            return 0;
        }
        int l = m[0];
        int j = 1;
        while (j <= 15 && this.f517c[j] == 0) {
            j++;
        }
        int k = j;
        if (l < j) {
            l = j;
        }
        i = 15;
        while (i != 0 && this.f517c[i] == 0) {
            i--;
        }
        int g = i;
        if (l > i) {
            l = i;
        }
        m[0] = l;
        int y = 1 << j;
        while (j < i) {
            y -= this.f517c[j];
            if (y < 0) {
                return -3;
            }
            j++;
            y <<= 1;
        }
        y -= this.f517c[i];
        if (y < 0) {
            return -3;
        }
        iArr = this.f517c;
        iArr[i] = iArr[i] + y;
        j = 0;
        this.f521x[1] = 0;
        p = 1;
        int xp = 2;
        while (true) {
            i--;
            if (i == 0) {
                break;
            }
            j += this.f517c[p];
            this.f521x[xp] = j;
            xp++;
            p++;
        }
        i = 0;
        p = 0;
        do {
            j = b[bindex + p];
            if (j != 0) {
                iArr = this.f521x;
                i2 = iArr[j];
                iArr[j] = i2 + 1;
                v[i2] = i;
            }
            p++;
            i++;
        } while (i < n);
        n = this.f521x[g];
        i = 0;
        this.f521x[0] = 0;
        p = 0;
        int h = -1;
        int w = -l;
        this.f519u[0] = 0;
        int q = 0;
        int z = 0;
        while (k <= g) {
            int p2 = p;
            int a = this.f517c[k];
            while (true) {
                int a2 = a - 1;
                if (a == 0) {
                    break;
                }
                int f;
                while (k > w + l) {
                    h++;
                    w += l;
                    z = g - w;
                    if (z > l) {
                        z = l;
                    }
                    j = k - w;
                    f = 1 << j;
                    if (f > a2 + 1) {
                        f -= a2 + 1;
                        xp = k;
                        if (j < z) {
                            while (true) {
                                j++;
                                if (j >= z) {
                                    break;
                                }
                                f <<= 1;
                                xp++;
                                if (f <= this.f517c[xp]) {
                                    break;
                                }
                                f -= this.f517c[xp];
                            }
                        }
                    }
                    z = 1 << j;
                    if (hn[0] + z > 1440) {
                        p = p2;
                        return -3;
                    }
                    iArr = this.f519u;
                    q = hn[0];
                    iArr[h] = q;
                    hn[0] = hn[0] + z;
                    if (h != 0) {
                        this.f521x[h] = i;
                        this.f518r[0] = (byte) j;
                        this.f518r[1] = (byte) l;
                        j = i >>> (w - l);
                        this.f518r[2] = (q - this.f519u[h - 1]) - j;
                        System.arraycopy(this.f518r, 0, hp, (this.f519u[h - 1] + j) * 3, 3);
                    } else {
                        t[0] = q;
                    }
                }
                this.f518r[1] = (byte) (k - w);
                if (p2 >= n) {
                    this.f518r[0] = 192;
                    p = p2;
                } else if (v[p2] < s) {
                    this.f518r[0] = (byte) (v[p2] < 256 ? 0 : 96);
                    p = p2 + 1;
                    this.f518r[2] = v[p2];
                } else {
                    this.f518r[0] = (byte) ((e[v[p2] - s] + 16) + 64);
                    p = p2 + 1;
                    this.f518r[2] = d[v[p2] - s];
                }
                f = 1 << (k - w);
                for (j = i >>> w; j < z; j += f) {
                    System.arraycopy(this.f518r, 0, hp, (q + j) * 3, 3);
                }
                j = 1 << (k - 1);
                while ((i & j) != 0) {
                    i ^= j;
                    j >>>= 1;
                }
                i ^= j;
                int mask = (1 << w) - 1;
                while ((i & mask) != this.f521x[h]) {
                    h--;
                    w -= l;
                    mask = (1 << w) - 1;
                }
                p2 = p;
                a = a2;
            }
            k++;
            p = p2;
        }
        return (y == 0 || g == 1) ? 0 : -5;
    }

    int inflate_trees_bits(int[] c, int[] bb, int[] tb, int[] hp, ZStream z) {
        initWorkArea(19);
        this.hn[0] = 0;
        int result = huft_build(c, 0, 19, 19, null, null, tb, bb, hp, this.hn, this.f520v);
        if (result == -3) {
            z.msg = "oversubscribed dynamic bit lengths tree";
            return result;
        } else if (result != -5 && bb[0] != 0) {
            return result;
        } else {
            z.msg = "incomplete dynamic bit lengths tree";
            return -3;
        }
    }

    int inflate_trees_dynamic(int nl, int nd, int[] c, int[] bl, int[] bd, int[] tl, int[] td, int[] hp, ZStream z) {
        initWorkArea(288);
        this.hn[0] = 0;
        int result = huft_build(c, 0, nl, 257, cplens, cplext, tl, bl, hp, this.hn, this.f520v);
        if (result != 0 || bl[0] == 0) {
            if (result == -3) {
                z.msg = "oversubscribed literal/length tree";
            } else if (result != -4) {
                z.msg = "incomplete literal/length tree";
                result = -3;
            }
            return result;
        }
        initWorkArea(288);
        result = huft_build(c, nl, nd, 0, cpdist, cpdext, td, bd, hp, this.hn, this.f520v);
        if (result == 0 && (bd[0] != 0 || nl <= 257)) {
            return 0;
        }
        if (result == -3) {
            z.msg = "oversubscribed distance tree";
        } else if (result == -5) {
            z.msg = "incomplete distance tree";
            result = -3;
        } else if (result != -4) {
            z.msg = "empty distance tree with lengths";
            result = -3;
        }
        return result;
    }

    static int inflate_trees_fixed(int[] bl, int[] bd, int[][] tl, int[][] td) {
        bl[0] = 9;
        bd[0] = 5;
        tl[0] = fixed_tl;
        td[0] = fixed_td;
        return 0;
    }

    private void initWorkArea(int vsize) {
        if (this.hn == null) {
            this.hn = new int[1];
            this.f520v = new int[vsize];
            this.f517c = new int[16];
            this.f518r = new int[3];
            this.f519u = new int[15];
            this.f521x = new int[16];
            return;
        }
        int i;
        if (this.f520v.length < vsize) {
            this.f520v = new int[vsize];
        } else {
            for (i = 0; i < vsize; i++) {
                this.f520v[i] = 0;
            }
        }
        for (i = 0; i < 16; i++) {
            this.f517c[i] = 0;
        }
        for (i = 0; i < 3; i++) {
            this.f518r[i] = 0;
        }
        System.arraycopy(this.f517c, 0, this.f519u, 0, 15);
        System.arraycopy(this.f517c, 0, this.f521x, 0, 16);
    }
}

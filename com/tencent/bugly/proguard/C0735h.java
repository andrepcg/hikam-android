package com.tencent.bugly.proguard;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/* compiled from: BUGLY */
public final class C0735h {
    private ByteBuffer f358a;
    private String f359b = "GBK";

    /* compiled from: BUGLY */
    public static class C0734a {
        public byte f356a;
        public int f357b;
    }

    public C0735h(byte[] bArr) {
        this.f358a = ByteBuffer.wrap(bArr);
    }

    public C0735h(byte[] bArr, int i) {
        this.f358a = ByteBuffer.wrap(bArr);
        this.f358a.position(4);
    }

    public final void m328a(byte[] bArr) {
        if (this.f358a != null) {
            this.f358a.clear();
        }
        this.f358a = ByteBuffer.wrap(bArr);
    }

    private static int m307a(C0734a c0734a, ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        c0734a.f356a = (byte) (b & 15);
        c0734a.f357b = (b & 240) >> 4;
        if (c0734a.f357b != 15) {
            return 1;
        }
        c0734a.f357b = byteBuffer.get();
        return 2;
    }

    private boolean m311a(int i) {
        try {
            C0734a c0734a = new C0734a();
            while (true) {
                int a = C0735h.m307a(c0734a, this.f358a.duplicate());
                if (i > c0734a.f357b && c0734a.f356a != (byte) 11) {
                    this.f358a.position(a + this.f358a.position());
                    m310a(c0734a.f356a);
                }
            }
            if (i == c0734a.f357b) {
                return true;
            }
            return false;
        } catch (C0733g e) {
            return false;
        } catch (BufferUnderflowException e2) {
            return false;
        }
    }

    private void m309a() {
        C0734a c0734a = new C0734a();
        do {
            C0735h.m307a(c0734a, this.f358a);
            m310a(c0734a.f356a);
        } while (c0734a.f356a != (byte) 11);
    }

    private void m310a(byte b) {
        int i = 0;
        int a;
        C0734a c0734a;
        switch (b) {
            case (byte) 0:
                this.f358a.position(this.f358a.position() + 1);
                return;
            case (byte) 1:
                this.f358a.position(2 + this.f358a.position());
                return;
            case (byte) 2:
                this.f358a.position(this.f358a.position() + 4);
                return;
            case (byte) 3:
                this.f358a.position(this.f358a.position() + 8);
                return;
            case (byte) 4:
                this.f358a.position(this.f358a.position() + 4);
                return;
            case (byte) 5:
                this.f358a.position(this.f358a.position() + 8);
                return;
            case (byte) 6:
                i = this.f358a.get();
                if (i < 0) {
                    i += 256;
                }
                this.f358a.position(i + this.f358a.position());
                return;
            case (byte) 7:
                this.f358a.position(this.f358a.getInt() + this.f358a.position());
                return;
            case (byte) 8:
                a = m321a(0, 0, true);
                while (i < (a << 1)) {
                    c0734a = new C0734a();
                    C0735h.m307a(c0734a, this.f358a);
                    m310a(c0734a.f356a);
                    i++;
                }
                return;
            case (byte) 9:
                a = m321a(0, 0, true);
                while (i < a) {
                    c0734a = new C0734a();
                    C0735h.m307a(c0734a, this.f358a);
                    m310a(c0734a.f356a);
                    i++;
                }
                return;
            case (byte) 10:
                m309a();
                return;
            case (byte) 11:
            case (byte) 12:
                return;
            case (byte) 13:
                C0734a c0734a2 = new C0734a();
                C0735h.m307a(c0734a2, this.f358a);
                if (c0734a2.f356a != (byte) 0) {
                    throw new C0733g("skipField with invalid type, type value: " + b + ", " + c0734a2.f356a);
                }
                this.f358a.position(m321a(0, 0, true) + this.f358a.position());
                return;
            default:
                throw new C0733g("invalid type.");
        }
    }

    public final boolean m329a(int i, boolean z) {
        if (m320a((byte) 0, i, z) != (byte) 0) {
            return true;
        }
        return false;
    }

    public final byte m320a(byte b, int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 0:
                    return this.f358a.get();
                case (byte) 12:
                    return (byte) 0;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return b;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    public final short m327a(short s, int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 0:
                    return (short) this.f358a.get();
                case (byte) 1:
                    return this.f358a.getShort();
                case (byte) 12:
                    return (short) 0;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return s;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    public final int m321a(int i, int i2, boolean z) {
        if (m311a(i2)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 0:
                    return this.f358a.get();
                case (byte) 1:
                    return this.f358a.getShort();
                case (byte) 2:
                    return this.f358a.getInt();
                case (byte) 12:
                    return 0;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return i;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    public final long m323a(long j, int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 0:
                    return (long) this.f358a.get();
                case (byte) 1:
                    return (long) this.f358a.getShort();
                case (byte) 2:
                    return (long) this.f358a.getInt();
                case (byte) 3:
                    return this.f358a.getLong();
                case (byte) 12:
                    return 0;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return j;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    private float m306a(float f, int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 4:
                    return this.f358a.getFloat();
                case (byte) 12:
                    return 0.0f;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return f;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    private double m305a(double d, int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 4:
                    return (double) this.f358a.getFloat();
                case (byte) 5:
                    return this.f358a.getDouble();
                case (byte) 12:
                    return 0.0d;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return d;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    public final String m330b(int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            int i2;
            byte[] bArr;
            switch (c0734a.f356a) {
                case (byte) 6:
                    i2 = this.f358a.get();
                    if (i2 < 0) {
                        i2 += 256;
                    }
                    bArr = new byte[i2];
                    this.f358a.get(bArr);
                    try {
                        return new String(bArr, this.f359b);
                    } catch (UnsupportedEncodingException e) {
                        return new String(bArr);
                    }
                case (byte) 7:
                    i2 = this.f358a.getInt();
                    if (i2 > 104857600 || i2 < 0) {
                        throw new C0733g("String too long: " + i2);
                    }
                    bArr = new byte[i2];
                    this.f358a.get(bArr);
                    try {
                        return new String(bArr, this.f359b);
                    } catch (UnsupportedEncodingException e2) {
                        return new String(bArr);
                    }
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return null;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    public final <K, V> HashMap<K, V> m326a(Map<K, V> map, int i, boolean z) {
        return (HashMap) m308a(new HashMap(), map, i, z);
    }

    private <K, V> Map<K, V> m308a(Map<K, V> map, Map<K, V> map2, int i, boolean z) {
        if (map2 == null || map2.isEmpty()) {
            return new HashMap();
        }
        Entry entry = (Entry) map2.entrySet().iterator().next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 8:
                    int a = m321a(0, 0, true);
                    if (a < 0) {
                        throw new C0733g("size invalid: " + a);
                    }
                    for (int i2 = 0; i2 < a; i2++) {
                        map.put(m325a(key, 0, true), m325a(value, 1, true));
                    }
                    return map;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return map;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    private boolean[] m314d(int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a < 0) {
                        throw new C0733g("size invalid: " + a);
                    }
                    boolean[] zArr = new boolean[a];
                    for (int i2 = 0; i2 < a; i2++) {
                        boolean z2;
                        if (m320a((byte) 0, 0, true) != (byte) 0) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        zArr[i2] = z2;
                    }
                    return zArr;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return null;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    public final byte[] m331c(int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            byte[] bArr;
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a < 0) {
                        throw new C0733g("size invalid: " + a);
                    }
                    bArr = new byte[a];
                    for (int i2 = 0; i2 < a; i2++) {
                        bArr[i2] = m320a(bArr[0], 0, true);
                    }
                    return bArr;
                case (byte) 13:
                    C0734a c0734a2 = new C0734a();
                    C0735h.m307a(c0734a2, this.f358a);
                    if (c0734a2.f356a != (byte) 0) {
                        throw new C0733g("type mismatch, tag: " + i + ", type: " + c0734a.f356a + ", " + c0734a2.f356a);
                    }
                    int a2 = m321a(0, 0, true);
                    if (a2 < 0) {
                        throw new C0733g("invalid size, tag: " + i + ", type: " + c0734a.f356a + ", " + c0734a2.f356a + ", size: " + a2);
                    }
                    bArr = new byte[a2];
                    this.f358a.get(bArr);
                    return bArr;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return null;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    private short[] m315e(int i, boolean z) {
        short[] sArr = null;
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a >= 0) {
                        sArr = new short[a];
                        for (int i2 = 0; i2 < a; i2++) {
                            sArr[i2] = m327a(sArr[0], 0, true);
                        }
                        break;
                    }
                    throw new C0733g("size invalid: " + a);
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (z) {
            throw new C0733g("require field not exist.");
        }
        return sArr;
    }

    private int[] m316f(int i, boolean z) {
        int[] iArr = null;
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a >= 0) {
                        iArr = new int[a];
                        for (int i2 = 0; i2 < a; i2++) {
                            iArr[i2] = m321a(iArr[0], 0, true);
                        }
                        break;
                    }
                    throw new C0733g("size invalid: " + a);
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (z) {
            throw new C0733g("require field not exist.");
        }
        return iArr;
    }

    private long[] m317g(int i, boolean z) {
        long[] jArr = null;
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a >= 0) {
                        jArr = new long[a];
                        for (int i2 = 0; i2 < a; i2++) {
                            jArr[i2] = m323a(jArr[0], 0, true);
                        }
                        break;
                    }
                    throw new C0733g("size invalid: " + a);
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (z) {
            throw new C0733g("require field not exist.");
        }
        return jArr;
    }

    private float[] m318h(int i, boolean z) {
        float[] fArr = null;
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a >= 0) {
                        fArr = new float[a];
                        for (int i2 = 0; i2 < a; i2++) {
                            fArr[i2] = m306a(fArr[0], 0, true);
                        }
                        break;
                    }
                    throw new C0733g("size invalid: " + a);
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (z) {
            throw new C0733g("require field not exist.");
        }
        return fArr;
    }

    private double[] m319i(int i, boolean z) {
        double[] dArr = null;
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a >= 0) {
                        dArr = new double[a];
                        for (int i2 = 0; i2 < a; i2++) {
                            dArr[i2] = m305a(dArr[0], 0, true);
                        }
                        break;
                    }
                    throw new C0733g("size invalid: " + a);
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (z) {
            throw new C0733g("require field not exist.");
        }
        return dArr;
    }

    private <T> T[] m312a(T[] tArr, int i, boolean z) {
        if (tArr != null && tArr.length != 0) {
            return m313b(tArr[0], i, z);
        }
        throw new C0733g("unable to get type of key and value.");
    }

    private <T> T[] m313b(T t, int i, boolean z) {
        if (m311a(i)) {
            C0734a c0734a = new C0734a();
            C0735h.m307a(c0734a, this.f358a);
            switch (c0734a.f356a) {
                case (byte) 9:
                    int a = m321a(0, 0, true);
                    if (a < 0) {
                        throw new C0733g("size invalid: " + a);
                    }
                    Object[] objArr = (Object[]) Array.newInstance(t.getClass(), a);
                    for (int i2 = 0; i2 < a; i2++) {
                        objArr[i2] = m325a((Object) t, 0, true);
                    }
                    return objArr;
                default:
                    throw new C0733g("type mismatch.");
            }
        } else if (!z) {
            return null;
        } else {
            throw new C0733g("require field not exist.");
        }
    }

    public final C0737j m324a(C0737j c0737j, int i, boolean z) {
        C0737j c0737j2 = null;
        if (m311a(i)) {
            try {
                c0737j2 = (C0737j) c0737j.getClass().newInstance();
                C0734a c0734a = new C0734a();
                C0735h.m307a(c0734a, this.f358a);
                if (c0734a.f356a != (byte) 10) {
                    throw new C0733g("type mismatch.");
                }
                c0737j2.mo2283a(this);
                m309a();
            } catch (Exception e) {
                throw new C0733g(e.getMessage());
            }
        } else if (z) {
            throw new C0733g("require field not exist.");
        }
        return c0737j2;
    }

    public final <T> Object m325a(T t, int i, boolean z) {
        int i2 = 0;
        if (t instanceof Byte) {
            return Byte.valueOf(m320a((byte) 0, i, z));
        }
        if (t instanceof Boolean) {
            boolean z2;
            if (m320a((byte) 0, i, z) != (byte) 0) {
                z2 = true;
            }
            return Boolean.valueOf(z2);
        } else if (t instanceof Short) {
            return Short.valueOf(m327a((short) 0, i, z));
        } else {
            if (t instanceof Integer) {
                return Integer.valueOf(m321a(0, i, z));
            }
            if (t instanceof Long) {
                return Long.valueOf(m323a(0, i, z));
            }
            if (t instanceof Float) {
                return Float.valueOf(m306a(0.0f, i, z));
            }
            if (t instanceof Double) {
                return Double.valueOf(m305a(0.0d, i, z));
            }
            if (t instanceof String) {
                return String.valueOf(m330b(i, z));
            }
            if (t instanceof Map) {
                return (HashMap) m308a(new HashMap(), (Map) t, i, z);
            } else if (t instanceof List) {
                List list = (List) t;
                if (list == null || list.isEmpty()) {
                    return new ArrayList();
                }
                Object[] b = m313b(list.get(0), i, z);
                if (b == null) {
                    return null;
                }
                ArrayList arrayList = new ArrayList();
                while (i2 < b.length) {
                    arrayList.add(b[i2]);
                    i2++;
                }
                return arrayList;
            } else if (t instanceof C0737j) {
                return m324a((C0737j) t, i, z);
            } else {
                if (!t.getClass().isArray()) {
                    throw new C0733g("read object error: unsupport type.");
                } else if ((t instanceof byte[]) || (t instanceof Byte[])) {
                    return m331c(i, z);
                } else {
                    if (t instanceof boolean[]) {
                        return m314d(i, z);
                    }
                    if (t instanceof short[]) {
                        return m315e(i, z);
                    }
                    if (t instanceof int[]) {
                        return m316f(i, z);
                    }
                    if (t instanceof long[]) {
                        return m317g(i, z);
                    }
                    if (t instanceof float[]) {
                        return m318h(i, z);
                    }
                    if (t instanceof double[]) {
                        return m319i(i, z);
                    }
                    return m312a((Object[]) t, i, z);
                }
            }
        }
    }

    public final int m322a(String str) {
        this.f359b = str;
        return 0;
    }
}

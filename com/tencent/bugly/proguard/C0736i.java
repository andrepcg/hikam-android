package com.tencent.bugly.proguard;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.netty.handler.codec.http.HttpConstants;

/* compiled from: BUGLY */
public final class C0736i {
    private ByteBuffer f360a;
    private String f361b;

    public C0736i(int i) {
        this.f361b = "GBK";
        this.f360a = ByteBuffer.allocate(i);
    }

    public C0736i() {
        this(128);
    }

    public final ByteBuffer m335a() {
        return this.f360a;
    }

    public final byte[] m347b() {
        Object obj = new byte[this.f360a.position()];
        System.arraycopy(this.f360a.array(), 0, obj, 0, this.f360a.position());
        return obj;
    }

    private void m332a(int i) {
        if (this.f360a.remaining() < i) {
            ByteBuffer allocate = ByteBuffer.allocate((this.f360a.capacity() + i) << 1);
            allocate.put(this.f360a.array(), 0, this.f360a.position());
            this.f360a = allocate;
        }
    }

    private void m333b(byte b, int i) {
        if (i < 15) {
            this.f360a.put((byte) ((i << 4) | b));
        } else if (i < 256) {
            this.f360a.put((byte) (b | 240));
            this.f360a.put((byte) i);
        } else {
            throw new C0731b("tag is too large: " + i);
        }
    }

    public final void m345a(boolean z, int i) {
        m336a((byte) (z ? 1 : 0), i);
    }

    public final void m336a(byte b, int i) {
        m332a(3);
        if (b == (byte) 0) {
            m333b((byte) 12, i);
            return;
        }
        m333b((byte) 0, i);
        this.f360a.put(b);
    }

    public final void m344a(short s, int i) {
        m332a(4);
        if (s < (short) -128 || s > (short) 127) {
            m333b((byte) 1, i);
            this.f360a.putShort(s);
            return;
        }
        m336a((byte) s, i);
    }

    public final void m337a(int i, int i2) {
        m332a(6);
        if (i < -32768 || i > 32767) {
            m333b((byte) 2, i2);
            this.f360a.putInt(i);
            return;
        }
        m344a((short) i, i2);
    }

    public final void m338a(long j, int i) {
        m332a(10);
        if (j < -2147483648L || j > 2147483647L) {
            m333b((byte) 3, i);
            this.f360a.putLong(j);
            return;
        }
        m337a((int) j, i);
    }

    public final void m341a(String str, int i) {
        byte[] bytes;
        try {
            bytes = str.getBytes(this.f361b);
        } catch (UnsupportedEncodingException e) {
            bytes = str.getBytes();
        }
        m332a(bytes.length + 10);
        if (bytes.length > 255) {
            m333b((byte) 7, i);
            this.f360a.putInt(bytes.length);
            this.f360a.put(bytes);
            return;
        }
        m333b((byte) 6, i);
        this.f360a.put((byte) bytes.length);
        this.f360a.put(bytes);
    }

    public final <K, V> void m343a(Map<K, V> map, int i) {
        m332a(8);
        m333b((byte) 8, i);
        m337a(map == null ? 0 : map.size(), 0);
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                m340a(entry.getKey(), 0);
                m340a(entry.getValue(), 1);
            }
        }
    }

    public final void m346a(byte[] bArr, int i) {
        m332a(bArr.length + 8);
        m333b(HttpConstants.CR, i);
        m333b((byte) 0, 0);
        m337a(bArr.length, 0);
        this.f360a.put(bArr);
    }

    public final <T> void m342a(Collection<T> collection, int i) {
        m332a(8);
        m333b((byte) 9, i);
        m337a(collection == null ? 0 : collection.size(), 0);
        if (collection != null) {
            for (T a : collection) {
                m340a((Object) a, 0);
            }
        }
    }

    public final void m339a(C0737j c0737j, int i) {
        m332a(2);
        m333b((byte) 10, i);
        c0737j.mo2284a(this);
        m332a(2);
        m333b((byte) 11, 0);
    }

    public final void m340a(Object obj, int i) {
        int i2 = 1;
        if (obj instanceof Byte) {
            m336a(((Byte) obj).byteValue(), i);
        } else if (obj instanceof Boolean) {
            if (!((Boolean) obj).booleanValue()) {
                i2 = 0;
            }
            m336a((byte) i2, i);
        } else if (obj instanceof Short) {
            m344a(((Short) obj).shortValue(), i);
        } else if (obj instanceof Integer) {
            m337a(((Integer) obj).intValue(), i);
        } else if (obj instanceof Long) {
            m338a(((Long) obj).longValue(), i);
        } else if (obj instanceof Float) {
            float floatValue = ((Float) obj).floatValue();
            m332a(6);
            m333b((byte) 4, i);
            this.f360a.putFloat(floatValue);
        } else if (obj instanceof Double) {
            double doubleValue = ((Double) obj).doubleValue();
            m332a(10);
            m333b((byte) 5, i);
            this.f360a.putDouble(doubleValue);
        } else if (obj instanceof String) {
            m341a((String) obj, i);
        } else if (obj instanceof Map) {
            m343a((Map) obj, i);
        } else if (obj instanceof List) {
            m342a((List) obj, i);
        } else if (obj instanceof C0737j) {
            C0737j c0737j = (C0737j) obj;
            m332a(2);
            m333b((byte) 10, i);
            c0737j.mo2284a(this);
            m332a(2);
            m333b((byte) 11, 0);
        } else if (obj instanceof byte[]) {
            m346a((byte[]) obj, i);
        } else if (obj instanceof boolean[]) {
            boolean[] zArr = (boolean[]) obj;
            m332a(8);
            m333b((byte) 9, i);
            m337a(zArr.length, 0);
            for (boolean z : zArr) {
                m336a((byte) (z ? 1 : 0), 0);
            }
        } else if (obj instanceof short[]) {
            short[] sArr = (short[]) obj;
            m332a(8);
            m333b((byte) 9, i);
            m337a(sArr.length, 0);
            for (short a : sArr) {
                m344a(a, 0);
            }
        } else if (obj instanceof int[]) {
            int[] iArr = (int[]) obj;
            m332a(8);
            m333b((byte) 9, i);
            m337a(iArr.length, 0);
            for (int a2 : iArr) {
                m337a(a2, 0);
            }
        } else if (obj instanceof long[]) {
            long[] jArr = (long[]) obj;
            m332a(8);
            m333b((byte) 9, i);
            m337a(jArr.length, 0);
            for (long a3 : jArr) {
                m338a(a3, 0);
            }
        } else if (obj instanceof float[]) {
            float[] fArr = (float[]) obj;
            m332a(8);
            m333b((byte) 9, i);
            m337a(fArr.length, 0);
            for (float f : fArr) {
                m332a(6);
                m333b((byte) 4, 0);
                this.f360a.putFloat(f);
            }
        } else if (obj instanceof double[]) {
            double[] dArr = (double[]) obj;
            m332a(8);
            m333b((byte) 9, i);
            m337a(dArr.length, 0);
            for (double d : dArr) {
                m332a(10);
                m333b((byte) 5, 0);
                this.f360a.putDouble(d);
            }
        } else if (obj.getClass().isArray()) {
            Object[] objArr = (Object[]) obj;
            m332a(8);
            m333b((byte) 9, i);
            m337a(objArr.length, 0);
            for (Object a4 : objArr) {
                m340a(a4, 0);
            }
        } else if (obj instanceof Collection) {
            m342a((Collection) obj, i);
        } else {
            throw new C0731b("write object error: unsupport type. " + obj.getClass());
        }
    }

    public final int m334a(String str) {
        this.f361b = str;
        return 0;
    }
}

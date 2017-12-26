package com.tencent.bugly.proguard;

import java.io.Serializable;

/* compiled from: BUGLY */
public abstract class C0737j implements Serializable {
    public abstract void mo2283a(C0735h c0735h);

    public abstract void mo2284a(C0736i c0736i);

    public abstract void mo2285a(StringBuilder stringBuilder, int i);

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        mo2285a(stringBuilder, 0);
        return stringBuilder.toString();
    }
}

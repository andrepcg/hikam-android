package com.squareup.leakcanary;

public final class RefWatcher {
    public static final RefWatcher DISABLED = new RefWatcher();

    private RefWatcher() {
    }

    public void watch(Object watchedReference) {
    }

    public void watch(Object watchedReference, String referenceName) {
    }
}

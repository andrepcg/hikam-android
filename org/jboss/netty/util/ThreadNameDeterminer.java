package org.jboss.netty.util;

public interface ThreadNameDeterminer {
    public static final ThreadNameDeterminer CURRENT = new C12572();
    public static final ThreadNameDeterminer PROPOSED = new C12561();

    static class C12561 implements ThreadNameDeterminer {
        C12561() {
        }

        public String determineThreadName(String currentThreadName, String proposedThreadName) throws Exception {
            return proposedThreadName;
        }
    }

    static class C12572 implements ThreadNameDeterminer {
        C12572() {
        }

        public String determineThreadName(String currentThreadName, String proposedThreadName) throws Exception {
            return null;
        }
    }

    String determineThreadName(String str, String str2) throws Exception;
}

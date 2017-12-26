package com.hp.hpl.sparta;

import java.util.Hashtable;

public class Sparta {
    private static CacheFactory cacheFactory_ = new C10632();
    private static Internment internment_ = new C10621();

    public interface Cache {
        Object get(Object obj);

        Object put(Object obj, Object obj2);

        int size();
    }

    public interface CacheFactory {
        Cache create();
    }

    public interface Internment {
        String intern(String str);
    }

    class C10621 implements Internment {
        private final Hashtable strings_ = new Hashtable();

        C10621() {
        }

        public String intern(String str) {
            String str2 = (String) this.strings_.get(str);
            if (str2 != null) {
                return str2;
            }
            this.strings_.put(str, str);
            return str;
        }
    }

    class C10632 implements CacheFactory {
        C10632() {
        }

        public Cache create() {
            return new HashtableCache(null);
        }
    }

    private static class HashtableCache extends Hashtable implements Cache {
        private HashtableCache() {
        }

        HashtableCache(C10621 c10621) {
            this();
        }
    }

    public static String intern(String str) {
        return internment_.intern(str);
    }

    static Cache newCache() {
        return cacheFactory_.create();
    }

    public static void setCacheFactory(CacheFactory cacheFactory) {
        cacheFactory_ = cacheFactory;
    }

    public static void setInternment(Internment internment) {
        internment_ = internment;
    }
}

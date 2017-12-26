package org.apache.commons.compress.utils;

import java.util.Collections;
import java.util.HashSet;

public class Sets {
    private Sets() {
    }

    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
}

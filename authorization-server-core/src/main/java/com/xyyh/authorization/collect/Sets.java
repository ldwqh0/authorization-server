package com.xyyh.authorization.collect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Sets {
    private Sets() {
    }

    public static <K> Set<K> newHashSet(@SuppressWarnings("unchecked") K... values) {
        HashSet<K> set = new HashSet<>(values.length);
        Collections.addAll(set, values);
        return set;
    }
}

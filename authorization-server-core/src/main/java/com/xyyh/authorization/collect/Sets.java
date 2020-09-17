package com.xyyh.authorization.collect;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Sets {
    private Sets() {
    }

    public static <K> Set<K> newHashSet(K... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    public static <K> Set<K> newHashSet(Collection<K> collection) {
        if (collection == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>(collection);
        }
    }

    public static <K> Set<K> newUnmodifiableSet(Collection<K> collection) {
        return Collections.unmodifiableSet(newHashSet(collection));
    }

    public static <IN, OUT> Set<OUT> newUnmodifiableSet(Collection<IN> collection, Function<IN, OUT> converter) {
        return newUnmodifiableSet(collection.stream().map(converter).collect(Collectors.toSet()));
    }
}

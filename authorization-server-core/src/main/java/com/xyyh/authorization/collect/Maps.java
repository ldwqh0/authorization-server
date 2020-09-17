package com.xyyh.authorization.collect;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Maps {
    private Maps() {
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> newHashMap(Map<K, V> map) {
        return map == null ? new HashMap<>() : new HashMap<>(map);
    }

    public static <K, V> Map<K, V> newUnmodifiableMap(Map<K, V> map) {
        return Collections.unmodifiableMap(newHashMap(map));
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }
}

package com.xyyh.authorization.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Sets {
    public static <T> Set<T> from(Iterable<T> iterable) {
        Set<T> result = new HashSet<>();
        iterable.forEach(result::add);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> of(T[] items) {
        if (!Objects.isNull(items) && items.length > 0) {
            Set<T> result = new HashSet<>();
            for (int i = 0; i < items.length; i++) {
                result.add(items[i]);
            }
            return result;
        } else {
            return (Set<T>) Collections.emptyList();
        }
    }
}

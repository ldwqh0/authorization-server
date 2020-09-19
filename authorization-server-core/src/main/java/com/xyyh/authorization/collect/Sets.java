package com.xyyh.authorization.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Sets {
    private Sets() {
    }

    public static <K> HashSet<K> hashSet(K... values) {
        return new HashSet<>(Arrays.asList(values));
    }


    public static <K> HashSet<K> hashSet(Collection<K> collection) {
        if (collection == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>(collection);
        }
    }

    public static <IN, OUT> Set<OUT> transform(Set<IN> in, Function<IN, OUT> converter) {
        return in.stream().map(converter).collect(Collectors.toSet());
    }

    public static <K> Set<K> merge(Set<K> origin, K... e) {
        HashSet<K> result = hashSet(origin);
        if (e != null) {
            result.addAll(Arrays.asList(e));
        }
        return result;
    }

}

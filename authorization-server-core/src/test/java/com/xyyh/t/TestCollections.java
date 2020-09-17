package com.xyyh.t;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestCollections {

    @Test
    public void testCollections() {
        Set<String> sets = new HashSet<>();
        Set<String> unmodi = Collections.unmodifiableSet(new HashSet<>(sets));
        System.out.println(unmodi);
        sets.add("good");
        System.out.println(unmodi);
    }
}

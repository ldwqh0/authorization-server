package com.xyyh.t;

import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TestStringSplit {

    @Test
    public void spiltString() {
        MultiValueMap<String, String> v = new LinkedMultiValueMap<String, String>();
        System.out.println(v.getFirst("a"));
    }
}

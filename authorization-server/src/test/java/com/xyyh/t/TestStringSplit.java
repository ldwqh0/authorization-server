package com.xyyh.t;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TestStringSplit {

    @Test
    public void spiltString() {
        MultiValueMap<String, String> v = new LinkedMultiValueMap<String, String>();
        System.out.println(v.getFirst("a"));
    }

    @Test
    public void testa() {
        StringKeyGenerator accessTokenGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder());
        System.out.println(accessTokenGenerator.generateKey());
    }
}

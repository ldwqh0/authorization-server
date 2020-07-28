package com.xyyh.t;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TestStringSplit {

    private static final Random random = new SecureRandom();
    private static final String RandomChars = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";
    StringKeyGenerator accessTokenGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder(), 33);

    BytesKeyGenerator g = KeyGenerators.secureRandom(32);

    @Test
    public void spiltString() {
        MultiValueMap<String, String> v = new LinkedMultiValueMap<String, String>();
        System.out.println(v.getFirst("a"));
    }

    public String random(int length) {
        int max = RandomChars.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(RandomChars.charAt(random.nextInt(max)));
        }
        return builder.toString();
    }

    public String random2() {
        return accessTokenGenerator.generateKey();
    }

    public String random3() {
        return new String(Hex.encode(g.generateKey()));
    }

    public String random4() {
        return Base64.getEncoder().encodeToString(g.generateKey());
    }

    @Test
    public void Random6() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Base64.getUrlEncoder().encodeToString(KeyGenerators.secureRandom(30).generateKey()));
        }
    }

    @Test
    public void testa() {
        System.out.println(random3());
        System.out.println(random4());
        long now = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            System.out.println(random2());
        }
        System.out.println(System.currentTimeMillis() - now);
    }

}

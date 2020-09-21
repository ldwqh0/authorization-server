package com.xyyh.t;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UUIDStringKey {
    StringKeyGenerator stringKeyGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder(), 33);

    @Test
    public void test() {
        Set<String> all = new HashSet<>();
        Long cur = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            all.add(stringKeyGenerator.generateKey());
        }
        System.out.println("stringKeyGenerator共耗时:" + (System.currentTimeMillis() - cur) + ",共" + all.size() + "项目");
    }

    private byte[] toByteArray(long value) {
        // Note that this code needs to stay compatible with GWT, which has known
        // bugs when narrowing byte casts of long values occur.
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
        return result;
    }

    @Test
    public void testuuid() {
        Set<String> all = new HashSet<>();
        Long cur = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            UUID uuid = UUID.randomUUID();

            System.out.println(Base64.getEncoder().encodeToString(toByteArray(uuid.getLeastSignificantBits())));
            System.out.println(Base64.getEncoder().encodeToString(toByteArray(uuid.getMostSignificantBits())));
//            all.add(Long.toHexString(uuid.getLeastSignificantBits()) + Long.toHexString(uuid.getMostSignificantBits()));
        }
        System.out.println("UUID共耗时:" + (System.currentTimeMillis() - cur) + ",共" + all.size() + "项目");
    }
}

package com.xyyh.authorization.provider;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.xyyh.authorization.core.OAuth2Authentication;
import com.xyyh.authorization.core.OAuth2AuthorizationCodeService;

public class InMemoryAuthorizationCodeService implements OAuth2AuthorizationCodeService {
    private static final String RandomChars = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";

    // TODO 要处理过期时间
    private Map<String, OAuth2Authentication> store = new ConcurrentHashMap<>();

    @Override
    public String create(OAuth2Authentication auth) {
        String code = getRandomString(8);
        store.put(code, auth);
        return code;
    }

    @Override
    public void delete(String code) {
        this.store.remove(code);
    }

    @Override
    public OAuth2Authentication get(String code) {
        return store.get(code);
    }

    private String getRandomString(int length) {
        Random random = new Random();
        int max = RandomChars.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(RandomChars.charAt(random.nextInt(max)));
        }
        return builder.toString();
    }
}

package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2Authentication;
import com.xyyh.authorization.core.OAuth2AuthorizationCode;
import com.xyyh.authorization.core.OAuth2AuthorizationCodeService;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAuthorizationCodeService implements OAuth2AuthorizationCodeService {


    private Map<String, TokenPair> store = new ConcurrentHashMap<>();

    @Override
    public OAuth2AuthorizationCode save(OAuth2AuthorizationCode code, OAuth2Authentication authentication) {
        String codeKey = code.getValue();
        store.put(codeKey, new TokenPair(code, authentication));
        return code;
    }


    @Override
    public OAuth2Authentication consume(String code) {
        TokenPair pair = store.remove(code);
        if (Objects.isNull(pair)) {
            // TODO 抛出异常还是返回空呢？
            return null;
        } else {
            OAuth2AuthorizationCode result = pair.code;
            Instant now = Instant.now();
            if (now.isBefore(result.getExpiresAt())) {
                return pair.authentication;
            } else {
                return null;
            }
        }
    }


    private static class TokenPair {
        private final OAuth2AuthorizationCode code;
        private final OAuth2Authentication authentication;

        public TokenPair(OAuth2AuthorizationCode code, OAuth2Authentication authentication) {
            this.code = code;
            this.authentication = authentication;
        }
    }

}

package com.xyyh.authorization.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xyyh.authorization.core.OAuth2AccessTokenAuthentication;

public class InMemoryOAuth2AccessTokenService implements OAuth2AccessTokenService {

    private Map<String, OAuth2AccessTokenAuthentication> reposiotry = new ConcurrentHashMap<String, OAuth2AccessTokenAuthentication>();

    @Override
    public OAuth2AccessTokenAuthentication save(OAuth2AccessTokenAuthentication token) {
        String tokenValue = token.getAccessToken().getTokenValue();
        this.reposiotry.put(tokenValue, token);
        return token;
    }

    @Override
    public void delete(String token) {
        this.reposiotry.remove(token);
    }

    @Override
    public OAuth2AccessTokenAuthentication get(String accessToken) {
        return this.reposiotry.get(accessToken);
    }

}

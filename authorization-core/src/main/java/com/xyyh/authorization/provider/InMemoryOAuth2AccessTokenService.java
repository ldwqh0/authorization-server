package com.xyyh.authorization.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import com.xyyh.authorization.core.OAuth2Authentication;

public class InMemoryOAuth2AccessTokenService extends AbstractOAuth2AccessTokenService {

    private final Map<String, OAuth2Authentication> authenticationReposiotry = new ConcurrentHashMap<>();

    private final Map<String, OAuth2AccessToken> tokenRepository = new ConcurrentHashMap<>();

    @Override
    public void delete(String token) {
        this.authenticationReposiotry.remove(token);
        this.tokenRepository.remove(token);
    }

    @Override
    protected void save(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String tokenKey = accessToken.getTokenValue();
        this.authenticationReposiotry.put(tokenKey, authentication);
        this.tokenRepository.put(tokenKey, accessToken);
    }

    @Override
    public OAuth2Authentication getAuthentication(String accessToken) {
        return this.authenticationReposiotry.get(accessToken);
    }

    @Override
    public OAuth2AccessToken getAccessToken(String accessToken) {
        return this.tokenRepository.get(accessToken);
    }

}

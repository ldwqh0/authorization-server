package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOAuth2AccessTokenService implements OAuth2AccessTokenService {

    private final Map<String, OAuth2Authentication> authenticationRepository = new ConcurrentHashMap<>();

    private final Map<String, OAuth2AccessToken> tokenRepository = new ConcurrentHashMap<>();

    @Override
    public void delete(String token) {
        this.authenticationRepository.remove(token);
        this.tokenRepository.remove(token);
    }

    @Override
    public OAuth2AccessToken save(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String tokenKey = accessToken.getTokenValue();
        this.authenticationRepository.put(tokenKey, authentication);
        this.tokenRepository.put(tokenKey, accessToken);
        return accessToken;
    }

    @Override
    public OAuth2Authentication getAuthentication(String accessToken) {
        return this.authenticationRepository.get(accessToken);
    }

    @Override
    public OAuth2AccessToken getAccessToken(String accessToken) {
        return this.tokenRepository.get(accessToken);
    }

    @Override
    public OAuth2RefreshToken createRefreshToken(OAuth2AccessToken accessToken) {
        // TODO Auto-generated method stub
        return null;
    }

}

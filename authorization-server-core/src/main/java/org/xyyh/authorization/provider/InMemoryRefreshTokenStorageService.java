package org.xyyh.authorization.provider;

import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2RefreshTokenStorageService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRefreshTokenStorageService implements OAuth2RefreshTokenStorageService {
    private final Map<String, OAuth2RefreshToken> tokenRepository = new ConcurrentHashMap<>();
    private final Map<String, OAuth2Authentication> authenticationRepository = new ConcurrentHashMap<>();

    @Override
    public OAuth2RefreshToken save(OAuth2RefreshToken token, OAuth2Authentication authentication) {
        String tokenKey = token.getTokenValue();
        tokenRepository.put(tokenKey, token);
        authenticationRepository.put(tokenKey, authentication);
        return token;
    }

    @Override
    public void delete(String tokenValue) {
        tokenRepository.remove(tokenValue);
        authenticationRepository.remove(tokenValue);
    }

    @Override
    public OAuth2Authentication getAuthentication(String tokenValue) {
        return authenticationRepository.get(tokenValue);
    }

    @Override
    public OAuth2RefreshToken getAccessToken(String tokenValue) {
        return tokenRepository.get(tokenValue);
    }
}

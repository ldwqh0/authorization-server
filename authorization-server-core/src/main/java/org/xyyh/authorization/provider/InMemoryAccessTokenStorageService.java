package org.xyyh.authorization.provider;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.xyyh.authorization.core.OAuth2AccessTokenStore;
import org.xyyh.authorization.core.OAuth2Authentication;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccessTokenStorageService implements OAuth2AccessTokenStore {

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
    public Optional<OAuth2Authentication> getAuthentication(String accessToken) {
        return Optional.ofNullable(this.authenticationRepository.get(accessToken));
    }

    @Override
    public Optional<OAuth2AccessToken> getAccessToken(String accessToken) {
        return Optional.ofNullable(this.tokenRepository.get(accessToken));
    }

    @Override
    public Optional<OAuth2AccessToken> getAccessToken(OAuth2Authentication authentication) {
        // TODO 这里待处理
        return null;
    }

}

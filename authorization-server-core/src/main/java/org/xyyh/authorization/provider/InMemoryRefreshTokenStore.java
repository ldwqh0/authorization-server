package org.xyyh.authorization.provider;

import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2RefreshTokenStore;
import org.xyyh.authorization.core.OAuth2ServerRefreshToken;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRefreshTokenStore implements OAuth2RefreshTokenStore {
    private final Map<String, OAuth2ServerRefreshToken> tokenRepository = new ConcurrentHashMap<>();
    private final Map<String, OAuth2Authentication> authenticationRepository = new ConcurrentHashMap<>();
    /**
     * the key is refresh token ,the value is access token
     */
    private final Map<String, String> accessTokenRepository = new ConcurrentHashMap<>();

    @Override
    public OAuth2ServerRefreshToken save(OAuth2ServerRefreshToken token, String accessTokenValue, OAuth2Authentication authentication) {
        String tokenKey = token.getTokenValue();
        tokenRepository.put(tokenKey, token);
        authenticationRepository.put(tokenKey, authentication);
        accessTokenRepository.put(tokenKey, accessTokenValue);
        return token;
    }

    @Override
    public void delete(String tokenValue) {
        tokenRepository.remove(tokenValue);
        authenticationRepository.remove(tokenValue);
        accessTokenRepository.remove(tokenValue);
    }

    @Override
    public Optional<OAuth2Authentication> getAuthentication(String tokenValue) {
        return Optional.ofNullable(authenticationRepository.get(tokenValue));
    }

    @Override
    public Optional<OAuth2ServerRefreshToken> getToken(String tokenValue) {
        return Optional.ofNullable(tokenRepository.get(tokenValue));
    }

    @Override
    public Optional<String> getAccessToken(String refreshToken) {
        return Optional.ofNullable(accessTokenRepository.get(refreshToken));
    }

    @Override
    public Optional<OAuth2ServerRefreshToken> findByAccessToken(String accessToken) {
        return accessTokenRepository.entrySet().stream()
            .filter(entry -> entry.getValue().equals(accessToken))
            .findAny()
            .map(Map.Entry::getKey)
            .map(tokenRepository::get);
    }
}

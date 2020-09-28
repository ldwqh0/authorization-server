package org.xyyh.authorization.provider;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.xyyh.authorization.core.OAuth2AccessTokenStore;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2ServerAccessToken;
import org.xyyh.authorization.core.OAuth2ServerRefreshToken;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccessTokenStore implements OAuth2AccessTokenStore {

    /**
     * the key is access token value ,the value is OAuth2Authentication
     */
    private final Map<String, OAuth2Authentication> tokenValue2AuthenticationRepository = new ConcurrentHashMap<>();

    /**
     * the key is token value,the value is OAuth2AccessToken
     */
    private final Map<String, OAuth2ServerAccessToken> tokenValue2AccessTokenRepository = new ConcurrentHashMap<>();

    /**
     * the key is extract authentication key,the value is OAuth2AccessToken
     */
    private final Map<String, OAuth2ServerAccessToken> authentication2AccessTokenRepository = new ConcurrentHashMap<>();

    /**
     * the key is refresh token ,the value is access token
     */
    private final Map<String, String> refreshToken2AccessTokenRepository = new ConcurrentHashMap<>();


    @Override
    public void delete(String token) {
        OAuth2Authentication authentication = this.tokenValue2AuthenticationRepository.remove(token);
        OAuth2ServerAccessToken accessToken = this.tokenValue2AccessTokenRepository.remove(token);
        Optional.ofNullable(authentication)
                .map(this::extractAuthenticationKey)
                .ifPresent(authentication2AccessTokenRepository::remove);
        Optional.ofNullable(accessToken)
                .flatMap(OAuth2ServerAccessToken::getRefreshToken)
                .map(OAuth2ServerRefreshToken::getTokenValue)
                .ifPresent(refreshToken2AccessTokenRepository::remove);
    }

    @Override
    public OAuth2ServerAccessToken save(OAuth2ServerAccessToken accessToken, OAuth2Authentication authentication) {
        String tokenKey = accessToken.getTokenValue();
        String authenticationKey = extractAuthenticationKey(authentication);
        this.tokenValue2AuthenticationRepository.put(tokenKey, authentication);
        this.tokenValue2AccessTokenRepository.put(tokenKey, accessToken);
        this.authentication2AccessTokenRepository.put(authenticationKey, accessToken);
        accessToken.getRefreshToken()
                .map(OAuth2ServerRefreshToken::getTokenValue)
                .ifPresent(refreshToken -> this.refreshToken2AccessTokenRepository.put(refreshToken, tokenKey));
        return accessToken;
    }

    @Override
    public Optional<OAuth2Authentication> loadAuthentication(String accessToken) {
        return Optional.ofNullable(this.tokenValue2AuthenticationRepository.get(accessToken));
    }

    @Override
    public Optional<OAuth2ServerAccessToken> getAccessToken(String accessToken) {
        return Optional.ofNullable(this.tokenValue2AccessTokenRepository.get(accessToken));
    }

    @Override
    public Optional<OAuth2ServerAccessToken> getAccessToken(OAuth2Authentication authentication) {
        String authenticationKey = extractAuthenticationKey(authentication);
        return Optional.ofNullable(authentication2AccessTokenRepository.get(authenticationKey));
    }

    @Override
    public Optional<OAuth2Authentication> loadAuthenticationByRefreshToken(String refreshToken) {
        return Optional.ofNullable(refreshToken)
                .map(this.refreshToken2AccessTokenRepository::get)
                .map(this.tokenValue2AuthenticationRepository::get);
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        String accessTokenValue = refreshToken2AccessTokenRepository.get(refreshToken);
        delete(accessTokenValue);
    }

    private String extractAuthenticationKey(OAuth2Authentication authentication) {
        String clientId = authentication.getClient().getClientId();
        List<String> scopes = new ArrayList<>(authentication.getScopes());
        Collections.sort(scopes);
        String name = authentication.getName();
        return DigestUtils.md5Hex(StringUtils.join("client:", clientId, "scopes:", StringUtils.join(scopes, ","), "name:", name));
    }
}

package org.xyyh.authorization.provider;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.xyyh.authorization.core.OAuth2AccessTokenStore;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2ServerAccessToken;
import org.xyyh.authorization.core.OAuth2ServerRefreshToken;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在内存中保存access token,如果使用该种形式保存，access token value和access token id是一致的
 */
public class InMemoryAccessTokenStore implements OAuth2AccessTokenStore {

    /**
     * the key is access token id ,the value is OAuth2Authentication
     */
    private final Map<String, OAuth2Authentication> tokenKeyAuthenticationRepository = new ConcurrentHashMap<>();

    /**
     * the key is token id,the value is OAuth2AccessToken
     */
    private final Map<String, OAuth2ServerAccessToken> tokenKey2AccessTokenRepository = new ConcurrentHashMap<>();

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
        OAuth2Authentication authentication = this.tokenKeyAuthenticationRepository.remove(token);
        OAuth2ServerAccessToken accessToken = this.tokenKey2AccessTokenRepository.remove(token);
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
        String tokenKey = accessToken.getId();
        String authenticationKey = extractAuthenticationKey(authentication);
        this.tokenKeyAuthenticationRepository.put(tokenKey, authentication);
        this.tokenKey2AccessTokenRepository.put(tokenKey, accessToken);
        this.authentication2AccessTokenRepository.put(authenticationKey, accessToken);
        accessToken.getRefreshToken()
            .map(OAuth2ServerRefreshToken::getTokenValue)
            .ifPresent(refreshToken -> this.refreshToken2AccessTokenRepository.put(refreshToken, tokenKey));
        return accessToken;
    }

    @Override
    public Optional<OAuth2Authentication> loadAuthentication(String accessToken) {
        return Optional.ofNullable(accessToken)
            .map(this.tokenKey2AccessTokenRepository::get)
            .filter(this::preCheckAccessToken)
            .map(OAuth2ServerAccessToken::getTokenValue)
            .map(this.tokenKeyAuthenticationRepository::get);
    }

    @Override
    public Optional<OAuth2ServerAccessToken> getAccessToken(String accessToken) {
        return Optional.ofNullable(accessToken)
            .map(this.tokenKey2AccessTokenRepository::get)
            .filter(this::preCheckAccessToken);
    }

    @Override
    public Optional<OAuth2ServerRefreshToken> getRefreshToken(String refreshToken) {
        return Optional.ofNullable(refreshToken)
            .map(refreshToken2AccessTokenRepository::get)
            .map(tokenKey2AccessTokenRepository::get)
            .flatMap(OAuth2ServerAccessToken::getRefreshToken)
            .filter(this::preCheckRefreshToken);
    }

    @Override
    public Optional<OAuth2ServerAccessToken> getAccessToken(OAuth2Authentication authentication) {
        return Optional.ofNullable(authentication)
            .map(this::extractAuthenticationKey)
            .map(authentication2AccessTokenRepository::get)
            .filter(this::preCheckAccessToken);
    }

    @Override
    public Optional<OAuth2Authentication> loadAuthenticationByRefreshToken(String refreshToken) {
        return Optional.ofNullable(refreshToken)
            .map(this.refreshToken2AccessTokenRepository::get) // 获取access token value
            .map(this.tokenKey2AccessTokenRepository::get) // 获取access token
            .flatMap(accessToken -> accessToken.getRefreshToken()
                .filter(this::preCheckRefreshToken)
                .map(rt -> this.tokenKeyAuthenticationRepository.get(accessToken.getId()))
            );
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

    private boolean preCheckAccessToken(OAuth2ServerAccessToken accessToken) {
        if (Instant.now().isAfter(accessToken.getExpiresAt())) {
            this.delete(accessToken.getId());
            return false;
        }
        return true;
    }

    private boolean preCheckRefreshToken(OAuth2ServerRefreshToken refreshToken) {
        if (Instant.now().isAfter(refreshToken.getExpiresAt())) {
            this.deleteByRefreshToken(refreshToken.getTokenValue());
            return false;
        }
        return true;
    }
}

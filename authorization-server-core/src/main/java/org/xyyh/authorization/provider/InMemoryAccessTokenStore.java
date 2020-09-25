package org.xyyh.authorization.provider;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.xyyh.authorization.core.OAuth2AccessTokenStore;
import org.xyyh.authorization.core.OAuth2Authentication;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccessTokenStore implements OAuth2AccessTokenStore {

    /**
     * the key is access token value ,the value is OAuth2Authentication
     */
    private final Map<String, OAuth2Authentication> authenticationRepository = new ConcurrentHashMap<>();

    /**
     * the key is token value,the value is OAuth2AccessToken
     */
    private final Map<String, OAuth2AccessToken> tokenValueRepository = new ConcurrentHashMap<>();

    /**
     * the key is extract authentication key,the value is OAuth2AccessToken
     */
    private final Map<String, OAuth2AccessToken> authenticationAccessTokenRepository = new ConcurrentHashMap<>();


    @Override
    public void delete(String token) {
        OAuth2Authentication authentication = this.authenticationRepository.remove(token);
        this.tokenValueRepository.remove(token);
        Optional.ofNullable(authentication)
            .map(this::extractAuthenticationKey)
            .ifPresent(authenticationAccessTokenRepository::remove);
    }

    @Override
    public OAuth2AccessToken save(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String tokenKey = accessToken.getTokenValue();
        String authenticationKey = extractAuthenticationKey(authentication);
        this.authenticationRepository.put(tokenKey, authentication);
        this.tokenValueRepository.put(tokenKey, accessToken);
        this.authenticationAccessTokenRepository.put(authenticationKey, accessToken);
        return accessToken;
    }

    @Override
    public Optional<OAuth2Authentication> getAuthentication(String accessToken) {
        return Optional.ofNullable(this.authenticationRepository.get(accessToken));
    }

    @Override
    public Optional<OAuth2AccessToken> getAccessToken(String accessToken) {
        return Optional.ofNullable(this.tokenValueRepository.get(accessToken));
    }

    @Override
    public Optional<OAuth2AccessToken> getAccessToken(OAuth2Authentication authentication) {
        String authenticationKey = extractAuthenticationKey(authentication);
        return Optional.ofNullable(authenticationAccessTokenRepository.get(authenticationKey));
    }

    private String extractAuthenticationKey(OAuth2Authentication authentication) {
        String clientId = authentication.getClient().getClientId();
        List<String> scopes = new ArrayList<>(authentication.getScopes());
        Collections.sort(scopes);
        String name = authentication.getName();
        return DigestUtils.md5Hex(StringUtils.join("client:", clientId, "scopes:", StringUtils.join(scopes, ","), "name:", name));
    }

}

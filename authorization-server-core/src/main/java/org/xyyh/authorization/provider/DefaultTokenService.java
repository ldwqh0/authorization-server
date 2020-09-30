package org.xyyh.authorization.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.collect.CollectionUtils;
import org.xyyh.authorization.core.*;
import org.xyyh.authorization.exception.RefreshTokenValidationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.xyyh.authorization.collect.Sets.hashSet;

public class DefaultTokenService implements OAuth2AuthorizationServerTokenServices, OAuth2ResourceServerTokenServices {

    private Integer defaultAccessTokenValiditySeconds = 3600;
    private Integer defaultRefreshTokenValiditySeconds = 7200;

    private final OAuth2AccessTokenStore accessTokenStore;

    private final StringKeyGenerator stringGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder(), 33);

    /**
     * 对用户进行无密码的校验<br>
     * 用于refresh token校验
     */
    private ProviderManager preProviderManager;

    @Autowired(required = false)
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        if (userDetailsService != null) {
            this.preProviderManager = new ProviderManager(Arrays.asList(new PreAuthenticatedProvider(userDetailsService)));
        }
    }

    public DefaultTokenService(OAuth2AccessTokenStore accessTokenStore) {
        this.accessTokenStore = accessTokenStore;
    }

    public void deleteAccessToken(String accessToken) {
        accessTokenStore.delete(accessToken);
    }

    @Override
    public OAuth2ServerAccessToken createAccessToken(OAuth2Authentication authentication) {
        OAuth2ServerAccessToken existingAccessToken = accessTokenStore.getAccessToken(authentication).orElse(null);
        if (existingAccessToken != null) {
            if (Instant.now().isAfter(existingAccessToken.getExpiresAt())) {
                accessTokenStore.delete(existingAccessToken.getTokenValue());
            } else {
                // Re-store the access token in case the authentication has changed
                accessTokenStore.save(existingAccessToken, authentication);
                return existingAccessToken;
            }
        }
        ClientDetails client = authentication.getClient();
        OAuth2ServerAccessToken accessToken = generateAccessToken(client, authentication.getScopes());
        return accessTokenStore.save(accessToken, authentication);
    }

    @Override
    public OAuth2ServerAccessToken refreshAccessToken(String refreshToken, ClientDetails client, Collection<String> requestScopes) throws RefreshTokenValidationException {
        final String internRefreshTokenValue = refreshToken.intern();
        // 对token进行预检，如果检测失败，抛出异常
        accessTokenStore.loadAuthenticationByRefreshToken(refreshToken).orElseThrow(RefreshTokenValidationException::new);
        // 同一时刻，针对用一个refresh token,有且仅有一个线程可以读取某个refresh token的相关信息
        synchronized (internRefreshTokenValue) {
            // 进行双重检查
            OAuth2Authentication preAuthentication = accessTokenStore.loadAuthenticationByRefreshToken(refreshToken).orElseThrow(RefreshTokenValidationException::new);
            // 验证传入的refresh token是否发布给该client
            if (!Objects.equals(preAuthentication.getClient().getClientId(), client.getClientId())) {
                throw new RefreshTokenValidationException("client validate failure");
            }
            // 验证重新请求的scope不能不能大于之前的scope
            Set<String> scopeToUse = preAuthentication.getScopes();
            if (!CollectionUtils.containsAll(preAuthentication.getScopes(), requestScopes)) {
                throw new RefreshTokenValidationException("scope validate failure");
            }
            if (CollectionUtils.isNotEmpty(requestScopes)) {
                scopeToUse = hashSet(requestScopes);
            }
            // 使用refreshToken时,需要重新加载用户的信息
            Authentication user = new PreAuthenticatedAuthenticationToken(preAuthentication, preAuthentication.getAuthorities());
            user = preProviderManager.authenticate(user);
            // 创建一个新的OAuth2Authentication
            OAuth2Authentication authentication = OAuth2Authentication.of(preAuthentication.getRequest(), ApprovalResult.empty(scopeToUse), client, user);
            // 删除之前的access token
            accessTokenStore.deleteByRefreshToken(internRefreshTokenValue);
            // 创建一个新的token
            OAuth2ServerAccessToken accessToken = generateAccessToken(client, authentication.getScopes());

            return accessTokenStore.save(accessToken, authentication);
        }
    }

    @Override
    public Optional<OAuth2Authentication> loadAuthentication(String accessToken) {
        return accessTokenStore.loadAuthentication(accessToken);
    }

    @Override
    public Optional<OAuth2ServerAccessToken> readAccessToken(String accessToken) {
        // TODO 这里处理过期
        return accessTokenStore.getAccessToken(accessToken);
    }

    /**
     * 验证请求是否支持refresh_token
     *
     * @param client 待验证的client
     * @return 支持返回true, 不支持返回false
     */
    private boolean isSupportRefreshToken(ClientDetails client) {
        return this.preProviderManager != null && client.getAuthorizedGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN);
    }

    private OAuth2ServerAccessToken generateAccessToken(ClientDetails client, Set<String> scopes) {
        Instant issuedAt = Instant.now();
        Integer accessTokenValiditySeconds = Optional.ofNullable(client.getAccessTokenValiditySeconds()).orElse(defaultAccessTokenValiditySeconds);
        Instant expiresAt = issuedAt.plus(accessTokenValiditySeconds, ChronoUnit.SECONDS);
        String tokenId = stringGenerator.generateKey();
        OAuth2ServerRefreshToken refreshToken = null;
        if (isSupportRefreshToken(client)) {
            refreshToken = generateRefreshToken(client);
        }
        return OAuth2ServerAccessToken.of(tokenId, OAuth2AccessToken.TokenType.BEARER, tokenId, issuedAt, expiresAt, scopes, refreshToken);
    }

    private OAuth2ServerRefreshToken generateRefreshToken(ClientDetails client) {
        Instant issuedAt = Instant.now();
        Integer validitySeconds = Optional.ofNullable(client.getRefreshTokenValiditySeconds()).orElse(defaultRefreshTokenValiditySeconds);
        Instant expiresAt = issuedAt.plus(validitySeconds, ChronoUnit.SECONDS);
        String tokenValue = stringGenerator.generateKey();
        return OAuth2ServerRefreshToken.of(tokenValue, issuedAt, expiresAt);
    }
}

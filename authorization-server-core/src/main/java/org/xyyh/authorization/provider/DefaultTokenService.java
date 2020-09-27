package org.xyyh.authorization.provider;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.core.*;
import org.xyyh.authorization.exception.RefreshTokenValidationException;

import java.time.Instant;
import java.util.Optional;

public class DefaultTokenService implements OAuth2AuthorizationServerTokenServices, OAuth2ResourceServerTokenServices {

    private final TokenGenerator tokenGenerator;

    private final OAuth2AccessTokenStore accessTokenStore;

    private final OAuth2RefreshTokenStore refreshTokenStore;

    public DefaultTokenService(TokenGenerator tokenGenerator, OAuth2AccessTokenStore accessTokenStore, OAuth2RefreshTokenStore refreshTokenStore) {
        this.tokenGenerator = tokenGenerator;
        this.accessTokenStore = accessTokenStore;
        this.refreshTokenStore = refreshTokenStore;
    }

    public void deleteAccessToken(String accessToken) {
        accessTokenStore.delete(accessToken);
        refreshTokenStore.findByAccessToken(accessToken)
            .map(OAuth2RefreshToken::getTokenValue).ifPresent(refreshTokenStore::delete);
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken existingAccessToken = accessTokenStore.getAccessToken(authentication).orElse(null);
        if (existingAccessToken != null) {
            if (Instant.now().isAfter(existingAccessToken.getExpiresAt())) {
                // if (existingAccessToken.getRefreshToken() != null) {
                //     refreshToken = existingAccessToken.getRefreshToken();
                //     // The token store could remove the refresh token when the
                //     // access token is removed, but we want to
                //     // be sure...
                //     tokenStore.removeRefreshToken(refreshToken);
                // }
                accessTokenStore.delete(existingAccessToken.getTokenValue());
            } else {
                // Re-store the access token in case the authentication has changed
                accessTokenStore.save(existingAccessToken, authentication);
                return existingAccessToken;
            }
        }
        ClientDetails client = authentication.getClient();
        OAuth2AccessToken accessToken = tokenGenerator.generateAccessToken(authentication.getScopes(), client.getAccessTokenValiditySeconds());
        return accessTokenStore.save(accessToken, authentication);
    }

    @Override
    public OAuth2RefreshToken createRefreshToken(String accessToken, ClientDetails client) {
        // 如果有现成的refresh token，直接返回现有的
        return refreshTokenStore.findByAccessToken(accessToken).orElseGet(() -> {
            OAuth2RefreshToken refreshToken = tokenGenerator.generateRefreshToken(client);
            return accessTokenStore.getAuthentication(accessToken)
                .map(authentication -> refreshTokenStore.save(refreshToken, accessToken, authentication))
                .orElseThrow(RuntimeException::new);
        });
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshToken, OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = tokenGenerator.generateAccessToken(authentication.getScopes(), authentication.getClient().getAccessTokenValiditySeconds());
        refreshTokenStore.getAccessToken(refreshToken).ifPresent(accessTokenStore::delete);
        return accessTokenStore.save(accessToken, authentication);
    }


    /**
     * 对 refresh token 进行预检测
     *
     * @param refreshToken 待检测的token值
     */
    @Override
    public void preCheckRefreshToken(String refreshToken) throws RefreshTokenValidationException {
        refreshTokenStore.getToken(refreshToken)
            .map(OAuth2RefreshToken::getTokenValue)
            .flatMap(refreshTokenStore::getAuthentication)
            .orElseThrow(RefreshTokenValidationException::new);
    }

    @Override
    public Optional<OAuth2Authentication> loadAuthenticationByRefreshToken(String refreshToken) {
        return refreshTokenStore.getAuthentication(refreshToken);
    }

    @Override
    public Optional<OAuth2Authentication> loadAuthentication(String accessToken) {
        return accessTokenStore.getAuthentication(accessToken);
    }

    @Override
    public Optional<OAuth2AccessToken> readAccessToken(String accessToken) {
        return accessTokenStore.getAccessToken(accessToken);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenStore.delete(refreshToken);
    }
}

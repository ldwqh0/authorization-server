package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Server端的access token定义
 */
public interface OAuth2ServerAccessToken extends Serializable {

    String getTokenValue();

    Instant getIssuedAt();

    Instant getExpiresAt();

    OAuth2AccessToken.TokenType getTokenType();

    Set<String> getScopes();

    Optional<OAuth2ServerRefreshToken> getRefreshToken();

    static OAuth2ServerAccessToken of(OAuth2AccessToken.TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes, OAuth2ServerRefreshToken refreshToken) {
        return new DefaultOAuth2ServerAccessToken(tokenType, tokenValue, issuedAt, expiresAt, scopes, refreshToken);
    }
}


/**
 * 默认的OAuth2ServerAccessToken实现
 */
class DefaultOAuth2ServerAccessToken extends OAuth2AccessToken implements OAuth2ServerAccessToken {

    private final OAuth2ServerRefreshToken refreshToken;

    public DefaultOAuth2ServerAccessToken(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt) {
        super(tokenType, tokenValue, issuedAt, expiresAt);
        this.refreshToken = null;
    }

    public DefaultOAuth2ServerAccessToken(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
        super(tokenType, tokenValue, issuedAt, expiresAt, scopes);
        this.refreshToken = null;
    }

    public DefaultOAuth2ServerAccessToken(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes, OAuth2ServerRefreshToken refreshToken) {
        super(tokenType, tokenValue, issuedAt, expiresAt, scopes);
        this.refreshToken = refreshToken;
    }


    @Override
    public Optional<OAuth2ServerRefreshToken> getRefreshToken() {
        return Optional.ofNullable(this.refreshToken);
    }
}

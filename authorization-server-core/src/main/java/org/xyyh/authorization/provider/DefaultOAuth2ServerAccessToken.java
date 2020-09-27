package org.xyyh.authorization.provider;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.xyyh.authorization.core.OAuth2ServerAccessToken;
import org.xyyh.authorization.core.OAuth2ServerRefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * 默认的OAuth2ServerAccessToken实现
 */
public class DefaultOAuth2ServerAccessToken extends OAuth2AccessToken implements OAuth2ServerAccessToken {

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

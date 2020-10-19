package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Server端的access token定义
 */
public interface OAuth2ServerAccessToken extends OAuth2ServerToken, Serializable {

    String getId();

    OAuth2AccessToken.TokenType getTokenType();

    Set<String> getScopes();

    Optional<OAuth2ServerRefreshToken> getRefreshToken();

    static OAuth2ServerAccessToken of(String id, OAuth2AccessToken.TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes, OAuth2ServerRefreshToken refreshToken) {
        return new DefaultOAuth2ServerAccessToken(id, tokenType, tokenValue, issuedAt, expiresAt, scopes, refreshToken);
    }

    static OAuth2ServerAccessToken of(OAuth2AccessToken.TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes, OAuth2ServerRefreshToken refreshToken) {
        return new DefaultOAuth2ServerAccessToken(tokenValue, tokenType, tokenValue, issuedAt, expiresAt, scopes, refreshToken);
    }
}


/**
 * 默认的OAuth2ServerAccessToken实现
 */
class DefaultOAuth2ServerAccessToken extends OAuth2AccessToken implements OAuth2ServerAccessToken {

    private static final long serialVersionUID = 6322641771177832776L;
    private final OAuth2ServerRefreshToken refreshToken;
    private final String id;

    public DefaultOAuth2ServerAccessToken(String id, TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt) {
        super(tokenType, tokenValue, issuedAt, expiresAt);
        this.id = id;
        this.refreshToken = null;
    }

    public DefaultOAuth2ServerAccessToken(String id, TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
        super(tokenType, tokenValue, issuedAt, expiresAt, scopes);
        this.refreshToken = null;
        this.id = id;
    }

    public DefaultOAuth2ServerAccessToken(String id, TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes, OAuth2ServerRefreshToken refreshToken) {
        super(tokenType, tokenValue, issuedAt, expiresAt, scopes);
        this.refreshToken = refreshToken;
        this.id = id;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public Optional<OAuth2ServerRefreshToken> getRefreshToken() {
        return Optional.ofNullable(this.refreshToken);
    }
}

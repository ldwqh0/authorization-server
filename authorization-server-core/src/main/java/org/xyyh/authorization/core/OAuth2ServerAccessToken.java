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
}

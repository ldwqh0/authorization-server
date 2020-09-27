package org.xyyh.authorization.provider;

import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.xyyh.authorization.core.OAuth2ServerRefreshToken;

import java.time.Instant;

/**
 * 服务端的refresh token实现，有别于客户端的refresh token实现，它需要保存失效信息
 *
 * @see org.springframework.security.oauth2.core.OAuth2RefreshToken
 */
public class DefaultOAuth2RefreshToken extends AbstractOAuth2Token implements OAuth2ServerRefreshToken {
    /**
     * Constructs an {@code OAuth2RefreshToken} using the provided parameters.
     *
     * @param tokenValue the token value
     * @param issuedAt   the time at which the token was issued
     */
    public DefaultOAuth2RefreshToken(String tokenValue, Instant issuedAt, Instant expiresAt) {
        super(tokenValue, issuedAt, expiresAt);
    }
}

package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.AbstractOAuth2Token;

import java.io.Serializable;
import java.time.Instant;

public interface OAuth2ServerRefreshToken extends Serializable {
    String getTokenValue();

    static OAuth2ServerRefreshToken of(String tokenValue, Instant issuedAt, Instant expiresAt) {
        return new DefaultOAuth2ServerRefreshToken(tokenValue, issuedAt, expiresAt);
    }
}


/**
 * 服务端的refresh token实现，有别于客户端的refresh token实现，它需要保存失效信息
 *
 * @see org.springframework.security.oauth2.core.OAuth2RefreshToken
 */
class DefaultOAuth2ServerRefreshToken extends AbstractOAuth2Token implements OAuth2ServerRefreshToken {
    private static final long serialVersionUID = 6160931455353253429L;

    /**
     * Constructs an {@code OAuth2RefreshToken} using the provided parameters.
     *
     * @param tokenValue the token value
     * @param issuedAt   the time at which the token was issued
     */
    public DefaultOAuth2ServerRefreshToken(String tokenValue, Instant issuedAt, Instant expiresAt) {
        super(tokenValue, issuedAt, expiresAt);
    }

}


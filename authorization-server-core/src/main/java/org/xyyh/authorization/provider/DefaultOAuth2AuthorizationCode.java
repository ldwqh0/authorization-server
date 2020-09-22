package org.xyyh.authorization.provider;

import org.xyyh.authorization.core.OAuth2AuthorizationCode;

import java.time.Instant;

/**
 * 默认的授权码
 */
public class DefaultOAuth2AuthorizationCode implements OAuth2AuthorizationCode {

    private final String value;

    private final Instant issuedAt;

    private final Instant expiresAt;

    public DefaultOAuth2AuthorizationCode(String value, Instant issuedAt, Instant expiresAt) {
        this.value = value;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Instant getIssuedAt() {
        return issuedAt;
    }

    @Override
    public Instant getExpiresAt() {
        return expiresAt;
    }
}

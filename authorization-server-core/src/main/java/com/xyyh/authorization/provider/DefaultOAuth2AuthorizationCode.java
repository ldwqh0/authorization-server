package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2AuthorizationCode;

import java.time.Instant;

public class DefaultOAuth2AuthorizationCode implements OAuth2AuthorizationCode {

    private String value;

    private Instant issuedAt;

    private Instant expiresAt;

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

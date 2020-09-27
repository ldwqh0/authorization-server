package org.xyyh.authorization.core;

import java.io.Serializable;
import java.time.Instant;

/**
 * 授权码
 */
public interface OAuth2AuthorizationCode extends Serializable {
    /**
     * 获取token值
     */
    String getValue();

    /**
     * 创建时间
     */
    Instant getIssuedAt();

    /**
     * 过期时间
     */
    Instant getExpiresAt();

    static OAuth2AuthorizationCode of(String value, Instant issuedAt, Instant expiresAt) {
        return new DefaultOAuth2AuthorizationCode(value, issuedAt, expiresAt);
    }

}

/**
 * 默认的授权码实现
 */
class DefaultOAuth2AuthorizationCode implements OAuth2AuthorizationCode {
    private static final long serialVersionUID = -6847663525050656084L;
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

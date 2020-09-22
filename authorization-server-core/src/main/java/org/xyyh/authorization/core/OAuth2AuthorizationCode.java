package org.xyyh.authorization.core;

import java.time.Instant;

/**
 * 授权码
 */
public interface OAuth2AuthorizationCode {
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

}

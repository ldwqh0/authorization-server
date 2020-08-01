package com.xyyh.authorization.core;

import java.time.Instant;

public interface OAuth2AuthorizationCode {
    /**
     * 获取token值
     *
     * @return
     */
    public String getValue();

    /**
     * 创建时间
     *
     * @return
     */
    public Instant getIssuedAt();

    /**
     * 过期时间
     *
     * @return
     */
    public Instant getExpiresAt();

}

package org.xyyh.authorization.client;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.io.Serializable;
import java.util.Set;

/**
 * 一个oauth client信息
 */

public interface ClientDetails extends Serializable {

    boolean isAutoApproval();

    /**
     * 应用access_token过期时间
     *
     * @return 过期时间，单位是秒
     */
    Integer getAccessTokenValiditySeconds();

    /**
     * 应用refresh_token过期时间
     *
     * @return 过期时间，单位是秒
     */
    Integer getRefreshTokenValiditySeconds();

    /**
     * 应用的ID
     */
    String getClientId();

    /**
     * 应用的密钥
     */
    String getClientSecret();

    /**
     * 应用的scope
     */
    Set<String> getScope();

    Set<AuthorizationGrantType> getAuthorizedGrantTypes();

    Set<String> getRegisteredRedirectUris();

    /**
     * 确定某个client是否需要pkce验证
     *
     * @return
     */
    boolean isRequirePkce();

}

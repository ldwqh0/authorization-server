package org.xyyh.authorization.client;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.io.Serializable;
import java.util.Set;

public interface ClientDetails extends Serializable {

    boolean isAutoApproval();

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

}

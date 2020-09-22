package org.xyyh.authorization.core;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;

import java.util.Set;

/**
 * 一个 oauth2 授权信息，包括授权用户信息，授权 scope 和 client 信息
 */
public interface OAuth2Authentication extends Authentication, CredentialsContainer {

    /**
     * the clientId
     */
    String getClientId();

    /**
     * the authorized scopes
     */
    Set<String> getScopes();

    /**
     * the {@link  OpenidAuthorizationRequest}
     *
     * @see OpenidAuthorizationRequest
     */
    OpenidAuthorizationRequest getRequest();

}

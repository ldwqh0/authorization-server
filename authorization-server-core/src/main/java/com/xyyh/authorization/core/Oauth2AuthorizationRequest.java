package com.xyyh.authorization.core;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface Oauth2AuthorizationRequest extends Serializable {
    Set<String> getResponseTypes();

    String getAuthorizationUri();

    AuthorizationGrantType getAuthorizationGrantType();

    String getClientId();

    String getRedirectUri();

    Set<String> getScopes();

    String getState();

    Map<String, Object> getAdditionalParameters();

    String getAuthorizationRequestUri();

    Map<String, Object> getAttributes();
}

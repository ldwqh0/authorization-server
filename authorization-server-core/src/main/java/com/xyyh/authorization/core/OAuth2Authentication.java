package com.xyyh.authorization.core;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;

import java.util.Set;

public interface OAuth2Authentication extends Authentication, CredentialsContainer {

    String getClientId();

    Set<String> getScopes();

    Oauth2AuthorizationRequest getRequest();

}

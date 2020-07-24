package com.xyyh.authorization.core;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;

public interface OAuth2Authentication extends Authentication, CredentialsContainer {

    public String getClientId();

    public Set<String> getScopes();

    public String getRedirectUri();

}

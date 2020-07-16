package com.xyyh.authorization.core;

import java.io.Serializable;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class OAuth2AccessTokenAuthentication implements Serializable {

    private static final long serialVersionUID = -4548467746346379976L;

    private String clientId;

    private Set<String> scopes;

    private OAuth2AccessToken accessToken;

    private Authentication user;

    public String getClientId() {
        return clientId;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public Authentication getUser() {
        return user;
    }

    public OAuth2AccessTokenAuthentication(ApprovalResult approvalResult, OAuth2AccessToken accessToken,
            Authentication user) {
        super();
        this.clientId = approvalResult.getClientId();
        this.scopes = approvalResult.getScope();
        this.accessToken = accessToken;
        this.user = user;
    }

}

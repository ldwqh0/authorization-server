package com.xyyh.authorization.core;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;

public class OAuth2Authentication extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -6827330735137748398L;

    private ApprovalResult result;

    private Authentication user;

    public OAuth2Authentication(ApprovalResult result,
            Authentication user) {
        super(user.getAuthorities());
        this.result = result;
        this.user = user;
    }

    public ApprovalResult getResult() {
        return result;
    }

    public Authentication getUser() {
        return user;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.user == null ? this.result.getClientId() : this.user.getPrincipal();
    }

}

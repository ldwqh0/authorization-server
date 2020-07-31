package com.xyyh.authorization.provider;

import java.util.Collections;
import java.util.Set;

import com.xyyh.authorization.core.ApprovalResult;

public class DefaultApprovalResult implements ApprovalResult {

    private static final long serialVersionUID = 8068718072536160467L;

    private String clientId;

    private boolean approvaled = false;

    private String redirectUri;

    private Set<String> scopes = Collections.emptySet();

    @Override
    public boolean isApprovaled() {
        return this.approvaled;
    }

    public void setApprovaled(boolean approvaled) {
        this.approvaled = approvaled;
    }

    @Override
    public Set<String> getScopes() {
        return this.scopes;
    }

    public void setScope(Set<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

}

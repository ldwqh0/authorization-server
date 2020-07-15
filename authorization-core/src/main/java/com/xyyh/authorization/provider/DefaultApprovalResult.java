package com.xyyh.authorization.provider;

import java.util.Collections;
import java.util.Set;

import com.xyyh.authorization.core.ApprovalResult;

public class DefaultApprovalResult implements ApprovalResult {

    private static final long serialVersionUID = 8068718072536160467L;

    private String clientId;

    private boolean approvaled = false;

    private String redirectUri;

    private Set<String> scope = Collections.emptySet();

    @Override
    public boolean isApprovaled() {
        return this.approvaled;
    }

    public void setApprovaled(boolean approvaled) {
        this.approvaled = approvaled;
    }

    @Override
    public Set<String> getScope() {
        return this.scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

}

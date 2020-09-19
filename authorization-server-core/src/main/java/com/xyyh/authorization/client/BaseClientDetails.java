package com.xyyh.authorization.client;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Set;

import static com.xyyh.authorization.collect.Sets.hashSet;
import static com.xyyh.authorization.collect.Sets.transform;

public class BaseClientDetails implements ClientDetails {

    private static final long serialVersionUID = -7386163121370242465L;

    private final String clientId;
    private final String clientSecret;
    private final boolean autoApproval;
    private final Set<String> scope;
    private final Set<String> registeredRedirectUris;
    private final Set<AuthorizationGrantType> authorizedGrantTypes;

    public BaseClientDetails(
        String clientId,
        String clientSecret,
        boolean autoApproval,
        Set<String> scope,
        Set<String> registeredRedirectUris,
        Set<String> authorizedGrantTypes) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.autoApproval = autoApproval;
        this.scope = hashSet(scope);
        this.registeredRedirectUris = hashSet(registeredRedirectUris);
        this.authorizedGrantTypes = transform(authorizedGrantTypes, AuthorizationGrantType::new);
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public String getClientSecret() {
        return this.clientSecret;
    }

    @Override
    public Set<String> getScope() {
        return this.scope;
    }

    @Override
    public Set<String> getRegisteredRedirectUris() {
        return registeredRedirectUris;
    }

    @Override
    public boolean isAutoApproval() {
        return autoApproval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
        result = prime * result + ((clientSecret == null) ? 0 : clientSecret.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseClientDetails other = (BaseClientDetails) obj;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        if (clientSecret == null) {
            if (other.clientSecret != null)
                return false;
        } else if (!clientSecret.equals(other.clientSecret))
            return false;
        if (scope == null) {
            return other.scope == null;
        } else {
            return scope.equals(other.scope);
        }
    }

    @Override
    public String toString() {
        return "BaseClientDetails [clientId=" + clientId + ", clientSecret=" + clientSecret + ", scope=" + scope
            + ", registeredRedirectUris=" + registeredRedirectUris + ", authorizedGrantTypes="
            + authorizedGrantTypes + "]";
    }

    @Override
    public Set<AuthorizationGrantType> getAuthorizedGrantTypes() {
        return this.authorizedGrantTypes;
    }

}

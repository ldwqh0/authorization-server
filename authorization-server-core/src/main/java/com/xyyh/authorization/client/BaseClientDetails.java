package com.xyyh.authorization.client;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Set;
import java.util.stream.Collectors;

public class BaseClientDetails implements ClientDetails {

    private static final long serialVersionUID = -7386163121370242465L;

    private String clientId;
    private String clientSecret;
    private Set<String> scope;
    private Set<String> registeredRedirectUris;
    private Set<AuthorizationGrantType> authorizedGrantTypes;

    public BaseClientDetails() {
        super();
    }

    public BaseClientDetails(String clientId, String clientSecret) {
        super();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public BaseClientDetails(
        String clientId,
        String clientSecret,
        Set<String> scope,
        Set<String> registeredRedirectUris,
        Set<String> authorizedGrantTypes) {
        super();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.registeredRedirectUris = registeredRedirectUris;
        this.authorizedGrantTypes = authorizedGrantTypes.stream().map(AuthorizationGrantType::new).collect(Collectors.toSet());
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

    public void setRegisteredRedirectUri(Set<String> registeredRedirectUri) {
        this.registeredRedirectUris = registeredRedirectUri;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public void setAuthorizedGrantTypes(Set<AuthorizationGrantType> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
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
            if (other.scope != null)
                return false;
        } else if (!scope.equals(other.scope))
            return false;
        return true;
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

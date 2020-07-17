package com.xyyh.authorization.provider;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;

import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.OAuth2Authentication;

/**
 * 授权结果token
 * 
 * @author LiDong
 *
 */
public class DefaultOAuth2AuthenticationToken implements OAuth2Authentication {
    private static final long serialVersionUID = -6827330735137748398L;

    private ApprovalResult result;

    private Authentication userAuthentication;

    @Override
    public boolean isAuthenticated() {
        return this.result.isApprovaled()
                && (Objects.isNull(userAuthentication) || userAuthentication.isAuthenticated());
    }

    public boolean isClientOnly() {
        return Objects.isNull(userAuthentication);
    }

    public DefaultOAuth2AuthenticationToken(ApprovalResult result,
            Authentication userAuthentication) {
        this.result = result;
        this.userAuthentication = userAuthentication;
    }

    public ApprovalResult getResult() {
        return result;
    }

    public Authentication getUserAuthentication() {
        return userAuthentication;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.userAuthentication == null ? this.result.getClientId() : this.userAuthentication.getPrincipal();
    }

    @Override
    public String getName() {
        return String.valueOf(getPrincipal());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Objects.isNull(this.userAuthentication) ? null : this.userAuthentication.getAuthorities();
    }

    @Override
    public Object getDetails() {
        return Objects.isNull(this.userAuthentication) ? this.result : this.userAuthentication;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getClientId() {
        return result.getClientId();
    }

    @Override
    public Set<String> getScopes() {
        return result.getScope();
    }

    @Override
    public void eraseCredentials() {
        if (this.userAuthentication != null
                && CredentialsContainer.class.isAssignableFrom(this.userAuthentication.getClass())) {
            CredentialsContainer.class.cast(this.userAuthentication).eraseCredentials();
        }
    }

}

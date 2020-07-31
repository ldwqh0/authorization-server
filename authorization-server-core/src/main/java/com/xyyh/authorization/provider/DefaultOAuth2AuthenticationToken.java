package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.OAuth2Authentication;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * 授权结果token
 *
 * @author LiDong
 */
public class DefaultOAuth2AuthenticationToken implements OAuth2Authentication {
    private static final long serialVersionUID = -6827330735137748398L;

    private ApprovalResult approvalResult;

    private Authentication userAuthentication;

    @Override
    public boolean isAuthenticated() {
        return this.approvalResult.isApprovaled()
                && (Objects.isNull(userAuthentication) || userAuthentication.isAuthenticated());
    }

    public boolean isClientOnly() {
        return Objects.isNull(userAuthentication);
    }

    public DefaultOAuth2AuthenticationToken(ApprovalResult result,
            Authentication userAuthentication) {
        this.approvalResult = result;
        this.userAuthentication = userAuthentication;
    }

    public ApprovalResult getApprovalResult() {
        return approvalResult;
    }

    public Authentication getUserAuthentication() {
        return userAuthentication;
    }

    @Override
    public Object getCredentials() {
        return "NaN";
    }

    @Override
    public Object getPrincipal() {
        return this.userAuthentication == null ? this.approvalResult.getClientId()
                : this.userAuthentication.getPrincipal();
    }

    @Override
    public String getName() {
        return Objects.isNull(this.userAuthentication) ? this.approvalResult.getClientId()
                : this.userAuthentication.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Objects.isNull(this.userAuthentication) ? null : this.userAuthentication.getAuthorities();
    }

    @Override
    public Object getDetails() {
        return Objects.isNull(this.userAuthentication) ? this.approvalResult : this.userAuthentication;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new NotImplementedException("the method is not implemented");
    }

    @Override
    public String getClientId() {
        return approvalResult.getClientId();
    }

    @Override
    public Set<String> getScopes() {
        return approvalResult.getScopes();
    }

    @Override
    public void eraseCredentials() {
        if (this.userAuthentication != null
                && CredentialsContainer.class.isAssignableFrom(this.userAuthentication.getClass())) {
            CredentialsContainer.class.cast(this.userAuthentication).eraseCredentials();
        }
    }

    @Override
    public String getRedirectUri() {
        return approvalResult.getRedirectUri();
    }
}

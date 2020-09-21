package org.xyyh.authorization.provider;

import org.xyyh.authorization.core.ApprovalResult;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.Oauth2AuthorizationRequest;
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

    private final ApprovalResult approvalResult;

    private final Authentication userAuthentication;

    private final Oauth2AuthorizationRequest request;

    @Override
    public boolean isAuthenticated() {
        return this.approvalResult.isApproved()
            && (Objects.isNull(userAuthentication) || userAuthentication.isAuthenticated());
    }

    public boolean isClientOnly() {
        return Objects.isNull(userAuthentication);
    }

    public DefaultOAuth2AuthenticationToken(ApprovalResult result,
                                            Authentication userAuthentication, Oauth2AuthorizationRequest request) {
        this.approvalResult = result;
        this.userAuthentication = userAuthentication;
        this.request = request;
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
        return this.userAuthentication == null ? this.request.getClientId()
            : this.userAuthentication.getPrincipal();
    }

    @Override
    public String getName() {
        return Objects.isNull(this.userAuthentication) ? this.request.getClientId()
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
        return request.getClientId();
    }

    @Override
    public Set<String> getScopes() {
        return approvalResult.getScopes();
    }

    @Override
    public Oauth2AuthorizationRequest getRequest() {
        return this.request;
    }

    @Override
    public void eraseCredentials() {
        if (this.userAuthentication != null
            && CredentialsContainer.class.isAssignableFrom(this.userAuthentication.getClass())) {
            ((CredentialsContainer) this.userAuthentication).eraseCredentials();
        }
    }


}

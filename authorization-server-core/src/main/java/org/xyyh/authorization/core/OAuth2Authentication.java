package org.xyyh.authorization.core;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * 一个 oauth2 授权信息，包括授权用户信息，授权 scope 和 client 信息
 */
public interface OAuth2Authentication extends Authentication, CredentialsContainer {


    /**
     * the {@link ClientDetails}
     */
    ClientDetails getClient();

    /**
     * the authorized scopes
     */
    Set<String> getScopes();

    /**
     * the {@link  OpenidAuthorizationRequest}
     *
     * @see OpenidAuthorizationRequest
     */
    OpenidAuthorizationRequest getRequest();

    static OAuth2Authentication of(ApprovalResult approvalResult, ClientDetails client, Authentication user) {
        return new DefaultOAuth2AuthenticationToken(null, approvalResult, client, user);
    }


    static OAuth2Authentication of(OpenidAuthorizationRequest request,
                                   ApprovalResult result,
                                   ClientDetails client,
                                   Authentication userAuthentication) {
        return new DefaultOAuth2AuthenticationToken(request, result, client, userAuthentication);
    }

}

/**
 * 授权结果token
 *
 * @author LiDong
 */
class DefaultOAuth2AuthenticationToken implements OAuth2Authentication {
    private static final long serialVersionUID = -6827330735137748398L;

    private final ClientDetails client;

    private final ApprovalResult approvalResult;

    private final Authentication userAuthentication;

    private final OpenidAuthorizationRequest request;

    @Override
    public boolean isAuthenticated() {
        return this.approvalResult.isApproved()
                && (Objects.isNull(userAuthentication) || userAuthentication.isAuthenticated());
    }

    public boolean isClientOnly() {
        return Objects.isNull(userAuthentication);
    }

    /**
     * 使用指定的信息构建一个 {@link OAuth2Authentication}
     *
     * @param request            授权请求
     * @param result             授权结果
     * @param client             client信息
     * @param userAuthentication 用户信息
     */
    public DefaultOAuth2AuthenticationToken(OpenidAuthorizationRequest request,
                                            ApprovalResult result,
                                            ClientDetails client,
                                            Authentication userAuthentication) {
        this.client = client;
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
        return this.userAuthentication == null ? this.client
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
        return Objects.isNull(this.userAuthentication) ? this.approvalResult : this.userAuthentication.getDetails();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new NotImplementedException("the method is not implemented");
    }

    @Override
    public ClientDetails getClient() {
        return client;
    }

    @Override
    public Set<String> getScopes() {
        return approvalResult.getScopes();
    }

    @Override
    public OpenidAuthorizationRequest getRequest() {
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

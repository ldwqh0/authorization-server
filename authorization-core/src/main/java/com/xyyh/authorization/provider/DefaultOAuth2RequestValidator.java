package com.xyyh.authorization.provider;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.exception.InvalidScopeException;
import com.xyyh.authorization.token.request.TokenRequest;

public class DefaultOAuth2RequestValidator implements OAuth2RequestValidator {

    @Override
    public void validateScope(OAuth2AuthorizationRequest authorizationRequest, ClientDetails client)
            throws InvalidScopeException {
        validateScope(authorizationRequest.getScopes(), client.getScope());

    }

    @Override
    public void validateScope(TokenRequest tokenRequest, ClientDetails client) throws InvalidScopeException {
        // TODO Auto-generated method stub

    }

    /**
     * 验证请求的scope是否client支持的scope
     * 
     * @param requestScope 请求scope
     * @param clientScope  client的scope
     */
    private void validateScope(Set<String> requestScope, Set<String> clientScope) {
        if (CollectionUtils.isEmpty(requestScope)) {
            throw new InvalidScopeException();
        } else {
            for (String scope : requestScope) {
                if (!clientScope.contains(scope)) {
                    throw new InvalidScopeException();
                }
            }
        }
    }
}

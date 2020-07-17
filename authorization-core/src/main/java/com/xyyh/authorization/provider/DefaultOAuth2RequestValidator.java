package com.xyyh.authorization.provider;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.core.OAuth2RequestValidator;
import com.xyyh.authorization.exception.InvalidScopeException;

public class DefaultOAuth2RequestValidator implements OAuth2RequestValidator {

    @Override
    public void validateScope(OAuth2AuthorizationRequest authorizationRequest, ClientDetails client)
            throws InvalidScopeException {
        validateScope(authorizationRequest.getScopes(), client);
    }

    @Override
    public void validateScope(Set<String> requestScopes, ClientDetails client) throws InvalidScopeException {
        Set<String> clientScope = client.getScope();
        if (CollectionUtils.isEmpty(requestScopes)) {
            throw new InvalidScopeException();
        } else {
            for (String scope : requestScopes) {

                if (!clientScope.contains(scope)) {
                    throw new InvalidScopeException();
                }
            }
        }
    }
}

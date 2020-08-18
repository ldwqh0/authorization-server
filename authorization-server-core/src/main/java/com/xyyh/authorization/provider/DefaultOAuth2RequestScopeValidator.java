package com.xyyh.authorization.provider;

import java.util.Set;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.collect.Collections;
import com.xyyh.authorization.core.OAuth2RequestScopeValidator;
import com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import com.xyyh.authorization.exception.InvalidScopeException;

public class DefaultOAuth2RequestScopeValidator implements OAuth2RequestScopeValidator {

    @Override
    public void validateScope(OpenidAuthorizationRequest authorizationRequest, ClientDetails client)
            throws InvalidScopeException {
        validateScope(authorizationRequest.getScopes(), client);
    }

    @Override
    public void validateScope(Set<String> requestScopes, ClientDetails client) throws InvalidScopeException {
        Set<String> clientScope = client.getScope();
        if (Collections.isEmpty(requestScopes)) {
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
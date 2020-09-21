package org.xyyh.authorization.provider;

import java.util.Set;

import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.collect.CollectionUtils;
import org.xyyh.authorization.core.OAuth2RequestScopeValidator;
import org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import org.xyyh.authorization.exception.InvalidScopeException;

public class DefaultOAuth2RequestScopeValidator implements OAuth2RequestScopeValidator {

    @Override
    public void validateScope(OpenidAuthorizationRequest authorizationRequest, ClientDetails client)
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

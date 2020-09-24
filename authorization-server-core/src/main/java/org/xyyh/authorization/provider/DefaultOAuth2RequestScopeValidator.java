package org.xyyh.authorization.provider;

import org.xyyh.authorization.collect.CollectionUtils;
import org.xyyh.authorization.core.OAuth2RequestScopeValidator;
import org.xyyh.authorization.exception.InvalidScopeException;

import java.util.Set;

public class DefaultOAuth2RequestScopeValidator implements OAuth2RequestScopeValidator {

    @Override
    public void validateScope(Set<String> requestScopes, Set<String> clientScopes) throws InvalidScopeException {
        if (CollectionUtils.isEmpty(requestScopes)) {
            throw new InvalidScopeException();
        } else {
            for (String scope : requestScopes) {
                if (!clientScopes.contains(scope)) {
                    throw new InvalidScopeException();
                }
            }
        }
    }
}

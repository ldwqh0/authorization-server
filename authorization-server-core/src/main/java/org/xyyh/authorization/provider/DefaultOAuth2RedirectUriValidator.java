package org.xyyh.authorization.provider;

import org.xyyh.authorization.collect.CollectionUtils;
import org.xyyh.authorization.core.OAuth2RedirectUriValidator;

import java.util.Set;

public class DefaultOAuth2RedirectUriValidator implements OAuth2RedirectUriValidator {

    @Override
    public boolean validate(String requestUri, Set<String> registeredUris) {
        return CollectionUtils.isNotEmpty(registeredUris) && registeredUris.contains(requestUri);
    }
}

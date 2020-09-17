package com.xyyh.authorization.provider;

import com.xyyh.authorization.collect.CollectionUtils;
import com.xyyh.authorization.core.OAuth2RedirectUriValidator;

import java.util.Set;

public class DefaultOAuth2RedirectUriValidator implements OAuth2RedirectUriValidator {

    @Override
    public boolean validate(String requestUri, Set<String> registeredUris) {
        return CollectionUtils.isNotEmpty(registeredUris) && registeredUris.contains(requestUri);
    }
}

package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2RedirectUriValidator;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

public class DefaultOAuth2RedirectUriValidator implements OAuth2RedirectUriValidator {

    @Override
    public boolean validate(String requestUri, Set<String> registeredUris) {
        return CollectionUtils.isNotEmpty(registeredUris) && registeredUris.contains(requestUri);
    }
}

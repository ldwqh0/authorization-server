package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2RedirectUriValidator;
import com.xyyh.authorization.exception.RequestValidationException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

public class DefaultOAuth2RedirectUriValidator implements OAuth2RedirectUriValidator {

    @Override
    public void validate(String requestUri, Set<String> registeredUris) {

        if (CollectionUtils.isNotEmpty(registeredUris) && !registeredUris.contains(requestUri)) {
            throw new RequestValidationException("redirect url not matched");
        }
    }
}

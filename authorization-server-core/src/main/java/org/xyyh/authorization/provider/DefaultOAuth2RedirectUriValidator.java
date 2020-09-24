package org.xyyh.authorization.provider;

import org.xyyh.authorization.collect.CollectionUtils;
import org.xyyh.authorization.core.OAuth2RedirectUriValidator;
import org.xyyh.authorization.exception.UnRegisteredRedirectUriException;

import java.util.Set;

public class DefaultOAuth2RedirectUriValidator implements OAuth2RedirectUriValidator {

    @Override
    public void validate(String requestUri, Set<String> registeredUris) throws UnRegisteredRedirectUriException {
        if (CollectionUtils.isNotEmpty(registeredUris) && registeredUris.contains(requestUri)) {
            // do nothing here
        } else {
            throw new UnRegisteredRedirectUriException();
        }
    }
}

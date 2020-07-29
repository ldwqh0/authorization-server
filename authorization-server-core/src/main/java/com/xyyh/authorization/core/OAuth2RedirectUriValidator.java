package com.xyyh.authorization.core;

import java.util.Set;

@FunctionalInterface
public interface OAuth2RedirectUriValidator {

    /**
     * 验证传入的请求的url是否符合
     *
     * @param requestUri
     * @param registeredUris
     */
    public boolean validate(String requestUri, Set<String> registeredUris);
}

package org.xyyh.authorization.core;

import org.xyyh.authorization.exception.UnRegisteredRedirectUriException;

import java.util.Set;

@FunctionalInterface
public interface OAuth2RedirectUriValidator {

    /**
     * 验证传入的请求的url是否符合
     *
     * @param requestUri     请求的uri
     * @param registeredUris 注册的请求uri
     * @return 验证成功返回true, 验证失败返回false
     */
    void validate(String requestUri, Set<String> registeredUris) throws UnRegisteredRedirectUriException;
}

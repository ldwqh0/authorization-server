package org.xyyh.authorization.core;

import org.xyyh.authorization.exception.InvalidScopeException;

import java.util.Set;

/**
 * OAuth2请求验证器，用户验证请求的正确性
 */
public interface OAuth2RequestScopeValidator {

    /**
     * 验证token请求的正确性
     *
     * @param requestScopes the TokenRequest to be validated
     * @param clientScopes  the client that is making the request
     */
    void validateScope(Set<String> requestScopes, Set<String> clientScopes) throws InvalidScopeException;

}

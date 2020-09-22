package org.xyyh.authorization.core;

import org.xyyh.authorization.client.ClientDetails;

import java.util.Set;

/**
 * OAuth2请求验证器，用户验证请求的正确性
 */
public interface OAuth2RequestScopeValidator {

    /**
     * 验证token请求的正确性
     *
     * @param scopes the TokenRequest to be validated
     * @param client the client that is making the request
     */
    void validateScope(Set<String> scopes, ClientDetails client);

}

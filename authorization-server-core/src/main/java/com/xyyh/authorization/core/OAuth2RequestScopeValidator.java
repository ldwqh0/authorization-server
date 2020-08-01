package com.xyyh.authorization.core;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import com.xyyh.authorization.exception.InvalidScopeException;

import java.util.Set;

/**
 * OAuth2请求验证器，用户验证请求的正确性
 */
public interface OAuth2RequestScopeValidator {

    /**
     * 验证授权请求的正确型
     */
    public void validateScope(OpenidAuthorizationRequest authorizationRequest, ClientDetails client)
            throws InvalidScopeException;

    /**
     * 验证token请求的正确性
     *
     * @param scopes the TokenRequest to be validated
     * @param client the client that is making the request
     */
    public void validateScope(Set<String> scopes, ClientDetails client);

}

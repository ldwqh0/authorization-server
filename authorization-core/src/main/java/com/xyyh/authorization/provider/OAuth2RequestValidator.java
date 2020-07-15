package com.xyyh.authorization.provider;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.exception.InvalidScopeException;
import com.xyyh.authorization.token.request.TokenRequest;
import com.xyyh.authorization.web.AuthorizationRequest;

/**
 * 
 * OAuth2请求验证器，用户验证请求的正确性
 */
public interface OAuth2RequestValidator {

    /**
     * 验证授权请求的正确型
     */
    public void validateScope(AuthorizationRequest authorizationRequest, ClientDetails client)
            throws InvalidScopeException;

    /**
     * 验证token请求的正确性
     * 
     * @param tokenRequest the TokenRequest to be validated
     * @param client       the client that is making the request
     * @throws InvalidScopeException if a requested scope is invalid
     */
    public void validateScope(TokenRequest tokenRequest, ClientDetails client) throws InvalidScopeException;

}
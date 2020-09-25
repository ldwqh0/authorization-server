package org.xyyh.authorization.core;

import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import org.xyyh.authorization.exception.InvalidScopeException;
import org.xyyh.authorization.exception.OpenidRequestValidationException;
import org.xyyh.authorization.exception.UnRegisteredRedirectUriException;
import org.xyyh.authorization.exception.UnsupportedResponseTypeException;

/**
 * OAuth2请求验证器，用户验证请求的正确性
 */
@FunctionalInterface
public interface OAuth2AuthorizationRequestValidator {


    /**
     * 对一个oauth2授权请求进行验证
     *
     * @param request 用户授权请求
     * @param client  连接程序
     */
    void validate(OpenidAuthorizationRequest request, ClientDetails client) throws InvalidScopeException, UnsupportedResponseTypeException, OpenidRequestValidationException, UnRegisteredRedirectUriException;

}

package org.xyyh.authorization.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.core.OAuth2AuthorizationRequestValidator;
import org.xyyh.authorization.core.OAuth2RedirectUriValidator;
import org.xyyh.authorization.core.OAuth2RequestScopeValidator;
import org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import org.xyyh.authorization.exception.InvalidScopeException;
import org.xyyh.authorization.exception.OpenidRequestValidationException;
import org.xyyh.authorization.exception.UnRegisteredRedirectUriException;
import org.xyyh.authorization.exception.UnsupportedResponseTypeException;

import java.util.Set;

import static org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest.*;

public class DefaultOAuth2AuthorizationRequestValidator implements OAuth2AuthorizationRequestValidator {

    private final OAuth2RedirectUriValidator redirectUriValidator;

    private final OAuth2RequestScopeValidator requestScopeValidator;

    public DefaultOAuth2AuthorizationRequestValidator(OAuth2RedirectUriValidator redirectUriValidator, OAuth2RequestScopeValidator requestScopeValidator) {
        this.redirectUriValidator = redirectUriValidator;
        this.requestScopeValidator = requestScopeValidator;
    }

    @Override
    public void validate(OpenidAuthorizationRequest request, ClientDetails client) throws InvalidScopeException, UnsupportedResponseTypeException, OpenidRequestValidationException, UnRegisteredRedirectUriException {
        // 首先验证scope
        requestScopeValidator.validateScope(request.getScopes(), client.getScopes());
        // 验证response type
        validResponseType(request.getResponseTypes(), client.getAuthorizedGrantTypes());
        // 验证pkce
        validPkceRequest(request, client);
        // 验证redirect uri
        redirectUriValidator.validate(request.getRedirectUri(), client.getRegisteredRedirectUris());
    }


    private void validResponseType(Set<String> requestResponseTypes,
                                   Set<AuthorizationGrantType> authorizedGrantTypes) throws UnsupportedResponseTypeException {
        for (String responseType : requestResponseTypes) {
            if (!validResponseType(responseType, authorizedGrantTypes)) {
                throw new UnsupportedResponseTypeException();
            }
        }
    }

    /**
     * 验证指定的client的authorizationGrantTypes是否支持特定的responseType
     *
     * @param responseType            待验证的responseType
     * @param authorizationGrantTypes client的authorizationGrantTypes
     * @return 验证成功返回true, 否则返回false
     */
    private boolean validResponseType(String responseType, Set<AuthorizationGrantType> authorizationGrantTypes) {
        // 如果response type=code,要求client必须支持AUTHORIZATION_CODE
        if (RESPONSE_TYPE_CODE.equals(responseType)) {
            return authorizationGrantTypes.contains(AuthorizationGrantType.AUTHORIZATION_CODE);
        }
        // 如果 response type = id_token.要求client必须支持IMPLICIT
        if (RESPONSE_TYPE_ID_TOKEN.equals(responseType)) {
            return authorizationGrantTypes.contains(AuthorizationGrantType.IMPLICIT);
        }
        if (RESPONSE_TYPE_TOKEN.equals(responseType)) {
            return authorizationGrantTypes.contains(AuthorizationGrantType.IMPLICIT);
        }
        return false;
    }

    /**
     * 验证请求是否符合pkce规范<br>
     * 如果客户端对authorize端点配置为需要pkce验证，则必须进行验证
     *
     * @param client  要验证的客户端
     * @param request 授权请求
     */
    private void validPkceRequest(OpenidAuthorizationRequest request, ClientDetails client) throws OpenidRequestValidationException {
        if (client.isRequirePkce()) {
            String codeChallenge = request.getAdditionalParameters().get("code_challenge");
            if (StringUtils.isBlank(codeChallenge)) {
                throw new OpenidRequestValidationException(request, "invalid_request");
            }
        }
    }
}

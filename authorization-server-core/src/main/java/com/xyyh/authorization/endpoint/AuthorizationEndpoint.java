package com.xyyh.authorization.endpoint;

import com.google.common.collect.Maps;
import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.core.*;
import com.xyyh.authorization.endpoint.request.OpenidAuthorizationFlow;
import com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import com.xyyh.authorization.exception.InvalidScopeException;
import com.xyyh.authorization.exception.NoSuchClientException;
import com.xyyh.authorization.exception.OpenidRequestValidationException;
import com.xyyh.authorization.exception.UnsupportedResponseTypeException;
import com.xyyh.authorization.provider.DefaultOAuth2AuthenticationToken;
import com.xyyh.authorization.provider.DefaultOAuth2AuthorizationCode;
import com.xyyh.authorization.utils.OAuth2AccessTokenUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;

import static com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest.*;

@SessionAttributes({"authorizationRequest"})
@RequestMapping("/oauth2/authorize")
public class AuthorizationEndpoint {

    private static final String OAUTH2_AUTHORIZATION_REQUEST = "authorizationRequest";
    private static final String USER_OAUTH_APPROVAL = "user_oauth_approval";

    StringKeyGenerator authorizationCodeGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder(), 33);

    private int periodOfValidity = 3 * 60;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private OAuth2RequestScopeValidator oAuth2RequestValidator;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    private OAuth2AuthorizationCodeService authorizationCodeService;

    @Autowired
    private OAuth2AccessTokenService accessTokenService;

    @Autowired
    private OAuth2RedirectUriValidator redirectUriValidator;

    /**
     * 返回授权页面
     *
     * @param model
     * @param sessionStatus
     * @return
     */

    @RequestMapping
    public ModelAndView authorize(
        HttpServletRequest request,
        Map<String, Object> model,
        @RequestParam MultiValueMap<String, String> params,
        SessionStatus sessionStatus,
        @AuthenticationPrincipal Authentication user) {
        OpenidAuthorizationRequest authorizationRequest = createRequest(request.getRequestURL().toString(), params);
        try {
            // 加载client信息
            ClientDetails client = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());

            // 验证scope,会抛出InvalidScopeException异常
            oAuth2RequestValidator.validateScope(authorizationRequest, client);

            // 验证response type
            if (!validResponseType(authorizationRequest.getResponseTypes(), client.getAuthorizedGrantTypes())) {
                throw new OpenidRequestValidationException(authorizationRequest, "unsupported_response_type");
            }

            // 验证request redirect uri是否符合请求
            String redirectUri = authorizationRequest.getRedirectUri();
            if (!redirectUriValidator.validate(redirectUri, client.getRegisteredRedirectUris())) {
                throw new OpenidRequestValidationException(authorizationRequest, "invalid_redirect_uri");
            }

            ApprovalResult preResult = userApprovalHandler.preCheck(authorizationRequest, user);

            // 进行请求预检
            if (preResult.isApprovaled()) {
                sessionStatus.setComplete();
                return new ModelAndView(getRedirectView(authorizationRequest, preResult, user));
            } else {
                model.put(OAUTH2_AUTHORIZATION_REQUEST, authorizationRequest);
                // 如果预检没有通过，跳转到授权页面
                return new ModelAndView("/oauth/confirm_access", model);
            }
            //TODO invalid_request error
        } catch (NoSuchClientException ex) {
            throw new OpenidRequestValidationException(authorizationRequest, "unauthorized_client");
        } catch (InvalidScopeException ex2) {
            throw new OpenidRequestValidationException(authorizationRequest, "invalid_scope");
        } catch (UnsupportedResponseTypeException ex3) {
            throw new OpenidRequestValidationException(authorizationRequest, "unsupported_response_type");
        } finally {

        }
    }


    /**
     * 提交授权验证请求,并返回授权验证结果
     *
     * @param approvalParameters 授权验证参数
     * @param model
     * @param sessionStatus
     * @param userAuthentication 当前用户信息
     * @return
     */
    @PostMapping(params = {USER_OAUTH_APPROVAL})
    public View approveOrDeny(
        @RequestParam Map<String, String> approvalParameters,
        Map<String, ?> model,
        SessionStatus sessionStatus,
        Authentication userAuthentication) {
        OpenidAuthorizationRequest authorizationRequest = (OpenidAuthorizationRequest) model.get(OAUTH2_AUTHORIZATION_REQUEST);
        // 当提交用户授权信息之后，将session标记为完成
        sessionStatus.setComplete();
        // 获取用户授权结果
        ApprovalResult approvalResult = userApprovalHandler.approval(authorizationRequest, userAuthentication, approvalParameters);
        // 如果授权不通过，直接返回
        if (!approvalResult.isApprovaled()) {
            throw new OpenidRequestValidationException(authorizationRequest, "access_denied");
        } else {
            return getRedirectView(authorizationRequest, approvalResult, userAuthentication);
        }
    }

    private View getRedirectView(OpenidAuthorizationRequest authorizationRequest, ApprovalResult approvalResult, Authentication userAuthentication) {
        OpenidAuthorizationFlow flow = authorizationRequest.getFlow();
        if (OpenidAuthorizationFlow.CODE.equals(flow)) {
            return getCodeFlowResponse(authorizationRequest, approvalResult, userAuthentication);
        } else if (OpenidAuthorizationFlow.IMPLICIT.equals(flow)) {
            // 简易模式的token请求 https://tools.ietf.org/html/rfc6749#section-4.2
            return getImplicitFlowResponse(authorizationRequest, approvalResult, userAuthentication);
        } else if (OpenidAuthorizationFlow.HYBRID.equals(flow)) {
            return getHybridFlow(authorizationRequest, approvalResult, userAuthentication);
        }
        throw new RuntimeException("no flow can be find");
    }

    /**
     * 创建简易模式跳转请求
     *
     * @param request            授权请求
     * @param result             授权结果
     * @param userAuthentication 授权用户
     * @return
     */
    private View getImplicitFlowResponse(OpenidAuthorizationRequest request, ApprovalResult result,
                                         Authentication userAuthentication) {
        OAuth2Authentication authentication = new DefaultOAuth2AuthenticationToken(result, userAuthentication);
        OAuth2AccessToken accessToken = accessTokenService.create(authentication);
        Map<String, ?> fragment = OAuth2AccessTokenUtils.converterToken2Map(accessToken);
        return buildRedirectView(request.getRedirectUri(), null, fragment);
    }

    /**
     * 创建授权码请求的view
     *
     * @param request
     * @param result
     * @param userAuthentication
     * @return
     */
    private View getCodeFlowResponse(
        OpenidAuthorizationRequest request,
        ApprovalResult result,
        Authentication userAuthentication) {
        Map<String, String> query = new LinkedHashMap<>();
        String state = request.getState();
        if (StringUtils.isNotEmpty(state)) {
            query.put("state", state);
        }
        // 创建授权码
        String codeValue = authorizationCodeGenerator.generateKey();
        Instant issueAt = Instant.now();
        // code有效期默认三分钟
        Instant expireAt = issueAt.plusSeconds(periodOfValidity);
        // 创建并保存授权码
        OAuth2AuthorizationCode code = authorizationCodeService.save(new DefaultOAuth2AuthorizationCode(codeValue, issueAt, expireAt), new DefaultOAuth2AuthenticationToken(result, userAuthentication));
        query.put("code", code.getValue());
        return buildRedirectView(request.getRedirectUri(), query, null);
    }

    /**
     * 返回混合模式认证的视图
     *
     * @param authorizationRequest 授权请求
     * @param approvalResult       用户授权结果
     * @param userAuthentication   用户信息
     * @return
     */
    private View getHybridFlow(OpenidAuthorizationRequest authorizationRequest, ApprovalResult approvalResult,
                               Authentication userAuthentication) {

        return null;
    }

    /**
     * 创建跳转请求
     *
     * @param uri      要跳转的url
     * @param query    需要额外增加的查询参数
     * @param fragment hash部分的参数列表
     * @return
     */
    private View buildRedirectView(String uri, Map<String, String> query, Map<String, ?> fragment) {
        // 将新构建的查询参数附加到url上
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);
        if (MapUtils.isNotEmpty(query)) {
            query.forEach((key, value) -> {
                builder.queryParam(key, value);
            });
        }
        if (MapUtils.isNotEmpty(fragment)) {
            StringBuilder values = new StringBuilder();
            String originFragment = builder.build().getFragment();
            if (StringUtils.isNotBlank(originFragment)) {
                values.append(originFragment);
            }
            fragment.forEach((key, value) -> {
                if (values.length() > 0) {
                    values.append("&");
                }
                values.append(key).append("=").append(value);
            });
            builder.fragment(values.toString());
        }
        RedirectView redirectView = new RedirectView(builder.toUriString());
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        return redirectView;
    }

    /**
     * 根据传入参数，创建授权请求信息
     *
     * @param uri
     * @param parameters
     * @return
     */
    private OpenidAuthorizationRequest createRequest(String uri, MultiValueMap<String, String> parameters) {
        Map<String, Object> additionalParameters = new HashMap<String, Object>();
        parameters.entrySet().stream()
            .filter(e -> !e.getKey().equals(OAuth2ParameterNames.RESPONSE_TYPE) &&
                !e.getKey().equals(OAuth2ParameterNames.CLIENT_ID) &&
                !e.getKey().equals(OAuth2ParameterNames.REDIRECT_URI) &&
                !e.getKey().equals(OAuth2ParameterNames.SCOPE) &&
                !e.getKey().equals(OAuth2ParameterNames.STATE))
            .forEach(e -> additionalParameters.put(e.getKey(), e.getValue().get(0)));
        return OpenidAuthorizationRequest.builder()
            .responseType(parameters.get(OAuth2ParameterNames.RESPONSE_TYPE))
            .authorizationRequestUri(uri)
            .clientId(parameters.getFirst(OAuth2ParameterNames.CLIENT_ID))
            .redirectUri(parameters.getFirst(OAuth2ParameterNames.REDIRECT_URI))
            .scopes(parameters.get(OAuth2ParameterNames.SCOPE))
            .state(parameters.getFirst(OAuth2ParameterNames.STATE))
            .additionalParameters(additionalParameters)
            .build();
    }

    private boolean validResponseType(Set<String> requestResponseTypes, Set<AuthorizationGrantType> authorizedGrantTypes) {
        for (String responseType : requestResponseTypes) {
            if (!validResponseType(responseType, authorizedGrantTypes)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证指定的client的authorizationGrantTypes是否支持特定的responseType
     *
     * @param responseType            待验证的responseType
     * @param authorizationGrantTypes client的authorizationGrantTypes
     * @return
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
     * 处理请求校验错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({OpenidRequestValidationException.class})
    public View handleError(OpenidRequestValidationException ex) {
        OpenidAuthorizationRequest authorizationRequest = ex.getRequest();
        String uri = authorizationRequest.getRedirectUri();
        Map<String, String> error = Maps.newHashMap();
        error.put("error", ex.getMessage());
        String state = authorizationRequest.getState();
        if (StringUtils.isNotEmpty(state)) {
            error.put("state", state);
        }
        return buildRedirectView("https://www.baidu.com", error, null);
    }
}

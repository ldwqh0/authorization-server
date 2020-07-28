package com.xyyh.authorization.web;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.core.*;
import com.xyyh.authorization.core.endpoint.OpenidAuthorizationFlow;
import com.xyyh.authorization.core.endpoint.OpenidAuthorizationRequest;
import com.xyyh.authorization.provider.DefaultOAuth2AuthenticationToken;
import com.xyyh.authorization.utils.OAuth2AccessTokenUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SessionAttributes({"authorizationRequest"})
@RequestMapping("/oauth2/authorize")
public class AuthorizationEndpoint {

    private static final String OAUTH2_AUTHORIZATION_REQUEST = "authorizationRequest";
    private static final String USER_OAUTH_APPROVAL = "user_oauth_approval";

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
     * @param userAuthentication
     * @return
     */

    @RequestMapping
    public ModelAndView authorize(
        HttpServletRequest request,
        Map<String, Object> model,
        @RequestParam MultiValueMap<String, String> params,
        SessionStatus sessionStatus,
        Authentication userAuthentication) {
        OpenidAuthorizationRequest authorizationRequest = createRequest(request.getRequestURL().toString(), params);
        // 加载client信息
        ClientDetails client = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
        // 验证scope
        oAuth2RequestValidator.validateScope(authorizationRequest, client);

        String redirectUri = authorizationRequest.getRedirectUri();

        // 验证request redirect uri是否符合请求
        redirectUriValidator.validate(redirectUri, client.getRegisteredRedirectUris());
        // 进行请求预检
        if (preCheck(authorizationRequest, client)) {
            return null;
        } else {
            model.put(OAUTH2_AUTHORIZATION_REQUEST, authorizationRequest);
            // 如果预检没有通过，跳转到授权页面
            return new ModelAndView("/oauth/confirm_access", model);
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
        OpenidAuthorizationRequest authorizationRequest = (OpenidAuthorizationRequest) model
            .get(OAUTH2_AUTHORIZATION_REQUEST);

        // 获取用户授权结果
        ApprovalResult approvalResult = userApprovalHandler.approval(authorizationRequest, approvalParameters);

        // TODO 如果授权通过

        // 获取请求流程
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
        // 创建并保存授权码
        String code = authorizationCodeService.create(new DefaultOAuth2AuthenticationToken(result, userAuthentication));
        query.put("code", code);
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
     * 进行请求预检
     *
     * @param authorizationRequest
     * @param client
     * @return
     */
    private boolean preCheck(OpenidAuthorizationRequest authorizationRequest, ClientDetails client) {
        // TODO Auto-generated method stub
        return false;
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

}

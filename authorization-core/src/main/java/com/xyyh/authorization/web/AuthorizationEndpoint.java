package com.xyyh.authorization.web;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest.Builder;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Sets;
import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2Authentication;
import com.xyyh.authorization.core.OAuth2AuthorizationCodeService;
import com.xyyh.authorization.core.OAuth2RequestValidator;
import com.xyyh.authorization.core.UserApprovalHandler;
import com.xyyh.authorization.provider.DefaultOAuth2AuthenticationToken;
import com.xyyh.authorization.provider.DefaultUserApprovalHandler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType.*;

@RequestMapping("/oauth/authorize")
@SessionAttributes({ "authorizationRequest" })
public class AuthorizationEndpoint {

    private static final String OAUTH2_AUTHORIZATION_REQUEST = "authorizationRequest";
    private static final String SPACE = " ";

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private OAuth2RequestValidator oAuth2RequestValidator;

    @Autowired(required = false)
    private UserApprovalHandler userApprovalHandler = new DefaultUserApprovalHandler();

    @Autowired
    private OAuth2AuthorizationCodeService authorizationCodeService;

    @Autowired
    private OAuth2AccessTokenService accessTokenService;

    /**
     * 返回授权页面
     *
     * @param model
     * @param sessionStatus
     * @param principal
     * @return
     */
    @GetMapping
    public ModelAndView authorize(
            HttpServletRequest request,
            Map<String, Object> model,
            @RequestParam MultiValueMap<String, String> params,
            SessionStatus sessionStatus,
            Authentication principal) {
        OAuth2AuthorizationRequest authorizationRequest = createRequest(request.getRequestURL().toString(), params);
        // 加载client信息
        ClientDetails client = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
        // 验证scope
        oAuth2RequestValidator.validateScope(authorizationRequest, client);

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
     * 提交授权验证请求
     *
     * @param approvalParameters 授权验证参数
     * @param model
     * @param sessionStatus
     * @param principal          当前用户信息
     * @return
     */
    @PostMapping
    public View approveOrDeny(
            @RequestParam Map<String, String> approvalParameters,
            Map<String, ?> model,
            SessionStatus sessionStatus,
            Authentication principal) {
        OAuth2AuthorizationRequest authorizationRequest = (OAuth2AuthorizationRequest) model
                .get(OAUTH2_AUTHORIZATION_REQUEST);

        // 获取用户授权结果
        ApprovalResult approvalResult = userApprovalHandler.approval(authorizationRequest, approvalParameters);

        /**
         * 如果授权通过
         */
        OAuth2AuthorizationResponseType responseType = authorizationRequest.getResponseType();
        if (approvalResult.isApprovaled()) {
            if (CODE.equals(responseType)) {
                return getAuthorizationCodeResponse(authorizationRequest, approvalResult, principal);
            } else if (TOKEN.equals(responseType)) {
                return getImplicitGrantResponse(authorizationRequest, approvalResult, principal);
            }
        } else {

        }
        return null;
    }

    /**
     * 创建简易模式跳转请求
     * 
     * @param request   授权请求
     * @param result    授权结果
     * @param principal 授权用户
     * @return
     */
    private View getImplicitGrantResponse(OAuth2AuthorizationRequest request, ApprovalResult result,
            Authentication userAuthentication) {
        OAuth2Authentication authentication = new DefaultOAuth2AuthenticationToken(result, userAuthentication);
        OAuth2AccessToken accessToken = accessTokenService.create(authentication);
        Map<String, ?> fragment = converToAccessTokenResponse(accessToken);
        return buildRedirectView(request.getRedirectUri(), null, fragment);
    }

    /**
     * 创建授权码请求的view
     * 
     * @param result
     * @return
     */
    private View getAuthorizationCodeResponse(
            OAuth2AuthorizationRequest request,
            ApprovalResult result,
            Authentication principal) {
        Map<String, String> query = new LinkedHashMap<>();
        String state = request.getState();
        if (StringUtils.isNotEmpty(state)) {
            query.put("state", state);
        }
        // 创建并保存授权码
        String code = authorizationCodeService.create(new DefaultOAuth2AuthenticationToken(result, principal));
        query.put("code", code);
        return buildRedirectView(request.getRedirectUri(), query, null);
    }

    /**
     * 进行请求预检
     * 
     * @param authorizationRequest
     * @param client
     * @return
     */
    private boolean preCheck(OAuth2AuthorizationRequest authorizationRequest, ClientDetails client) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 创建跳转请求
     * 
     * @param uri   要跳转的url
     * @param query 需要额外增加的查询参数
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

    private OAuth2AuthorizationRequest createRequest(String uri, MultiValueMap<String, String> parameters) {
        Map<String, Object> additionalParameters = new HashMap<String, Object>();
        String reponseType = parameters.getFirst(OAuth2ParameterNames.RESPONSE_TYPE);
        parameters.entrySet().stream()
                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.RESPONSE_TYPE) &&
                        !e.getKey().equals(OAuth2ParameterNames.CLIENT_ID) &&
                        !e.getKey().equals(OAuth2ParameterNames.REDIRECT_URI) &&
                        !e.getKey().equals(OAuth2ParameterNames.SCOPE) &&
                        !e.getKey().equals(OAuth2ParameterNames.STATE))
                .forEach(e -> additionalParameters.put(e.getKey(), e.getValue().get(0)));
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        Set<String> scopes = StringUtils.isBlank(scope) ? Collections.emptySet()
                : Sets.newHashSet(StringUtils.split(scope, SPACE));
        Builder requestBuilder = null;
        if (CODE.getValue().equals(reponseType)) {
            requestBuilder = OAuth2AuthorizationRequest.authorizationCode();
        } else if (TOKEN.getValue().equals(reponseType)) {
            requestBuilder = OAuth2AuthorizationRequest.implicit();
        }
        return requestBuilder
                .authorizationUri(uri)
                .clientId(parameters.getFirst(OAuth2ParameterNames.CLIENT_ID))
                .redirectUri(parameters.getFirst(OAuth2ParameterNames.REDIRECT_URI))
                .scopes(scopes)
                .state(parameters.getFirst(OAuth2ParameterNames.STATE))
                .additionalParameters(additionalParameters)
                .build();
    }

    // TODO 这个是重复的
    private Map<String, ?> converToAccessTokenResponse(OAuth2AccessToken accessToken) {
        long expiresIn = -1;
        Instant expiresAt = accessToken.getExpiresAt();
        if (expiresAt != null) {
            expiresIn = ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("access_token", accessToken.getTokenValue());
        result.put("token_type", accessToken.getTokenType().getValue());
        result.put("expires_in", expiresIn);
        result.put("refresh_token", "");
        return result;
    }
}

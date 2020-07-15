package com.xyyh.authorization.web;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.OAuth2Authentication;
import com.xyyh.authorization.provider.AuthorizationCodeService;
import com.xyyh.authorization.provider.DefaultOAuth2RequestValidator;
import com.xyyh.authorization.provider.DefaultUserApprovalHandler;
import com.xyyh.authorization.provider.InMemoryAuthorizationCodeService;
import com.xyyh.authorization.provider.OAuth2RequestValidator;
import com.xyyh.authorization.provider.UserApprovalHandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequestMapping("/oauth/authorize")
@SessionAttributes({ "authorizationRequest" })
public class AuthorizationEndpoint implements InitializingBean {

    private static final String authorizationRequest = "authorizationRequest";

    private ClientDetailsService clientDetailsService;

    private OAuth2RequestValidator oAuth2RequestValidator;

    private UserApprovalHandler userApprovalHandler;

    private AuthorizationCodeService authorizationCodeService;

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @Autowired(required = false)
    public void setRequestValidator(OAuth2RequestValidator oAuth2RequestValidator) {
        this.oAuth2RequestValidator = oAuth2RequestValidator;
    }

    @Autowired(required = false)
    public void setUserApprovalHandler(UserApprovalHandler userApprovalHandler) {
        this.userApprovalHandler = userApprovalHandler;
    }

    @Autowired(required = false)
    public void setAuthorizationCodeService(AuthorizationCodeService authorizationCodeService) {
        this.authorizationCodeService = authorizationCodeService;
    }

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
            Map<String, Object> model,
            @RequestParam MultiValueMap<String, String> params,
            SessionStatus sessionStatus,
            Principal principal) {
        // 创建授权请求
        AuthorizationRequest authorizationRequest = AuthorizationRequest.createRequest(params);
        // 加载client信息
        ClientDetails client = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
        // 验证scope
        oAuth2RequestValidator.validateScope(authorizationRequest, client);

        // 进行请求预检
        if (preCheck(authorizationRequest, client)) {
            return null;
        } else {
            model.put("authorizationRequest", authorizationRequest);
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
            Principal principal) {
        AuthorizationRequest request = (AuthorizationRequest) model.get("authorizationRequest");

        // 获取用户授权结果
        ApprovalResult result = userApprovalHandler.approval(request, approvalParameters);

        /**
         * 如果授权通过
         */
        String responseType = request.getResponseType();
        if (result.isApprovaled()) {
            if ("code".equals(responseType)) {
                return getAuthorizationCodeResponse(request, result, (Authentication) principal);
            } else if ("token".equals(responseType)) {
                return getImplicitGrantResponse(request, result, principal);
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
    private View getImplicitGrantResponse(AuthorizationRequest request, ApprovalResult result, Principal principal) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 创建授权码请求的view
     * 
     * @param result
     * @return
     */
    private View getAuthorizationCodeResponse(
            AuthorizationRequest request,
            ApprovalResult result,
            Authentication principal) {
        Map<String, String> query = new HashMap<>();
        String state = request.getState();
        if (StringUtils.isNotEmpty(state)) {
            query.put("state", state);
        }
        // 创建并保存授权码
        String code = authorizationCodeService.create(new OAuth2Authentication(result, principal));
        query.put("code", code);
        return buildRedirectView(request.getRedirectUri(), query);
    }

    /**
     * 进行请求预检
     * 
     * @param authorizationRequest
     * @param client
     * @return
     */
    private boolean preCheck(AuthorizationRequest authorizationRequest, ClientDetails client) {
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
    private View buildRedirectView(String uri, Map<String, String> query) {
        // 将新构建的查询参数附加到url上
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);
        if (MapUtils.isNotEmpty(query)) {
            query.forEach((key, value) -> {
                builder.queryParam(key, value);
            });
        }
        RedirectView redirectView = new RedirectView(builder.toUriString());
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        return redirectView;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(oAuth2RequestValidator)) {
            oAuth2RequestValidator = new DefaultOAuth2RequestValidator();
        }

        if (Objects.isNull(userApprovalHandler)) {
            userApprovalHandler = new DefaultUserApprovalHandler();
        }

        if (Objects.isNull(authorizationCodeService)) {
            authorizationCodeService = new InMemoryAuthorizationCodeService();
        }
    }
}

package com.xyyh.authorization.web;

import com.google.common.collect.Sets;
import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2Authentication;
import com.xyyh.authorization.core.OAuth2AuthorizationCodeService;
import com.xyyh.authorization.core.OAuth2RequestScopeValidator;
import com.xyyh.authorization.exception.RequestValidationException;
import com.xyyh.authorization.provider.DefaultApprovalResult;
import com.xyyh.authorization.provider.DefaultOAuth2AuthenticationToken;
import com.xyyh.authorization.utils.OAuth2AccessTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RequestMapping("/oauth/token")
@Controller
public class TokenEndpoint {

    private static final String SPACE = " ";
    @Autowired
    private OAuth2AuthorizationCodeService authorizationCodeService;

    @Autowired
    private OAuth2AccessTokenService accessTokenService;

    @Autowired(required = false)
    private AuthenticationManager userAuthenticationManager;

    @Autowired
    private OAuth2RequestScopeValidator oAuth2RequestValidator;

    @Autowired
    private ClientDetailsService clientDetailsService;

    //    @GetMapping
    // 暂不支持get请求
    public ResponseEntity<Map<String, ?>> getAccessToken(
        Authentication principal,
        @RequestParam("grant_type") String grantType,
        @RequestParam("code") String code,
        @RequestParam("redirect_uri") String redirectUri) {
        return postAccessToken(principal, code, redirectUri);
    }

    /**
     * 密码模式的授权请求
     *
     * @return
     */
    @PostMapping(params = {"grant_type=password"})
    public ResponseEntity<Map<String, ?>> postAccessToken(
        Authentication clientAuthentication, // client的信息
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam("scope") String scope) {
        ClientDetails client = clientDetailsService.loadClientByClientId(clientAuthentication.getName());
        Set<String> scopes = Sets.newHashSet(scope.split(SPACE));
        // 验证scope
        oAuth2RequestValidator.validateScope(scopes, client);

        // 认证用户
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication user = this.userAuthenticationManager.authenticate(token);

        // 构建授权结果
        DefaultApprovalResult approvalResult = new DefaultApprovalResult();
        approvalResult.setClientId(client.getClientId());
        approvalResult.setScope(scopes);
        OAuth2Authentication authentication = new DefaultOAuth2AuthenticationToken(approvalResult, user);

        // 生成并保存token
        OAuth2AccessToken accessToken = accessTokenService.create(authentication);

        // 返回token
        return ResponseEntity.ok(OAuth2AccessTokenUtils.converterToken2Map(accessToken));
    }

    /**
     * 授权码模式的授权请求
     *
     * @param client
     * @param code
     * @param redirectUri
     * @return
     */
    @PostMapping(params = {"code", "grant_type=authorization_code"})
    public ResponseEntity<Map<String, ?>> postAccessToken(
        Authentication client,
        @RequestParam("code") String code,
        @RequestParam("redirect_uri") String redirectUri) {
        OAuth2Authentication authentication = authorizationCodeService.get(code);
        // 验证client是否匹配
        if (!StringUtils.equals(client.getName(), authentication.getClientId())) {
            throw new RequestValidationException("授权码校验错误");
        }

        // TODO 验证RedirectUri是否匹配

        // 没有找到指定的授权码信息时报错
        if (Objects.isNull(authentication)) {
            throw new RequestValidationException("指定授权码不正确");
        } else {
            OAuth2AccessToken accessToken = accessTokenService.create(authentication);
            authorizationCodeService.delete(code);
            return ResponseEntity.ok(OAuth2AccessTokenUtils.converterToken2Map(accessToken));
        }
    }

    /**
     * 刷新token
     *
     * @param refreshToken
     * @param scope
     * @return
     */
    @PostMapping(params = {"grant_type=refresh_token"})
    public ResponseEntity<Map<String, ?>> refreshToken(
        @RequestParam("refresh_token") String refreshToken,
        @RequestParam("scope") String scope) {
        return null;
    }
}

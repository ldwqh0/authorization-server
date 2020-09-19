package com.xyyh.authorization.endpoint;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.collect.CollectionUtils;
import com.xyyh.authorization.collect.Maps;
import com.xyyh.authorization.collect.Sets;
import com.xyyh.authorization.core.*;
import com.xyyh.authorization.exception.TokenRequestValidationException;
import com.xyyh.authorization.provider.DefaultOAuth2AuthenticationToken;
import com.xyyh.authorization.utils.OAuth2AccessTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * oauth2获取token的核心协议
 *
 * @see <a href=
 * "https://tools.ietf.org/html/rfc6749">https://tools.ietf.org/html/rfc6749</a>
 */
@RequestMapping("/oauth2/token")
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

    private UserDetailsService userDetailsService;

    @Autowired(required = false)
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // @GetMapping
    // 暂不支持get请求
    public Map<String, ?> getAccessToken(
        Authentication principal,
        @RequestParam("grant_type") String grantType,
        @RequestParam("code") String code,
        @RequestParam("redirect_uri") String redirectUri) {
        return null;
//        return postAccessToken(principal, code, redirectUri);
    }

    /**
     * 密码模式的授权请求
     *
     * @return accessToken信息
     * @see <a href=
     * "https://tools.ietf.org/html/rfc6749#section-4.3">https://tools.ietf.org/html/rfc6749#section-4.3</a>
     */
    @PostMapping(params = {"grant_type=password"})
    @ResponseBody
    public Map<String, ?> postAccessToken(
        @AuthenticationPrincipal(expression = "clientDetails") ClientDetails client, // client的信息
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam("scope") String scope) {
        // 验证grant type
        validGrantTypes(client, "password");

        // 验证scope
        Set<String> scopes = Sets.hashSet(scope.split(SPACE));
        oAuth2RequestValidator.validateScope(scopes, client);

        // 认证用户
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication user = this.userAuthenticationManager.authenticate(token);
        // 如果用户授权失败
        if (user.isAuthenticated()) {
            // 构建授权结果
            ApprovalResult approvalResult = ApprovalResult.of(scopes);
            OAuth2Authentication authentication = new DefaultOAuth2AuthenticationToken(approvalResult, user, null);
            // 生成并保存token
            OAuth2AccessToken accessToken = accessTokenService.save(OAuth2AccessTokenGenerator.generateAccessToken(authentication), authentication);
            // 返回token
            return OAuth2AccessTokenUtils.converterToken2Map(accessToken);
        } else {
            throw new TokenRequestValidationException("d");
        }
    }

    /**
     * 授权码模式的授权请求
     *
     * @param client      连接信息
     * @param code        授权码
     * @param redirectUri 重定向uri
     * @return accessToken信息
     * @see <a href=
     * "https://tools.ietf.org/html/rfc6749#section-4.1">https://tools.ietf.org/html/rfc6749#section-4.1</a>
     */
    @PostMapping(params = {"code", "grant_type=authorization_code"})
    @ResponseBody
    public Map<String, ?> postAccessToken(
        @AuthenticationPrincipal(expression = "clientDetails") ClientDetails client,
        @RequestParam("code") String code,
        @RequestParam("redirect_uri") String redirectUri) {
        // 使用http basic来验证client，通过AuthorizationServerSecurityConfiguration实现
        // 验证grant type
        validGrantTypes(client, "authorization_code");
        // 验证code
        OAuth2Authentication authentication = authorizationCodeService.consume(code);
        // 首先验证code是否存在
        if (Objects.isNull(authentication)) {
            throw new TokenRequestValidationException("invalid_grant");
        }
        // 验证client是否匹配code所指定的client
        if (!StringUtils.equals(client.getClientId(), authentication.getClientId())) {
            throw new TokenRequestValidationException("invalid_grant");
        }

        // 颁发token时，验证RedirectUri是否匹配
        // 颁发token时，redirect uri 必须和请求的redirect uri 一致
        if (!StringUtils.equals(redirectUri, authentication.getRequest().getRedirectUri())) {
            throw new TokenRequestValidationException("invalid_grant");
        }

        // 没有找到指定的授权码信息时报错
        OAuth2AccessToken accessToken = accessTokenService
            .save(OAuth2AccessTokenGenerator.generateAccessToken(authentication), authentication);
        // TODO 需要处理openid
        return OAuth2AccessTokenUtils.converterToken2Map(accessToken);
    }

    private void validGrantTypes(ClientDetails client, String grantType) {
        Set<AuthorizationGrantType> grantTypes = client.getAuthorizedGrantTypes();
        if (!CollectionUtils.containsAny(grantTypes, new AuthorizationGrantType(grantType))) {
            throw new TokenRequestValidationException("unauthorized_client");
        }
    }

    /**
     * 刷新token
     *
     * @param refreshToken refreshToken
     * @param scope        scope
     * @return accessToken信息
     */
    @PostMapping(params = {"grant_type=refresh_token"})
    @ResponseBody
    public Map<String, ?> refreshToken(
        @RequestParam("refresh_token") String refreshToken,
        @RequestParam("scope") String scope) {
        return null;
        // 使用refreshToken时,需要重新加载用户的信息
        // userDetailsService.loadUserByUsername();
    }

    /**
     * 其它不支持的授权类型
     *
     * @return 错误响应
     */
    @PostMapping
    public ResponseEntity<Map<String, ?>> otherwise() {
        Map<String, Object> response = Maps.hashMap();
        response.put("error", "unsupported_grant_type");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理异常请求
     *
     * @param ex 异常信息
     * @return 异常响应
     */
    @ExceptionHandler({TokenRequestValidationException.class})
    public ResponseEntity<Map<String, ?>> handleException(Exception ex) {
        Map<String, Object> response = Maps.hashMap();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * revoke a token
     *
     * @param token         要移除的token的值
     * @param tokenTypeHint tokenTypeHint
     * @param client        client信息
     * @see <a href=
     * "https://tools.ietf.org/html/rfc7009#section-2.1">https://tools.ietf.org/html/rfc7009#section-2.1</a>
     */
    @PostMapping("revoke")
    public void revoke(
        @RequestParam("token") String token,
        @RequestParam("token_type_hint") String tokenTypeHint,
        @AuthenticationPrincipal(expression = "clientDetails") ClientDetails client) {
        // TODO 需要支持跨域
        // TODO 待实现
    }

}

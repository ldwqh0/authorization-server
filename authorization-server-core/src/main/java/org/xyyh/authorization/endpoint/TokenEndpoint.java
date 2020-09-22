package org.xyyh.authorization.endpoint;

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
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.web.bind.annotation.*;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.collect.Maps;
import org.xyyh.authorization.collect.Sets;
import org.xyyh.authorization.core.*;
import org.xyyh.authorization.exception.TokenRequestValidationException;
import org.xyyh.authorization.provider.DefaultOAuth2AuthenticationToken;
import org.xyyh.authorization.utils.OAuth2AccessTokenUtils;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.xyyh.authorization.core.PkceValidator.CODE_CHALLENGE_METHOD_PLAIN;

/**
 * oauth2获取token的核心协议
 *
 * @see <a href=
 * "https://tools.ietf.org/html/rfc6749">https://tools.ietf.org/html/rfc6749</a>
 */
@RequestMapping("/oauth2/token")
public class TokenEndpoint {

    private final OAuth2AuthorizationCodeStorageService authorizationCodeService;

    private final OAuth2AccessTokenStorageService accessTokenService;

    private final OAuth2RefreshTokenStorageService refreshTokenStorageService;

    private AuthenticationManager userAuthenticationManager;

    private final TokenGenerator tokenGenerator;

    @Autowired
    private final OAuth2RequestScopeValidator oAuth2RequestValidator;

    private UserDetailsService userDetailsService;

    private final PkceValidator pkceValidator;

    public TokenEndpoint(OAuth2AuthorizationCodeStorageService authorizationCodeService, OAuth2AccessTokenStorageService accessTokenService, OAuth2RefreshTokenStorageService refreshTokenStorageService, TokenGenerator tokenGenerator, OAuth2RequestScopeValidator oAuth2RequestValidator, PkceValidator pkceValidator) {
        this.authorizationCodeService = authorizationCodeService;
        this.accessTokenService = accessTokenService;
        this.refreshTokenStorageService = refreshTokenStorageService;
        this.tokenGenerator = tokenGenerator;
        this.oAuth2RequestValidator = oAuth2RequestValidator;
        this.pkceValidator = pkceValidator;
    }

    @Autowired(required = false)
    public void setUserAuthenticationManager(AuthenticationManager userAuthenticationManager) {
        this.userAuthenticationManager = userAuthenticationManager;
    }

    @Autowired(required = false)
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    /**
     * 不支持 get请求获取token,返回415状态码
     *
     * @return
     */
    @GetMapping
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    // 暂不支持get请求
    public Map<String, ?> getAccessToken(
        Authentication principal,
        @RequestParam("grant_type") String grantType,
        @RequestParam("code") String code,
        @RequestParam("redirect_uri") String redirectUri) {
        return Collections.emptyMap();
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
        Set<String> scopes = Sets.hashSet(scope.split("[\\s+]"));
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
            OAuth2AccessToken accessToken = accessTokenService.save(tokenGenerator.generateAccessToken(client, authentication), authentication);
            // 返回token
            Map<String, Object> result = OAuth2AccessTokenUtils.converterToken2Map(accessToken);
            return addRefreshToken(result, client, authentication);
            // return result;
        } else {
            // TODO 这个错误信息需要修改
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
        @RequestParam("redirect_uri") String redirectUri,
        Map<String, String> requestParams) {
        // 使用http basic来验证client，通过AuthorizationServerSecurityConfiguration实现
        // 验证grant type
        validGrantTypes(client, "authorization_code");
        // 验证code
        OAuth2Authentication authentication = authorizationCodeService.consume(code);
        // 首先验证code是否存在
        // 没有找到指定的授权码信息时报错
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
//        authentication.getRequest().getAdditionalParameters()
        Map<String, String> additionalParameters = authentication.getRequest().getAdditionalParameters();
        validPkce(additionalParameters, requestParams);
        // TODO 根据请求进行pkce校验
        // 签发token
        OAuth2AccessToken accessToken = accessTokenService.save(tokenGenerator.generateAccessToken(client, authentication), authentication);
        // TODO 需要处理openid
        // TODO　需要确定是否返回 refresh_token
        Map<String, Object> response = OAuth2AccessTokenUtils.converterToken2Map(accessToken);
        addRefreshToken(response, client, authentication);
        return response;
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
        // TODO 使用refreshToken时,需要重新加载用户的信息
        // 返回时需要确定是否持续返回access_token
        // userDetailsService.loadUserByUsername();
    }

    @RequestMapping(params = {"grant_type=client_credentials"})
    public Map<String, ?> postAccessToken() {
        // TODO 客户端模式的的逻辑需要处理
        // 该模式下不能返回refresh_token
        return null;
    }

    /**
     * 其它不支持的授权类型
     *
     * @return 错误响应
     */
    @PostMapping
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, ?> otherwise() {
        Map<String, Object> response = Maps.hashMap();
        response.put("error", "unsupported_grant_type");
        return response;
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

    /**
     * 对请求进行pkce校验
     *
     * @param storeParams 储存的pkce参数
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7636">Proof Key for Code Exchange by OAuth Public Clients</a>
     */
    private void validPkce(Map<String, String> storeParams, Map<String, String> requestParams) {
        String codeChallenge = storeParams.get("code_challenge");
        if (StringUtils.isNotBlank(codeChallenge)) {
            String codeChallengeMethod = storeParams.getOrDefault("code_challenge_method", CODE_CHALLENGE_METHOD_PLAIN);
            String codeVerifier = requestParams.get("code_verifier");
            pkceValidator.validate(codeChallenge, codeVerifier, codeChallengeMethod);
        }
    }

    private Map<String, ?> addRefreshToken(Map<String, Object> result, ClientDetails client, OAuth2Authentication userAuthentication) {
        if (isSupportRefreshToken(client)) {
            OAuth2RefreshToken refreshToken = tokenGenerator.generateRefreshToken(client);
            refreshTokenStorageService.save(refreshToken, userAuthentication);
            result.put("refresh_token", refreshToken.getTokenValue());
        }
        return result;
    }


    private void validGrantTypes(ClientDetails client, @NotNull String grantType) {
        Set<AuthorizationGrantType> grantTypes = client.getAuthorizedGrantTypes();
        if (grantTypes.stream().map(AuthorizationGrantType::getValue)
            .noneMatch(grantType::equals)) {
            throw new TokenRequestValidationException("unauthorized_client");
        }
    }

    /**
     * 验证请求是否支持refresh_token
     *
     * @param client
     * @return
     */
    private boolean isSupportRefreshToken(ClientDetails client) {
        return this.userAuthenticationManager != null && client.getAuthorizedGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN);
    }
}

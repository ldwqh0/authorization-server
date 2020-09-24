package org.xyyh.authorization.endpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.web.bind.annotation.*;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.collect.CollectionUtils;
import org.xyyh.authorization.collect.Maps;
import org.xyyh.authorization.core.*;
import org.xyyh.authorization.exception.InvalidScopeException;
import org.xyyh.authorization.exception.RefreshTokenValidationException;
import org.xyyh.authorization.exception.TokenRequestValidationException;
import org.xyyh.authorization.provider.DefaultOAuth2AuthenticationToken;
import org.xyyh.authorization.provider.PreAuthenticatedAuthenticationToken;
import org.xyyh.authorization.provider.PreAuthenticatedProvider;
import org.xyyh.authorization.utils.OAuth2AccessTokenUtils;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.xyyh.authorization.collect.Sets.hashSet;
import static org.xyyh.authorization.core.PkceValidator.CODE_CHALLENGE_METHOD_PLAIN;

/**
 * oauth2获取token的核心协议
 *
 * @see <a href=
 * "https://tools.ietf.org/html/rfc6749">https://tools.ietf.org/html/rfc6749</a>
 */
@RequestMapping("/oauth2/token")
public class TokenEndpoint {
    private static final String SPACE_REGEX = "[\\s+]";

    private final OAuth2AuthorizationCodeStore authorizationCodeService;

    private AuthenticationManager userAuthenticationManager;

    private final OAuth2AuthorizationServerTokenServices tokenService;

    @Autowired
    private final OAuth2RequestScopeValidator scopeValidator;

    /**
     * 对用户进行无密码的校验<br>
     * 用于refresh token校验
     */
    private ProviderManager preProviderManager;

    private final PkceValidator pkceValidator;

    public TokenEndpoint(OAuth2AuthorizationCodeStore authorizationCodeService,
                         OAuth2RefreshTokenStore refreshTokenStorageService,
                         OAuth2AuthorizationServerTokenServices tokenService,
                         OAuth2RequestScopeValidator scopeValidator,
                         PkceValidator pkceValidator) {
        this.authorizationCodeService = authorizationCodeService;
        this.tokenService = tokenService;
        this.scopeValidator = scopeValidator;
        this.pkceValidator = pkceValidator;
    }

    @Autowired(required = false)
    public void setUserAuthenticationManager(AuthenticationManager userAuthenticationManager) {
        this.userAuthenticationManager = userAuthenticationManager;
    }

    @Autowired(required = false)
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        if (userDetailsService != null) {
            this.preProviderManager = new ProviderManager(Arrays.asList(new PreAuthenticatedProvider(userDetailsService)));
        }
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
     * "https://tools.ietf.org/html/rfc6749#section-4.3">Resource Owner Password Credentials Grant</a>
     */
    @PostMapping(params = {"grant_type=password"})
    @ResponseBody
    public Map<String, ?> postAccessToken(
        @AuthenticationPrincipal(expression = "clientDetails") ClientDetails client, // client的信息
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam("scope") String scope) throws TokenRequestValidationException {
        try {
            // 验证grant type
            validGrantTypes(client, "password");
            // 验证scope
            Set<String> scopes = hashSet(scope.split(SPACE_REGEX));
            scopeValidator.validateScope(scopes, client.getScopes());
            // 认证用户
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication user = this.userAuthenticationManager.authenticate(token);
            // 如果用户授权失败
            if (user.isAuthenticated()) {
                // 构建授权结果
                ApprovalResult approvalResult = ApprovalResult.of(scopes);
                OAuth2Authentication authentication = new DefaultOAuth2AuthenticationToken(null, approvalResult, client, user);
                // 生成并保存token
                OAuth2AccessToken accessToken = tokenService.createAccessToken(authentication);
                // 返回token
                Map<String, Object> result = OAuth2AccessTokenUtils.converterToken2Map(accessToken);
                return addRefreshToken(result, client, authentication, accessToken.getTokenValue());
            } else {
                throw new TokenRequestValidationException("invalid_request");
            }
        } catch (InvalidScopeException e) {
            // TODO字符需要查一下
            throw new TokenRequestValidationException("invalid grant_type");
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
        Map<String, String> requestParams) throws TokenRequestValidationException {
        // 使用http basic来验证client，通过AuthorizationServerSecurityConfiguration实现
        // 验证grant type
        validGrantTypes(client, "authorization_code");
        // 验证code
        OAuth2Authentication authentication = authorizationCodeService.consume(code);
        if ( // 首先验证code是否存在,没有找到指定的授权码信息时报错
            !Objects.isNull(authentication)
                // 验证client是否匹配code所指定的client
                && StringUtils.equals(client.getClientId(), authentication.getClient().getClientId())
                // 颁发token时，验证RedirectUri是否匹配
                // 颁发token时，redirect uri 必须和请求的redirect uri一致
                && StringUtils.equals(redirectUri, authentication.getRequest().getRedirectUri())) {
            Map<String, String> additionalParameters = authentication.getRequest().getAdditionalParameters();
            // 根据请求进行pkce校验
            validPkce(additionalParameters, requestParams);
            // 签发token
            OAuth2AccessToken accessToken = tokenService.createAccessToken(authentication);
            // TODO 需要处理openid
            Map<String, Object> response = OAuth2AccessTokenUtils.converterToken2Map(accessToken);
            // 返回 refresh_token
            return addRefreshToken(response, client, authentication, accessToken.getTokenValue());
        } else {
            throw new TokenRequestValidationException("invalid_grant");
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
        @AuthenticationPrincipal(expression = "clientDetails") ClientDetails client,
        @RequestParam("refresh_token") String refreshToken,
        @RequestParam(value = "scope", required = false) String scope
    ) throws TokenRequestValidationException {
        refreshToken = refreshToken.intern();
        // 对token进行预检，如果检测失败，抛出异常
        try {
            tokenService.preCheckRefreshToken(refreshToken);
            // 同一时刻，针对用一个refresh token,有且仅有一个线程可以读取某个refresh token的相关信息
            synchronized (refreshToken) {
                // 进行双重检查
                tokenService.preCheckRefreshToken(refreshToken);
                OAuth2Authentication preAuthentication = tokenService.getAuthenticationByRefreshToken(refreshToken).orElseThrow(RefreshTokenValidationException::new);
                // 验证传入的refresh token是否发布给该client
                if (!Objects.equals(preAuthentication.getClient().getClientId(), client.getClientId())) {
                    throw new TokenRequestValidationException("invalid_grant");
                }
                // 验证重新请求的scope不能不能大于之前的scope
                List<String> requestScopes = Optional.ofNullable(scope)
                    .map(v -> v.split(SPACE_REGEX))
                    .map(Arrays::asList)
                    .orElseGet(Collections::emptyList);
                Set<String> scopeToUse = preAuthentication.getScopes();
                if (!CollectionUtils.containsAll(preAuthentication.getScopes(), requestScopes)) {
                    throw new TokenRequestValidationException("invalid_grant");
                }
                if (CollectionUtils.isNotEmpty(requestScopes)) {
                    scopeToUse = hashSet(requestScopes);
                }
                // 使用refreshToken时,需要重新加载用户的信息
                Authentication user = new PreAuthenticatedAuthenticationToken(preAuthentication, preAuthentication.getAuthorities());
                user = preProviderManager.authenticate(user);
                // 创建一个新的OAuth2Authentication
                OAuth2Authentication authentication = new DefaultOAuth2AuthenticationToken(preAuthentication.getRequest(), ApprovalResult.of(scopeToUse), client, user);
                // TODO 需要强制更新accessToken,这里可能会返回之前的token,需要处理一下
                OAuth2AccessToken accessToken = tokenService.refreshAccessToken(refreshToken, authentication);
                // 如果验证成功，删除之前的access token
                tokenService.deleteRefreshToken(refreshToken);
                Map<String, Object> response = OAuth2AccessTokenUtils.converterToken2Map(accessToken);
                addRefreshToken(response, client, authentication, accessToken.getTokenValue());
                return response;
            }
        } catch (RefreshTokenValidationException ex) {
            throw new TokenRequestValidationException("invalid_grant");
        }
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
    private void validPkce(Map<String, String> storeParams, Map<String, String> requestParams) throws TokenRequestValidationException {
        String codeChallenge = storeParams.get("code_challenge");
        if (StringUtils.isNotBlank(codeChallenge)) {
            String codeChallengeMethod = storeParams.getOrDefault("code_challenge_method", CODE_CHALLENGE_METHOD_PLAIN);
            String codeVerifier = requestParams.get("code_verifier");
            pkceValidator.validate(codeChallenge, codeVerifier, codeChallengeMethod);
        }
    }

    /**
     * 添加refresh_token
     *
     * @param result             待添加的参数列表
     * @param client             连接信息
     * @param userAuthentication 用户授权信息
     * @return 添加之后的响应体
     */
    private Map<String, ?> addRefreshToken(Map<String, Object> result, ClientDetails client, OAuth2Authentication userAuthentication, String accessToken) {
        if (isSupportRefreshToken(client)) {
            OAuth2RefreshToken refreshToken = tokenService.createRefreshToken(accessToken, client);
            result.put("refresh_token", refreshToken.getTokenValue());
        }
        return result;
    }

    private void validGrantTypes(ClientDetails client, @NotNull String grantType) throws TokenRequestValidationException {
        Set<AuthorizationGrantType> grantTypes = client.getAuthorizedGrantTypes();
        if (grantTypes.stream().map(AuthorizationGrantType::getValue)
            .noneMatch(grantType::equals)) {
            throw new TokenRequestValidationException("unauthorized_client");
        }
    }

    /**
     * 验证请求是否支持refresh_token
     *
     * @param client 待验证的client
     * @return 支持返回true, 不支持返回false
     */
    private boolean isSupportRefreshToken(ClientDetails client) {
        return this.preProviderManager != null && client.getAuthorizedGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN);
    }

}

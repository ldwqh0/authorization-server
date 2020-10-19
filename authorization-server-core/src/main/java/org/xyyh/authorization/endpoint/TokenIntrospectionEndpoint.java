package org.xyyh.authorization.endpoint;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xyyh.authorization.core.OAuth2TokenIntrospectionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Introspection(内省) endpoint<br>
 *
 * <b>资源服务器</b>用于审查token的正确性
 *
 * @author LiDong
 * @see <a href=
 * "https://tools.ietf.org/html/rfc7662">https://tools.ietf.org/html/rfc7662</a>
 */
@RequestMapping("/oauth2/token/introspection")
public class TokenIntrospectionEndpoint {

    private final OAuth2TokenIntrospectionService tokenIntrospectionService;

    private final Map<String, Object> notExistResponse;

    public TokenIntrospectionEndpoint(OAuth2TokenIntrospectionService tokenIntrospectionService) {
        this.tokenIntrospectionService = tokenIntrospectionService;
        Map<String, Object> notExist = new HashMap<>();
        notExist.put("active", Boolean.FALSE);
        this.notExistResponse = Collections.unmodifiableMap(notExist);
    }

    /**
     * 这个接口仅对资源服务器开放，不应该被外部服务器，或者client访问到
     * <p>
     * Content-Type: application/json for a regular response,
     * application/token-introspection+jwt for a JWT-secured response.<br>
     * <p>
     * 如果请求类型是
     *
     * @param token
     * @return
     * @see <a href=
     * "https://tools.ietf.org/html/rfc7662">OAuth 2.0 Token Introspection</a>
     */
    @PostMapping(consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public Map<String, ?> introspection(
        @RequestParam("token") String token,
        // token_type_hint的可选值有 access_token ，refresh_token,默认为access_token
        @RequestParam(value = "token_type_hint", required = false, defaultValue = "access_token") String tokenTypeHint) {
        switch (tokenTypeHint) {
            case "access_token":
                return tokenIntrospectionService.inspectAccessToken(token).orElse(notExistResponse);
            case "refresh_token":
                return tokenIntrospectionService.inspectRefreshToken(token).orElse(notExistResponse);
            default:

        }
        // TODO 这里待处理
        return null;
    }


    /**
     * 如果请求的accept是application/jwt,返回jwt
     *
     * @return
     * @see <a href=
     * "https://tools.ietf.org/html/draft-ietf-oauth-jwt-introspection-response-09">https://tools.ietf.org/html/draft-ietf-oauth-jwt-introspection-response-09</a>
     */
    @PostMapping(consumes = {"application/jwt"})
    public String introspection() {
        return null;
    }
}

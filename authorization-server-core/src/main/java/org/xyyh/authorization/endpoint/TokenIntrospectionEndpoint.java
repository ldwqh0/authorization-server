package org.xyyh.authorization.endpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2ResourceServerTokenServices;
import org.xyyh.authorization.core.OAuth2ServerAccessToken;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static org.xyyh.authorization.collect.Maps.hashMap;

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

    @Autowired
    private OAuth2ResourceServerTokenServices accessTokenService;

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
        switch (token) {
            case "access_token":
                inspectAccessToken(token);
                break;
            case "refresh_token":
                inspectRefreshToken(token);
                break;
            default:

        }

//        if (Objects.equals("access_token", tokenTypeHint)) {
//            inspectAccessToken(token);
//        } else if (Objects.equals("refresh_token", tokenTypeHint)) {
//            inspectRefreshToken(token);
//        } else {
////
//        }


        // OAuth2AccessToken accessToken = accessTokenService.getAccessToken(token);
        // OAuth2Authentication authentication = accessTokenService.getAuthentication(token);
        // return OAuth2AccessTokenUtils.converterToken2IntrospectionResponse(accessToken, authentication);
        // TODO 这里待处理
        return null;
    }

    private void inspectAccessToken(String accessToken) {
        OAuth2ServerAccessToken token = accessTokenService.readAccessToken(accessToken).get();
        OAuth2Authentication authentication = accessTokenService.loadAuthentication(accessToken).get();

        String scope = StringUtils.join(token.getScopes(), " ");
        String clientId = authentication.getClient().getClientId();

        Map<String, Object> r = hashMap();
        r.put("active", true);
        r.put("scope", scope);
        r.put("client_id", clientId);
        r.put("username", authentication.getName());
        r.put("token_type", "bearer");
        r.put("exp", token.getExpiresAt().getEpochSecond());
        r.put("iat", token.getIssuedAt().getEpochSecond());
        r.put("nbf", token.getIssuedAt().getEpochSecond());
        // TODO 主题，获取用户ID
        // r.put("sub", authentication.);
        // TODO 接收对象，什么意思
        r.put("aud", "");
        // TODO 签发者
        r.put("iss", "");
        // TODO 什么叫重放攻击
        r.put("jti", token);

    }

    private void inspectRefreshToken(String refreshToken) {

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

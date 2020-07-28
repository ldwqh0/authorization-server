package com.xyyh.authorization.utils;

import com.google.common.collect.Maps;
import com.nimbusds.oauth2.sdk.TokenIntrospectionErrorResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.xyyh.authorization.core.OAuth2Authentication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.collect.Maps.newLinkedHashMap;

public interface OAuth2AccessTokenUtils {

    static final String SPACE = " ";

    public static Map<String, ?> converterToken2Map(OAuth2AccessToken accessToken) {
        long expiresIn = -1;
        Instant expiresAt = accessToken.getExpiresAt();
        if (expiresAt != null) {
            expiresIn = ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
        }
        Maps.newLinkedHashMap();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("access_token", accessToken.getTokenValue());
        result.put("token_type", accessToken.getTokenType().getValue());
        result.put("expires_in", expiresIn);
        // TODO refreshToken策略 result.put("refresh_token", "");
        // TODO scope策略 result.put("scope", ""); accessToken.getScopes();
        return result;
    }

    /**
     * 转换 Introspection Response
     *
     * @param token
     * @param authentication
     * @return
     * @see <a href=
     * "https://tools.ietf.org/html/rfc7662#section-2.2">https://tools.ietf.org/html/rfc7662#section-2.2</a>
     */
    public static Map<String, ?> converterToken2IntrospectionResponse(OAuth2AccessToken token,
                                                                      OAuth2Authentication authentication) {
        Map<String, Object> response = newLinkedHashMap();
        // 如果没有找到相关的token直接返回false
        if (Objects.isNull(token)) {
            response.put("active", Boolean.FALSE);
        } else {
            response.put("active", Boolean.TRUE);
            response.put("scope", StringUtils.join(token.getScopes(), SPACE));
            response.put("client_id", authentication.getClientId());
            response.put("username", authentication.getName());
            // TODO 什么含义
            // response.put("token_type", "");

            Instant expiresAt = token.getExpiresAt();
            if (expiresAt != null) {
                response.put("exp", expiresAt.getEpochSecond());
            }
            Instant issuedAt = token.getIssuedAt();
            if (issuedAt != null) {
                response.put("iat", issuedAt.getEpochSecond());
            }
            // TODO
            // response.put("nbf", 0);
            // TODO 这里返回用户ID
            // response.put("sub", 0);
            // TODO Audience
            // response.put("aud", 0);F
            // TODO 签发者
            // response.put("iss", value)
            // response.put("jti", value)
        }
        return response;
    }

    static TokenIntrospectionResponse converterAccessToken2IntrospectionResponse(OAuth2AccessToken token,
                                                                                 OAuth2Authentication authentication) {
        if (Objects.isNull(token)) {
            return new TokenIntrospectionErrorResponse(BearerTokenError.INVALID_TOKEN);
        } else {
            return new TokenIntrospectionSuccessResponse.Builder(Boolean.TRUE).build();
        }
    }
}

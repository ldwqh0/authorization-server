package org.xyyh.authorization.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.xyyh.authorization.collect.Maps;
import org.xyyh.authorization.core.OAuth2Authentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

import static org.xyyh.authorization.utils.OAuth2IntrospectionClaimNames.*;

public final class OAuth2AccessTokenUtils {

    private OAuth2AccessTokenUtils() {
    }

    static final String SPACE = " ";

    public static Map<String, Object> converterToken2Map(OAuth2AccessToken accessToken) {
        long expiresIn = -1;
        Instant expiresAt = accessToken.getExpiresAt();
        if (expiresAt != null) {
            expiresIn = ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
        }
        Map<String, Object> result = Maps.linkedHashMap();
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
     * @param token          要转换的token
     * @param authentication 身份验证信息
     * @return Map结果的response
     * @see <a href=
     * "https://tools.ietf.org/html/rfc7662#section-2.2">https://tools.ietf.org/html/rfc7662#section-2.2</a>
     */
    public static Map<String, ?> converterToken2IntrospectionResponse(OAuth2AccessToken token,
                                                                      OAuth2Authentication authentication) {
        Map<String, Object> response = Maps.linkedHashMap();
        // 如果没有找到相关的token直接返回false
        // TODO 需要检擦token是否过期
        if (Objects.isNull(token)) {
            response.put(ACTIVE, Boolean.FALSE);
        } else {
            response.put(ACTIVE, Boolean.TRUE);
            response.put(SCOPE, StringUtils.join(token.getScopes(), SPACE));
            response.put(CLIENT_ID, authentication.getClientId());
            response.put(USERNAME, authentication.getName());
            // 目前只支持Bearer token
            response.put(TOKEN_TYPE, "Bearer");

            Instant expiresAt = token.getExpiresAt();
            if (expiresAt != null) {
                response.put(EXPIRES_AT, expiresAt.getEpochSecond());
            }
            Instant issuedAt = token.getIssuedAt();
            if (issuedAt != null) {
                response.put(ISSUED_AT, issuedAt.getEpochSecond());
                // Not Before ,不能在什么时间之前
                response.put(NOT_BEFORE, issuedAt.getEpochSecond());
            }
            // TODO 这里返回用户ID
            // response.put("sub", 0);
            // TODO Audience 接收方，ip地址？或者其它信息
            // response.put("aud", 0);
            // TODO 签发者
            // response.put("iss", value)
            // TODO jwt的id，用于黑名单校验之类的？
            // response.put("jti", value)
        }
        return response;
    }

}

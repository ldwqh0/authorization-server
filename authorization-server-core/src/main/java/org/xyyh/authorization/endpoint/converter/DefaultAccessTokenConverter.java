package org.xyyh.authorization.endpoint.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.xyyh.authorization.collect.Maps;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2ServerAccessToken;
import org.xyyh.authorization.core.OAuth2ServerRefreshToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.xyyh.authorization.collect.Maps.hashMap;

public class DefaultAccessTokenConverter implements AccessTokenConverter {

    protected static final String SPACE = " ";

    /**
     * 转换为 access token的response
     *
     * @param token
     * @return
     */
    @Override
    public Map<String, Object> toAccessTokenResponse(OAuth2ServerAccessToken token) {
        long expiresIn = -1;
        Instant expiresAt = token.getExpiresAt();
        if (expiresAt != null) {
            expiresIn = ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
        }
        Map<String, Object> result = Maps.linkedHashMap();
        result.put(OAuth2ParameterNames.ACCESS_TOKEN, token.getTokenValue());
        result.put(OAuth2ParameterNames.TOKEN_TYPE, token.getTokenType().getValue());
        result.put(OAuth2ParameterNames.EXPIRES_IN, expiresIn);
        token.getRefreshToken()
            .map(OAuth2ServerRefreshToken::getTokenValue)
            .ifPresent(value -> result.put(OAuth2ParameterNames.REFRESH_TOKEN, value));
        return result;
    }

    /**
     * 转换 Introspection Response
     *
     * @param token          要转换的token
     * @param authentication 身份验证信息
     * @return Map结果的response
     * @see <a href=
     * "https://tools.ietf.org/html/rfc7662#section-2.2">OAuth 2.0 Token Introspection - Introspection Response</a>
     */
    @Override
    public Map<String, Object> toAccessTokenIntrospectionResponse(OAuth2ServerAccessToken token,
                                                                  OAuth2Authentication authentication) {
        String scope = StringUtils.join(token.getScopes(), SPACE);
        String clientId = authentication.getClient().getClientId();
        Map<String, Object> response = hashMap();
        response.put(OAuth2IntrospectionClaimNames.ACTIVE, true);
        response.put(OAuth2IntrospectionClaimNames.SCOPE, scope);
        response.put(OAuth2IntrospectionClaimNames.CLIENT_ID, clientId);
        response.put(OAuth2IntrospectionClaimNames.USERNAME, authentication.getName());
        response.put(OAuth2IntrospectionClaimNames.TOKEN_TYPE, "bearer");
        response.put(OAuth2IntrospectionClaimNames.EXPIRES_AT, token.getExpiresAt().getEpochSecond());
        response.put(OAuth2IntrospectionClaimNames.ISSUED_AT, token.getIssuedAt().getEpochSecond());
        response.put(OAuth2IntrospectionClaimNames.NOT_BEFORE, token.getIssuedAt().getEpochSecond());
        Object principal = authentication.getPrincipal();
        // TODO 接收对象，什么意思
        // r.put("aud", "");
        // TODO 签发者
        // iss是一个url
        // r.put("iss", "oauth2server");
        if (principal instanceof UserDetails) {
            response.put(OAuth2IntrospectionClaimNames.SUBJECT, ((UserDetails) principal).getUsername());
        }
        response.put(OAuth2IntrospectionClaimNames.JTI, token.getId());
        return response;
    }
}

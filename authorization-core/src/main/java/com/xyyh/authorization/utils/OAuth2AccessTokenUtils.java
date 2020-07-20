package com.xyyh.authorization.utils;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

public interface OAuth2AccessTokenUtils {

    public static Map<String, ?> converterToken2Map(OAuth2AccessToken accessToken) {
        long expiresIn = -1;
        Instant expiresAt = accessToken.getExpiresAt();
        if (expiresAt != null) {
            expiresIn = ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("access_token", accessToken.getTokenValue());
        result.put("token_type", accessToken.getTokenType().getValue());
        result.put("expires_in", expiresIn);
        // TODO refreshToken策略 result.put("refresh_token", "");
        // TODO scope策略 result.put("scope", ""); accessToken.getScopes();
        return result;
    }
}

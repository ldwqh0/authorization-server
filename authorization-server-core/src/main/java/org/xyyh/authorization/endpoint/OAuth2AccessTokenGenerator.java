package org.xyyh.authorization.endpoint;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.xyyh.authorization.core.OAuth2Authentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Set;

public class OAuth2AccessTokenGenerator {

    private static final StringKeyGenerator stringKeyGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder(), 33);

    public static OAuth2AccessToken generateAccessToken(OAuth2Authentication authenticationToken) {
        Instant issuedAt = Instant.now();
        // TODO 默认的过期时间
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS);
        String tokenValue = stringKeyGenerator.generateKey();
        Set<String> scope = authenticationToken.getScopes();
        return new OAuth2AccessToken(TokenType.BEARER, tokenValue, issuedAt, expiresAt, scope);
    }

}

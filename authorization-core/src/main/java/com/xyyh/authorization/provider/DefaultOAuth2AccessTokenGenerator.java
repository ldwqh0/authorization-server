package com.xyyh.authorization.provider;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

import com.xyyh.authorization.core.OAuth2AccessTokenAuthentication;
import com.xyyh.authorization.core.OAuth2ApprovalAuthenticationToken;

public class DefaultOAuth2AccessTokenGenerator implements OAuth2AccessTokenGenerator {

    private final StringKeyGenerator accessTokenGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder());

    @Override
    public OAuth2AccessTokenAuthentication generate(OAuth2ApprovalAuthenticationToken token) {
        Authentication user = token.getUser();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS);
        String tokenValue = accessTokenGenerator.generateKey();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, tokenValue, issuedAt, expiresAt,
                token.getResult().getScope());
        return new OAuth2AccessTokenAuthentication(token.getResult(), accessToken, user);
    }

}

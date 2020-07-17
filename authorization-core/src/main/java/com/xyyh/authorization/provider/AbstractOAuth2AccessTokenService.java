package com.xyyh.authorization.provider;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Set;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2Authentication;

public abstract class AbstractOAuth2AccessTokenService implements OAuth2AccessTokenService {

    private final StringKeyGenerator accessTokenGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder());

    @Override
    public OAuth2AccessToken create(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = generate(authentication);
        save(accessToken, authentication);
        return accessToken;
    }

    protected abstract void save(OAuth2AccessToken accessToken, OAuth2Authentication authentication);

    private OAuth2AccessToken generate(OAuth2Authentication authenticationToken) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS);
        String tokenValue = accessTokenGenerator.generateKey();
        Set<String> scope = authenticationToken.getScopes();
        return new OAuth2AccessToken(TokenType.BEARER, tokenValue, issuedAt, expiresAt, scope);
    }

}

package org.xyyh.authorization.provider;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2AuthorizationCode;
import org.xyyh.authorization.core.TokenGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

public
class DefaultTokenGenerator implements TokenGenerator {
    private final StringKeyGenerator stringGenerator;

    // private final ClientDetailsService clientDetailsService;
    // 授权码有效期，默认为3分钟
    private Integer periodOfValidity = 180;
    private Integer defaultAccessTokenValiditySeconds = 3600;
    private Integer defaultRefreshTokenValiditySeconds = 7200;

    public DefaultTokenGenerator() {
        this(new Base64StringKeyGenerator(Base64.getUrlEncoder(), 33));
    }

    public DefaultTokenGenerator(StringKeyGenerator stringGenerator) {
        this.stringGenerator = stringGenerator;
    }

    @Override
    public OAuth2AccessToken generateAccessToken(ClientDetails client, OAuth2Authentication oAuth2Authentication) {
        Instant issuedAt = Instant.now();
        Integer accessTokenValiditySeconds = Optional.ofNullable(client.getAccessTokenValiditySeconds()).orElse(defaultAccessTokenValiditySeconds);
        Instant expiresAt = issuedAt.plus(accessTokenValiditySeconds, ChronoUnit.SECONDS);
        String tokenValue = stringGenerator.generateKey();
        Set<String> scope = oAuth2Authentication.getScopes();
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, issuedAt, expiresAt, scope);
    }


    @Override
    public OAuth2AuthorizationCode generateAuthorizationCode() {
        String codeValue = stringGenerator.generateKey();
        Instant issueAt = Instant.now();
        // code有效期默认三分钟
        Instant expireAt = issueAt.plusSeconds(periodOfValidity);
        return new DefaultOAuth2AuthorizationCode(codeValue, issueAt, expireAt);
    }

    @Override
    public OAuth2RefreshToken generateRefreshToken(ClientDetails client) {
        Instant issuedAt = Instant.now();
        // TODO refresh token的过期时间待处理
        // Integer validitySeconds = Optional.ofNullable(client.getRefreshTokenValiditySeconds()).orElse(defaultRefreshTokenValiditySeconds);
        // Instant expiresAt = issuedAt.plus(validitySeconds, ChronoUnit.SECONDS);
        String tokenValue = stringGenerator.generateKey();
        return new OAuth2RefreshToken(tokenValue, issuedAt);
    }
}

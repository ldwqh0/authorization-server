package com.xyyh.authorization.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.client.InMemoryClientDetailsService;
import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2AuthorizationCodeService;
import com.xyyh.authorization.core.OAuth2RedirectUriValidator;
import com.xyyh.authorization.core.OAuth2RequestScopeValidator;
import com.xyyh.authorization.core.UserApprovalHandler;
import com.xyyh.authorization.provider.DefaultOAuth2RedirectUriValidator;
import com.xyyh.authorization.provider.DefaultOAuth2RequestScopeValidator;
import com.xyyh.authorization.provider.DefaultUserApprovalHandler;
import com.xyyh.authorization.provider.InMemoryAuthorizationCodeService;
import com.xyyh.authorization.provider.InMemoryOAuth2AccessTokenService;
import com.xyyh.authorization.web.AuthorizationEndpoint;
import com.xyyh.authorization.web.KeySetEndpoint;
import com.xyyh.authorization.web.TokenEndpoint;

@Configuration
public class AuthorizationServerConfiguration {

    @Bean
    @ConditionalOnMissingBean({ ClientDetailsService.class })
    public ClientDetailsService clientDetailsService() {
        return new InMemoryClientDetailsService();
    }

    @Bean
    public AuthorizationEndpoint authorizationEndpoint() {
        return new AuthorizationEndpoint();
    }

    @Bean
    public TokenEndpoint tokenEndpoint() {
        return new TokenEndpoint();
    }

    @Bean
    public KeySetEndpoint keySetEndpoint() throws JOSEException {
        return new KeySetEndpoint(keyset());
    }

    /**
     * @return
     * @throws JOSEException
     */
    @Bean
    public JWKSet keyset() throws JOSEException {
        RSAKey rsaKey = new RSAKeyGenerator(2048).keyID("default-sign").keyUse(KeyUse.SIGNATURE).generate();
        return new JWKSet(rsaKey);
    }

    /**
     * 保存Access Token
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AccessTokenService.class)
    public OAuth2AccessTokenService oAuth2AccessTokenService() {
        return new InMemoryOAuth2AccessTokenService();
    }

    /**
     * 保存 Authorization Code
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationCodeService.class)
    public OAuth2AuthorizationCodeService oAuth2AuthorizationCodeService() {
        return new InMemoryAuthorizationCodeService();
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2RequestScopeValidator.class)
    public OAuth2RequestScopeValidator oAuth2RequestValidator() {
        return new DefaultOAuth2RequestScopeValidator();
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2RedirectUriValidator.class)
    public OAuth2RedirectUriValidator oAuth2RedirectUriValidator() {
        return new DefaultOAuth2RedirectUriValidator();
    }

    @Bean
    @ConditionalOnMissingBean(UserApprovalHandler.class)
    public UserApprovalHandler userApprovalHandler() {
        return new DefaultUserApprovalHandler();
    }

}

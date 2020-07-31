package com.xyyh.authorization.configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.client.InMemoryClientDetailsService;
import com.xyyh.authorization.core.*;
import com.xyyh.authorization.endpoint.AuthorizationEndpoint;
import com.xyyh.authorization.endpoint.JWKSetEndpoint;
import com.xyyh.authorization.endpoint.TokenEndpoint;
import com.xyyh.authorization.endpoint.TokenIntrospectionEndpoint;
import com.xyyh.authorization.provider.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationServerConfiguration {

    @Bean
    @ConditionalOnMissingBean({ClientDetailsService.class})
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
    public JWKSetEndpoint keySetEndpoint() throws JOSEException {
        return new JWKSetEndpoint(keyset());
    }

    @Bean
    public TokenIntrospectionEndpoint tokenIntrospectionEndpoint() {
        return new TokenIntrospectionEndpoint();
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
        return new ApprovalStoreUserApprovalHandler();
    }

    @ConditionalOnBean(ApprovalStoreUserApprovalHandler.class)
    @ConditionalOnMissingBean(ApprovalStoreService.class)
    public ApprovalStoreService storeService() {
        return new InMemoryApprovalStoreService();
    }
}

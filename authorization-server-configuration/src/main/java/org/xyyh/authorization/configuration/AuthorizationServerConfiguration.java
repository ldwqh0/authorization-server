package org.xyyh.authorization.configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xyyh.authorization.client.ClientDetailsService;
import org.xyyh.authorization.client.InMemoryClientDetailsService;
import org.xyyh.authorization.core.*;
import org.xyyh.authorization.endpoint.AuthorizationEndpoint;
import org.xyyh.authorization.endpoint.JWKSetEndpoint;
import org.xyyh.authorization.endpoint.TokenEndpoint;
import org.xyyh.authorization.endpoint.TokenIntrospectionEndpoint;
import org.xyyh.authorization.provider.*;

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
    public TokenEndpoint tokenEndpoint(OAuth2AuthorizationCodeService authorizationCodeService,
                                       OAuth2AccessTokenService accessTokenService,
                                       OAuth2RequestScopeValidator oAuth2RequestValidator,
                                       TokenGenerator tokenGenerator) {
        return new TokenEndpoint(authorizationCodeService, accessTokenService, tokenGenerator, oAuth2RequestValidator);
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
    public UserApprovalHandler userApprovalHandler(ApprovalStoreService approvalStoreService) {
        return new ApprovalStoreUserApprovalHandler(approvalStoreService);
    }

    @Bean
    @ConditionalOnMissingBean(ApprovalStoreService.class)
    public ApprovalStoreService approvalStoreService() {
        return new InMemoryApprovalStoreService();
    }

    @Bean
    @ConditionalOnMissingBean(TokenGenerator.class)
    public TokenGenerator tokenGenerator() {
        return new DefaultTokenGenerator();
    }
}

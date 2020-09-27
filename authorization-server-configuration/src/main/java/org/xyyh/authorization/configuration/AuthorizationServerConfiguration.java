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
    public AuthorizationEndpoint authorizationEndpoint(ClientDetailsService clientDetailsService,
                                                       OAuth2AuthorizationRequestValidator oAuth2RequestValidator,
                                                       UserApprovalHandler userApprovalHandler,
                                                       OAuth2AuthorizationCodeStore authorizationCodeService,
                                                       OAuth2AccessTokenStore accessTokenService,
                                                       OAuth2AuthorizationServerTokenServices tokenServices) {
        return new AuthorizationEndpoint(
            clientDetailsService,
            oAuth2RequestValidator,
            userApprovalHandler,
            authorizationCodeService,
            tokenServices);
    }

    @Bean
    public TokenEndpoint tokenEndpoint(OAuth2AuthorizationCodeStore authorizationCodeService,
                                       OAuth2RefreshTokenStore refreshTokenStorageService,
                                       PkceValidator pkceValidator,
                                       OAuth2AuthorizationServerTokenServices tokenService,
                                       OAuth2RequestScopeValidator requestScopeValidator) {
        return new TokenEndpoint(authorizationCodeService,
            refreshTokenStorageService,
            tokenService,
            requestScopeValidator,
            pkceValidator);
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
    @ConditionalOnMissingBean(OAuth2AccessTokenStore.class)
    public OAuth2AccessTokenStore oAuth2AccessTokenService() {
        return new InMemoryAccessTokenStore();
    }

    /**
     * 保存 Authorization Code
     *
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationCodeStore.class)
    public OAuth2AuthorizationCodeStore oAuth2AuthorizationCodeService() {
        return new InMemoryAuthorizationCodeStore();
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2RefreshTokenStore.class)
    public OAuth2RefreshTokenStore refreshTokenStorageService() {
        return new InMemoryRefreshTokenStore();
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2RedirectUriValidator.class)
    public OAuth2RedirectUriValidator oAuth2RedirectUriValidator() {
        return new DefaultOAuth2RedirectUriValidator();
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2RequestScopeValidator.class)
    public OAuth2RequestScopeValidator requestScopeValidator() {
        return new DefaultOAuth2RequestScopeValidator();
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationRequestValidator.class)
    public OAuth2AuthorizationRequestValidator oAuth2RequestValidator(OAuth2RedirectUriValidator redirectUriValidator, OAuth2RequestScopeValidator requestScopeValidator) {
        return new DefaultOAuth2AuthorizationRequestValidator(redirectUriValidator, requestScopeValidator);
    }


    @Bean
    @ConditionalOnMissingBean(UserApprovalHandler.class)
    public UserApprovalHandler userApprovalHandler(ApprovalResultStore approvalStoreService) {
        return new ApprovalStoreUserApprovalHandler(approvalStoreService);
    }

    @Bean
    @ConditionalOnMissingBean(ApprovalResultStore.class)
    public ApprovalResultStore approvalStoreService() {
        return new InMemoryApprovalStore();
    }

    @Bean
    @ConditionalOnMissingBean(PkceValidator.class)
    public PkceValidator pkceValidator() {
        return new CompositePkceValidator(
            new PlainPkceValidator(),
            new S256PkceValidator()
        );
    }

    @Bean
    @ConditionalOnMissingBean({OAuth2AuthorizationServerTokenServices.class, OAuth2ResourceServerTokenServices.class})
    public DefaultTokenService tokenService(OAuth2AccessTokenStore tokenStorageService, OAuth2RefreshTokenStore refreshTokenStore) {
        return new DefaultTokenService(tokenStorageService);
    }
}

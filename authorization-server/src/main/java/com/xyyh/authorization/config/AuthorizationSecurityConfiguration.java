package com.xyyh.authorization.config;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.xyyh.authorization.client.BaseClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.client.InMemoryClientDetailsService;
import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2AuthorizationCodeService;
import com.xyyh.authorization.core.OAuth2RequestValidator;
import com.xyyh.authorization.provider.ClientDetailsUserDetailsService;
import com.xyyh.authorization.provider.DefaultOAuth2RequestValidator;
import com.xyyh.authorization.provider.InMemoryAuthorizationCodeService;
import com.xyyh.authorization.provider.InMemoryOAuth2AccessTokenService;
import com.xyyh.authorization.web.AuthorizationEndpoint;

@EnableWebSecurity
@Configuration
@Order(99)
public class AuthorizationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.formLogin().disable()
                .requestMatchers()
                .antMatchers("/oauth/token")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new ClientDetailsUserDetailsService(clientDetailsService()))
                .passwordEncoder(passwordEncoder());
    }

    private PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return StringUtils.equals(rawPassword, encodedPassword);
            }

            @Override
            public String encode(CharSequence encodedPassword) {
                return encodedPassword.toString();
            }
        };
    }

    @Bean
    public AuthorizationEndpoint authorizationEndpoint() {
        return new AuthorizationEndpoint();
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        InMemoryClientDetailsService cds = new InMemoryClientDetailsService();
        cds.addClient(new BaseClientDetails("app", "123456", Collections.singleton("openid")));
        return cds;
    }

    /**
     * 保存Access Token
     * 
     * @return
     */
    @Bean
    public OAuth2AccessTokenService oAuth2AccessTokenService() {
        return new InMemoryOAuth2AccessTokenService();
    }

    /**
     * 保存 Authorization Code
     * 
     * @return
     */
    @Bean
    public OAuth2AuthorizationCodeService oAuth2AuthorizationCodeService() {
        return new InMemoryAuthorizationCodeService();
    }

    @Bean
    public OAuth2RequestValidator oAuth2RequestValidator() {
        return new DefaultOAuth2RequestValidator();
    }
}

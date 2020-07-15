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
import com.xyyh.authorization.provider.ClientDetailsUserDetailsService;
import com.xyyh.authorization.web.AuthorizationEndpoint;

@EnableWebSecurity
@Configuration
@Order(99)
public class AuthorizationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthorizationEndpoint authorizationEndpoint() {
        AuthorizationEndpoint endPoint = new AuthorizationEndpoint();
        endPoint.setClientDetailsService(clientDetailsService());
        return endPoint;
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        InMemoryClientDetailsService cds = new InMemoryClientDetailsService();
        cds.addClient(new BaseClientDetails("app", "123456", Collections.singleton("openid")));
        return cds;
    }

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
}

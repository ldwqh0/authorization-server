package com.demo.authorization.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2Authentication;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AccessTokenService oAuth2AccessTokenService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
//        http.oauth2ResourceServer().jwt().jwkSetUri("");
        http.oauth2ResourceServer().opaqueToken().introspector(introspector());
    }

    private OpaqueTokenIntrospector introspector() {
        return new OpaqueTokenIntrospector() {
            @Override
            public OAuth2AuthenticatedPrincipal introspect(String token) {
                OAuth2Authentication authentication = oAuth2AccessTokenService.getAuthentication(token);
//                Authentication user = (Authentication) authentication.getDetails();
                @SuppressWarnings("unchecked")
                Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication
                        .getAuthorities();
                String name = authentication.getName();
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("sub", name);
                attributes.put("principal", authentication.getPrincipal());
                return new DefaultOAuth2AuthenticatedPrincipal(name, attributes, authorities);
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = passwordEncoder();
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder.encode("123456"))
                .roles("ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "userAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

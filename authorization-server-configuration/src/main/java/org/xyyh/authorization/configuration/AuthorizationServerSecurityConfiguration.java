package org.xyyh.authorization.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xyyh.authorization.client.ClientDetailsService;
import org.xyyh.authorization.provider.ClientDetailsUserDetailsService;

@Order(99)
@EnableWebSecurity
public class AuthorizationServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ClientDetailsService clientDetailsService;

    public AuthorizationServerSecurityConfiguration(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/oauth2/token", "/oauth2/certs", "/oauth2/token/introspection");
        http.authorizeRequests()
            .antMatchers("/oauth2/certs").permitAll()
            .anyRequest().authenticated();
        http.formLogin().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        http.httpBasic();
        http.csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new ClientDetailsUserDetailsService(clientDetailsService))
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

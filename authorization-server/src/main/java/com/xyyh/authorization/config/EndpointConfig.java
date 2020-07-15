package com.xyyh.authorization.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xyyh.authorization.client.BaseClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.client.InMemoryClientDetailsService;
import com.xyyh.authorization.web.AuthorizationEndpoint;

@Configuration
public class EndpointConfig {

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

}

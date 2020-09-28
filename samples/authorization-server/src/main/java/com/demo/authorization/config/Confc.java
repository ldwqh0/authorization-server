package com.demo.authorization.config;

import com.google.common.collect.Sets;
import com.xyyh.authorization.client.BaseClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.client.InMemoryClientDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Confc {

    @Bean
    public ClientDetailsService clientDetailsService() {
        InMemoryClientDetailsService cds = new InMemoryClientDetailsService();
        cds.addClient(new BaseClientDetails(
            "app",
            "123456",
            Sets.newHashSet("openid", "email"),
            Sets.newHashSet("https://www.baidu.com"),
            Sets.newHashSet("authorization_code", "implicit", "password")));
        cds.addClient(new BaseClientDetails("resource", "123456"));
        return cds;
    }

}

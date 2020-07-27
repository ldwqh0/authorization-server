package com.demo.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;
import com.xyyh.authorization.client.BaseClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;
import com.xyyh.authorization.client.InMemoryClientDetailsService;

@Configuration
public class Confc {

    @Bean
    public ClientDetailsService clientDetailsService() {
        InMemoryClientDetailsService cds = new InMemoryClientDetailsService();
        cds.addClient(new BaseClientDetails(
                "app",
                "123456",
                Sets.newHashSet("openid"),
                Sets.newHashSet("https://www.baidu.com"),
                Sets.newHashSet("authorization_code")));
        return cds;
    }

}

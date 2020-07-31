package com.xyyh.authorization.endpoint;

import com.xyyh.authorization.endpoint.request.ClientRegistrationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/oauth2/clients")
public class ClientRegistrationEndpoint {

    /**
     * 客户端动态注册端点
     *
     * @param request
     */
    @PostMapping
    public void registry(@RequestBody ClientRegistrationRequest request) {

//        resonse.toSuccessResponse().
    }
}

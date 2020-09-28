package com.demo.authorization.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    /**
     * 返回用户的信息， 用户信息可以包含<a href=
     * "https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims</a>所列的标准字段<br>
     * 也可以返回自定义字段
     *
     * @return
     */
    @GetMapping
    public Map<String, Object> userinfo(@AuthenticationPrincipal UserDetails user) {
        Map<String, Object> response = new HashMap<String, Object>();
        String username = user.getUsername();
        response.put("sub", username);
        return response;
    }

}

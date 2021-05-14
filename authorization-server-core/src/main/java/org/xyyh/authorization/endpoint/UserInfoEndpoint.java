package org.xyyh.authorization.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("userinfo")
@RestController
public class UserInfoEndpoint {

    @GetMapping
    public void getUserInfo() {

    }

}

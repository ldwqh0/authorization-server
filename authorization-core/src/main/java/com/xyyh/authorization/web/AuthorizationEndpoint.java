package com.xyyh.authorization.web;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

public class AuthorizationEndpoint {

    @RequestMapping("/oauth/authorize")
    public ModelAndView authorize(
            Map<String, Object> model, 
            @RequestParam Map<String, String> parameters,
            SessionStatus sessionStatus,
            Principal principal) {
        return null;
    }

}

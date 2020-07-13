package com.xyyh.authorization.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/oauth/authorize")
public class AuthorizationEndpoint {

    @GetMapping
    public ModelAndView authorize(
        Map<String, Object> model,
        @RequestParam Map<String, String> parameters,
        SessionStatus sessionStatus,
        Principal principal) {
        return new ModelAndView("/oauth/confirm_access", model);
    }

    @PostMapping
    public View approveOrDeny(
        @RequestParam Map<String, String> approvalParameters,
        Map<String, ?> model,
        SessionStatus sessionStatus,
        Principal principal) {

        return null;
    }
}

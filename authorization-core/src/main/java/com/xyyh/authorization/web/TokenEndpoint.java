package com.xyyh.authorization.web;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@RequestMapping("/oauth/token")
@Controller
public class TokenEndpoint {

    @GetMapping
    public ResponseEntity getAccessToken(
        Principal principal,
        @RequestParam Map<String, String> parameters) {
        return postAccessToken(principal, parameters);
    }

    @PostMapping
    public ResponseEntity postAccessToken(
        Principal principal,
        @RequestParam Map<String, String> parameters) {
        return ResponseEntity.ok().build();
    }
}

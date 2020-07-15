package com.xyyh.authorization.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
    public ResponseEntity<OAuth2AccessToken> getAccessToken(
            Principal principal,
            @RequestParam("grant_type") String grantType,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri) {
        return postAccessToken(principal, grantType, code, redirectUri);
    }

    @PostMapping(params = "code")
    public ResponseEntity<OAuth2AccessToken> postAccessToken(
            Principal principal,
            @RequestParam("grant_type") String grantType,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri) {
        return ResponseEntity.ok().build();
    }
}

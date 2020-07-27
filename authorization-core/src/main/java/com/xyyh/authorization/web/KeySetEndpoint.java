package com.xyyh.authorization.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jose.jwk.JWKSet;

import net.minidev.json.JSONObject;

@RequestMapping("/oauth2/certs")
public class KeySetEndpoint {

    private final JWKSet keyset;

    public KeySetEndpoint(JWKSet keyset) {
        this.keyset = keyset;
    }

    @GetMapping
    @ResponseBody
    public JSONObject get() {
        return keyset.toPublicJWKSet().toJSONObject();
    }
}

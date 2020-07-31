package com.xyyh.authorization.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jose.jwk.JWKSet;

import net.minidev.json.JSONObject;

/**
 * JWK set endpoint
 *
 * @see
 */
@RequestMapping("/oauth2/certs")
public class JWKSetEndpoint {

    private final JWKSet keyset;

    public JWKSetEndpoint(JWKSet keyset) {
        this.keyset = keyset;
    }

    @GetMapping
    @ResponseBody
    public JSONObject get() {
        return keyset.toPublicJWKSet().toJSONObject();
    }
}

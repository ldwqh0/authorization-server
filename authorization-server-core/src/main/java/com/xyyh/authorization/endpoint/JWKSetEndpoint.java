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

    private final JWKSet keySet;

    public JWKSetEndpoint(JWKSet keySet) {
        this.keySet = keySet;
    }

    @GetMapping
    @ResponseBody
    public JSONObject get() {
        return keySet.toPublicJWKSet().toJSONObject();
    }
}

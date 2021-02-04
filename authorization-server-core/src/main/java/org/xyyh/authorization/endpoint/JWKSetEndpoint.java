package org.xyyh.authorization.endpoint;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jose.jwk.JWKSet;

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
    public Map<String, Object> get() {
        return keySet.toPublicJWKSet().toJSONObject();
    }
}

package org.xyyh.authorization.test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

public class TestJwt {

    @Test
    public void testGenerateJwt() throws JOSEException, ParseException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.RS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .claim("A", "b")
            .build();
        SignedJWT jwt = new SignedJWT(header, claimsSet);
        RSAKey jwk = keyset().getKeyByKeyId("default-sign").toRSAKey();
        JWSSigner signer = new RSASSASigner(jwk);
        jwt.sign(signer);
        String token = jwt.serialize();

        SignedJWT jwt2 = SignedJWT.parse(token);
        System.out.println(jwt2.getJWTClaimsSet());
        System.out.println(jwt.serialize());
    }


    public JWKSet keyset() throws JOSEException {
        RSAKey rsaKey = new RSAKeyGenerator(2048).keyID("default-sign").keyUse(KeyUse.SIGNATURE).generate();
        return new JWKSet(rsaKey);
    }
}

package com.xyyh.t;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import net.minidev.json.JSONObject;

public class TestJwk {
    RSAKey jwk;

    public TestJwk() throws JOSEException {
        this.jwk = new RSAKeyGenerator(2048).keyID("minimal-ASA").keyUse(KeyUse.SIGNATURE).generate();
    }

    @Test
    public void tt() throws JOSEException, ParseException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID()).build();
        JSONObject json = new JSONObject();
        json.appendField("name", "adgd");
        Payload payload = new Payload(json);
        JWSObject obj = new JWSObject(header, payload);
        obj.sign(new RSASSASigner(jwk.toRSAPrivateKey()));
        String key = obj.serialize();
        System.out.println(key);
        verifyKey(key);

    }

    private void verifyKey(String key) throws ParseException, JOSEException {
        JWSObject jws = JWSObject.parse(key);
        // 测试校验结果
        System.out.println(jws.verify(new RSASSAVerifier(jwk.toRSAPublicKey())));
        System.out.println(jws.getPayload().toJSONObject());

    }

}

package com.xyyh.t;

import java.security.SecureRandom;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import net.minidev.json.JSONObject;

public class JwsTest {

    @Test
    public void genJwt() throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JSONObject json = new JSONObject();
        json.appendField("name", "adgd");
        Payload payload = new Payload(json);
        JWSObject obj = new JWSObject(header, payload);
        byte[] sharedKey = new byte[32];
        new SecureRandom().nextBytes(sharedKey);
//        RSASSASigner s=new RSASSASigner(privateKey);
        obj.sign(new MACSigner(sharedKey));
        System.out.println(obj.serialize());
    }

    @Test
    public void se() throws ParseException, JOSEException {
        JWSObject obj = JWSObject
                .parse("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiYWRnZCJ9.GosOrDI7s5nscIwc1GKW5sCv6i0tKEjxjZ3zvaOhDv8");
        Payload payload = obj.getPayload();
        JSONObject json = payload.toJSONObject();
        byte[] sharedKey = new byte[32];
        new SecureRandom().nextBytes(sharedKey);
        JWSVerifier verifier = new MACVerifier(sharedKey);
        System.out.println(obj.verify(verifier));
        System.out.println(json);
    }
}

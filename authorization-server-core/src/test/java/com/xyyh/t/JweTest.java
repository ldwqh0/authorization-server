package com.xyyh.t;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;

import net.minidev.json.JSONObject;

public class JweTest {

    @Test
    public void a() throws JOSEException, NoSuchAlgorithmException, ParseException {
        JWEAlgorithm alg = JWEAlgorithm.DIR;
        EncryptionMethod enc = EncryptionMethod.A128GCM;
        JWEHeader header = new JWEHeader(alg, enc);
        JSONObject obj = new JSONObject();
        obj.appendField("name", "basd");
        Payload payload = new Payload(obj);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        JWEObject jwe = new JWEObject(header, payload);
        JWEEncrypter encrypter = new DirectEncrypter(key);
        jwe.encrypt(encrypter);
        String s = jwe.serialize();
        System.out.println(s);
        JWEObject p = JWEObject.parse(s);
        p.decrypt(new DirectDecrypter(key));
        System.out.println(p.getPayload());
    }
}

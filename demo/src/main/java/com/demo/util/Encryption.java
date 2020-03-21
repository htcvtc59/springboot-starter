package com.demo.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    public static final String HMACSHA256 = "HmacSHA256";

    public static String encrypt(String s, String d) {
        try {
            Mac sha256_HMAC = Mac.getInstance(HMACSHA256);
            SecretKeySpec secret_key = new SecretKeySpec(s.getBytes("UTF-8"), HMACSHA256);
            sha256_HMAC.init(secret_key);

            return Hex.encodeHexString(sha256_HMAC.doFinal(d.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }
}

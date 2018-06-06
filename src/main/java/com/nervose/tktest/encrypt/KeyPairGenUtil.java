package com.nervose.tktest.encrypt;

import sun.misc.BASE64Encoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Description:
 * Author:             吴兴华
 * CreateTime:	       2018/5/18 9:55
 * ModifiedBy:
 * ModifiedTime:
 */
public class KeyPairGenUtil {
    private static int keyLength = 2048;

    public static String[] createKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            KeyPair kp = kpg.genKeyPair();
            PublicKey public_key = kp.getPublic();
            PrivateKey private_key = kp.getPrivate();
            BASE64Encoder b64 = new BASE64Encoder();
            String pkStr = b64.encode(public_key.getEncoded());
            String skStr = b64.encode(private_key.getEncoded());
            String[] keyPairs = new String[]{pkStr, skStr};
            return keyPairs;
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String[] keyPairs=createKeyPair();
        System.out.println(keyPairs[0].length());
        System.out.println(keyPairs[1].length());
    }
}

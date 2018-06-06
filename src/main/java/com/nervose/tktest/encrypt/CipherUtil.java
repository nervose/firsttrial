package com.nervose.tktest.encrypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;


/**
 * @task: BXSALE-986
 * @discrption: Cipher support text message encryption and decryption
 * @author: Aere
 * @date: 2016/11/7 15:12
 * @version: 1.0.0
 * @extra: ##########         How to generate RSA key pair         ##########
 * 1. Generate original private key of 2^n bit length
 * openssl genrsa -out private.pem 2048
 * 2. Export public key of X509 Encoded format
 * openssl rsa -in private.pem -out public_key.pem -pubout
 * 3. Export private key of pkcs8 Encoded format
 * openssl pkcs8 -topk8 -in private.pem -out pkcs8_private_key.pem -nocrypt
 * <p>
 * ##########                 How to use it                ##########
 * Check the demo in main function
 */
public abstract class CipherUtil {
    public static final String AES = "AES";
    public static final String DES = "DES";
    public static final String RSA = "RSA";
    private static final int RSA_LIMIT = 200;
    private static final String BASE_CHAR_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    protected Key key;
    protected String charset;

    private CipherUtil() {
    }

    private static String generateRandString(int length) {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            stringBuffer.append(BASE_CHAR_STR.charAt(random.nextInt(62)));
        }
        return stringBuffer.toString();
    }

    public static CipherUtil build(String type, String password, String charset) {
        if (AES.equalsIgnoreCase(type)) {
            return CipherAES.buildAES(password, charset);
        } else if (DES.equalsIgnoreCase(type)) {
            return CipherDES.buildDES(password, charset);
        } else if (RSA.equalsIgnoreCase(type)) {
            return CipherRSA.buildRSA(password, charset);
        }
        return null;
    }

    public abstract String encryptString(String str);

    public abstract String decryptString(String str);

    private static class CipherAES extends CipherUtil {
        private static CipherUtil buildAES(String password, String charset) {
            CipherAES cipherAES = new CipherAES();
            try {
                KeyGenerator kGen = KeyGenerator.getInstance(AES);
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(password.getBytes());
                kGen.init(128, secureRandom);
                SecretKey secretKey = kGen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                cipherAES.key = new SecretKeySpec(enCodeFormat, AES);
                cipherAES.charset = charset;
            } catch (Exception e) {
            }
            return cipherAES;
        }

        private String encryptAES(String s) {
            try {
                Cipher cipher = Cipher.getInstance(AES);
                cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
                byte[] byteContent = s.getBytes(charset);
                byte[] bytes = cipher.doFinal(byteContent);
                //String temp=CipherUtil.parseByte2HexStr();
                return new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(bytes), charset).trim();
            } catch (Exception e) {
                return null;
            }
        }

        private String decryptAES(String s) {
            try {
                Cipher cipher = Cipher.getInstance(AES);
                cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
                byte[] data = s.getBytes(charset);
//                log.info("aes解密前的string：{}",s);
                byte[] result = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(data));
                return new String(result, charset).trim();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String encryptString(String str) {
            return encryptAES(str);
        }

        @Override
        public String decryptString(String str) {
            return decryptAES(str);
        }
    }

    private static class CipherDES extends CipherUtil {

        private SecureRandom sr;

        private static CipherUtil buildDES(String password, String charset) {
            CipherDES cipherDES = new CipherDES();
            try {
                DESKeySpec dks = new DESKeySpec(password.getBytes(charset));
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
                cipherDES.sr = new SecureRandom();
                cipherDES.key = keyFactory.generateSecret(dks);
                cipherDES.charset = charset;
            } catch (Exception e) {
            }
            return cipherDES;
        }

        @Override
        public String encryptString(String str) {
            try {
                Cipher cipher = Cipher.getInstance(DES);
                cipher.init(Cipher.ENCRYPT_MODE, key, sr);
                byte[] bytes = cipher.doFinal(str.getBytes(charset));
                return new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(bytes), charset).trim();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String decryptString(String str) {
            try {
                Cipher cipher = Cipher.getInstance(DES);
                cipher.init(Cipher.DECRYPT_MODE, key, sr);
                byte[] result = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(str.getBytes(charset)));
                return new String(result, charset).trim();
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static class CipherRSA extends CipherUtil {

        KeyFactory keyFactory;

        private static CipherUtil buildRSA(String password, String charset) {
            CipherRSA cipherRSA = new CipherRSA();
            try {
                byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(password.getBytes(charset));
                cipherRSA.keyFactory = KeyFactory.getInstance(RSA);
                Key key = cipherRSA.loadPublicKey(bytes);// Try to treated as Public Key
                if (key == null) {//Not a valid Public Key
                    key = cipherRSA.loadPrivateKey(bytes);// Try to treated as Private Key
                    if (key == null) {//Neither a valid Private Key
                        return null;
                    }
                }
                cipherRSA.key = key;
                cipherRSA.charset = charset;
            } catch (Exception e) {
                return null;
            }
            return cipherRSA;
        }

        private Key loadPublicKey(byte[] bytes) {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            try {
                return keyFactory.generatePublic(keySpec);
            } catch (InvalidKeySpecException e) {
                return null;
            }
        }

        private Key loadPrivateKey(byte[] bytes) {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            try {
                return keyFactory.generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                return null;
            }
        }

        @Override
        public String encryptString(String str) {
            try {
                Cipher cipher = Cipher.getInstance(RSA);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                if (str.getBytes(charset).length > RSA_LIMIT) {
//                    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//                    keyGen.init(256); // for example
//                    byte[] aesKey = keyGen.generateKey().getEncoded();
//                    byte[] bytes = cipher.doFinal(aesKey);
//                    String key = new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(bytes), charset);
//                    CipherUtil cipherAES = CipherAES.buildAES(new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(aesKey), charset), charset);
//                    String data = cipherAES.encryptString(str);
//                    return key + "." + data;
                    String aeskey=CipherUtil.generateRandString(30);
                    String data=CipherUtil.build(AES,aeskey,"UTF-8").encryptString(str);
                    byte[] bytes = cipher.doFinal(aeskey.getBytes(charset));
                    String aeskeycip=new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(bytes), charset).trim();
                    return aeskeycip+"."+data;
                } else {
                    byte[] bytes = cipher.doFinal(str.getBytes(charset));
                    return new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(bytes), charset).trim();
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String decryptString(String str) {
            try {
                Cipher cipher = Cipher.getInstance(RSA);
                cipher.init(Cipher.DECRYPT_MODE, key);
                int index;
                if ((index = str.indexOf('.')) != -1) {
//                    log.info("使用的charset是{}",charset);
//                    byte[] aesKey = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(str.substring(0, index).getBytes(charset)));
//                    String aesKeyStr = new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(aesKey), charset);
//                    log.info("使用的aesKeyStr:{}",aesKeyStr);
//                    CipherUtil cipherAES = CipherAES.buildAES(aesKeyStr, charset);
//                    return cipherAES.decryptString(str.substring(index + 1));
                    String aeskey = new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(str.substring(0,index).getBytes(charset))),charset).trim();
                    return CipherUtil.build(AES,aeskey,"UTF-8").decryptString(str.substring(index));
                } else {
//                    log.info("直接解码");
                    byte[] result = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(str.getBytes(charset)));
                    return new String(result, charset).trim();
                }
            } catch (Exception e) {
                return null;
            }
        }
    }
    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args){
        try {
            CipherUtil cipherAESE = CipherUtil.build(AES, "rh6n8jaUURIQ1BjQnLbdnaZtr54SCQ7R", "UTF-8");
            CipherUtil cipherAESD = CipherUtil.build(AES, "rh6n8jaUURIQ1BjQnLbdnaZtr54SCQ7R", "UTF-8");
            String origin = "Hello world AES! 字符串";
            String encrypt = cipherAESE.encryptString("{\n" +
                    "\t\"flowId\": \"CI1524466765964\"\n" +
                    "}");
            String decrypt = cipherAESD.decryptString("f0yx0fPrHKICIEoD8k8GX62M801ZPDXIipIA5AGihAMGV-gcRKapakCBkfl6o5ZyN-22DEThpohgPRduEwHcK-1BMsg4GLs1_nQJ2_ctkiiTa0t95Xv4WqUqjAlLVc1CE_t3wJAAYZSNNBR9EzENgPb3lV7fUrpfcBxHHqTmRSbkR3G2_EMjD65R7QGxoWlLzpnk0i8ZSSrsHiq4s0BYog==");
            System.out.println(decrypt + origin.equals(decrypt));

            CipherUtil cipherDESE = CipherUtil.build(DES, "DES15#Gf^3H", "UTF-8");
            CipherUtil cipherDESD = CipherUtil.build(DES, "DES15#Gf^3H", "UTF-8");
            origin = "Hello world DES! 字符串";
            encrypt = cipherDESE.encryptString(origin);
            decrypt = cipherDESD.decryptString(encrypt);
            System.out.println(decrypt + origin.equals(decrypt));

            String pubKey;
            String pemKey;
            String[] keyPairs=KeyPairGenUtil.createKeyPair();
            pubKey=keyPairs[0];
            pemKey=keyPairs[1];
            pemKey="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCJehpRXHkm4mOF5+r+YosmqRCz\n" +
                    "Jz8Fx9D8LtlhfAeb6dse3eyXwBqQk2nloJDG2URDOZ6cvvONONB0In2jzp6TXzWNUVC+x9GxhY2L\n" +
                    "96OI4NAbnf1eePAuDzBU/sCWxlTDkE0Upi3P5GLI7jWhXsLy1d0koA3YdDnVZL9wE8KP5YVnX0jU\n" +
                    "HAjHUnZ3v8Gj8orlAP+IPbGnf1QUPyucOY27161J/LnB7thtDHA32/N84UcerPIEfJ46WFbJWzx8\n" +
                    "CHTpyN22vDJ5ERZ+nOpwzQ1BnTkjE/R/ZGNVUPwsgvNaadic3fn/qBVf8+pa7Il5gn6ISztr1h5s\n" +
                    "y53dx5GCoU6xAgMBAAECggEALoEAUA9g6BdipRRLeJurCHveK0wWVIVAG15ZqFFFXadguPNHKhoQ\n" +
                    "omFLTCLhdnJeVgMytUEer+HNGcvlBjlJiP6G5fPgxXhgZZWQyQ0O46lwnDb4UzHDxUDcD5RRX29d\n" +
                    "kv3IJTkFfDJ8fA2gVKLyfSR89vuxU0wVmCrJW0dD5Fq3FanFub8dZ5c9WwiA8mPPYMqLoo9fbRUi\n" +
                    "BryKVsYsBjagirI7Gcd9OoGOEUBCMS3yvG8U22aY4kfNJjD44C6H5Ueu3SCO1fQxgEk+tg0INbG2\n" +
                    "QR/evnDfJ45BU0MSHs7DAAW/5T7s35c4rkSPZcSQz2K1QlJMg0dJBrHmljTKAQKBgQDPQo1o4goF\n" +
                    "zy2wfCzQD6AwOuFGdXzODSip1Wpy9On6qFWobWOn/nA8wIJL3iM7TEBlMZTiyQiWpkoILtBtwtXV\n" +
                    "W/YWnAYS61R0m+G+8bIeGtP6SHbL7y8Hc8AYZ3iTRMN0mGCW7joBiXAs99paz7KvnwQPtAp8RGqB\n" +
                    "wennsUhZEQKBgQCpznrKF2fg04e23Rnykt6XccimEIlGlYowfQsNWhvdzuVO/nJdLmhSL0vV9KcX\n" +
                    "t/oW0lErvOK7076uVXZ1s2VosXnQHRvEIvGbli9FI/NzIgGcN/mrEkM4hGr39efX6n9raRiK0K8x\n" +
                    "f3efh9j3rl+duAJ9Sk5AOhiEiNeKMvSboQKBgDjaH6IKhwdK6lSD62LzNlSkghDqv15iG6pigT2h\n" +
                    "UuFP9Zcq0nBNigoQIG2hNHbalrtrW1TOIk34tUwDU2cYT8Gj5SAUklH0PYlzDPxsnhhF8amw4O1V\n" +
                    "4cegh6vqDhgPf7Hm7m/YTw8G3T9oiXvphzFHZDzOfSXTOof3YisM5TSRAoGAbehim7nIl8Hyns6/\n" +
                    "g73Ca33PZIVhhRk3h6j35V2Eme3XiGizN1LZXRCV4ldna7e+HNVmiovIqdAlJIvWj9pc8gWllamx\n" +
                    "liMUBSORVwwtAEFrj2S/qhW5ArTa08ILnTp/3NL6eqcWLlCV6eEw8yefZeN7YQMeYP35bpdwA1h1\n" +
                    "8OECgYBL3qQjmd4zTSlJAyN2UljmVDGwSk11G9WeEdihMbzol9Z3Az0KBZ94Xw4BoH05afHdy3OU\n" +
                    "ZPYcHbzI8k7V+sG6kEn1XB4BrOor8qkaFDgvygCsIDiKlP0oeJryWfkmE7vhviwMrOniV7h/P+iX\n" +
                    "AZqV7sBNAHS6TEON6TllTmvpyg==";
            pubKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiXoaUVx5JuJjhefq/mKLJqkQsyc/BcfQ\n" +
                    "/C7ZYXwHm+nbHt3sl8AakJNp5aCQxtlEQzmenL7zjTjQdCJ9o86ek181jVFQvsfRsYWNi/ejiODQ\n" +
                    "G539XnjwLg8wVP7AlsZUw5BNFKYtz+RiyO41oV7C8tXdJKAN2HQ51WS/cBPCj+WFZ19I1BwIx1J2\n" +
                    "d7/Bo/KK5QD/iD2xp39UFD8rnDmNu9etSfy5we7YbQxwN9vzfOFHHqzyBHyeOlhWyVs8fAh06cjd\n" +
                    "trwyeREWfpzqcM0NQZ05IxP0f2RjVVD8LILzWmnYnN35/6gVX/PqWuyJeYJ+iEs7a9YebMud3ceR\n" +
                    "gqFOsQIDAQAB";
            System.out.println("pubKey---------------------------------------------------");
            System.out.println(pubKey);
            System.out.println("pemKey---------------------------------------------------");
            System.out.println(pemKey);
            System.out.println("end---------------------------------------------------");
            CipherUtil cipherRSAE = CipherUtil.build(RSA, pubKey, "UTF-8");
            CipherUtil cipherRSAD = CipherUtil.build(RSA, pemKey, "UTF-8");
            /*origin = "Hello world RSA! 字符串";
            encrypt = cipherRSAE.encryptString(origin);
            decrypt = cipherRSAD.decryptString(encrypt);
            System.out.println(decrypt + origin.equals(decrypt));*/
            origin = "[\n" +
                    "      {\n" +
                    "        \"patientName\":\"张三\",\n" +
                    "        \"hospitalId\":\"1501\",\n" +
                    "        \"patientId\":\"Q151222002\",\n" +
                    "        \"receiptNo\":\"\",\n" +
                    "        \"hospitalizationTimes\":\"\",\n" +
                    "        \"hospitalizationNo\":\"\",\n" +
                    "        \"invoiceNo\":\"\",\n" +
                    "        \"businessTransaction\":\"\",\n" +
                    "        \"diagnosisNo\":\"\",\n" +
                    "        \"diagnosisName\":\"\",\n" +
                    "        \"diagnosisLevel\":\"\",\n" +
                    "        \"medicalInsuranceDiagnosisCode\":\"\",\n" +
                    "        \"medicalInsuranceDiagnosisName\":\"\",\n" +
                    "        \"treatResult\":\"\",\n" +
                    "        \"diagnosisType\":\"\",\n" +
                    "        \"diagnosisResource \":\"\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"patientName\":\"张三\",\n" +
                    "        \"hospitalId\":\"1501\",\n" +
                    "        \"patientId\":\"Q151222002\",\n" +
                    "        \"receiptNo\":\"\",\n" +
                    "        \"hospitalizationTimes\":\"\",\n" +
                    "        \"hospitalizationNo\":\"\",\n" +
                    "        \"invoiceNo\":\"\",\n" +
                    "        \"businessTransaction\":\"\",\n" +
                    "        \"diagnosisNo\":\"\",\n" +
                    "        \"diagnosisName\":\"\",\n" +
                    "        \"diagnosisLevel\":\"\",\n" +
                    "        \"medicalInsuranceDiagnosisCode\":\"\",\n" +
                    "        \"medicalInsuranceDiagnosisName\":\"\",\n" +
                    "        \"treatResult\":\"\",\n" +
                    "        \"diagnosisType\":\"\",\n" +
                    "        \"diagnosisResource \":\"\"\n" +
                    "      }\n" +
                    "    ]";
            encrypt = cipherRSAE.encryptString(origin);
            decrypt = cipherRSAD.decryptString(encrypt);
            System.out.println("start-------------------------------------------------------------------------------------------");
            System.out.println(encrypt);
            System.out.println("end---------------------------------------------------------------------------------------------");
            System.out.println(decrypt);
            System.out.println(decrypt + origin.equals(decrypt));
        }catch (Exception e){

        }
    }




}


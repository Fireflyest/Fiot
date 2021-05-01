package com.fireflyest.fiot.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {

    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCsHRbjn8d-Wq_8NkEZ77TmF5V4HAmvlzU-4j0uRxd8qNfuTtD4SVM78Bs-QCvLZ5FiDyLsDXSwVXxxd303FThlXbL5iJDRhB7GRFFheno4oI31PEpO_dB7cxWDjoabYaKEVKI7-SBEU6qKgR2R30Cadb0RW0iLcQ1LloAHrmAjhQIDAQAB";

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";

    private static Cipher cipher;

    private RSAUtils(){}

    static {
        try {
            cipher = Cipher.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     */
    private static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getUrlDecoder().decode(publicKey));
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     */
    private static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getUrlDecoder().decode(privateKey));
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }


    /**
     * 公钥加密
     * @param data 加密数据
     * @param publicKey 公钥
     * @return 加密结果
     */
    public static String publicEncrypt(String data, String  publicKey){
        try{
            RSAPublicKey key = getPublicKey(publicKey);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getUrlEncoder().encodeToString(
                    rsaSplitCodec(
                            cipher,
                            Cipher.ENCRYPT_MODE,
                            data.getBytes(CHARSET),
                            key.getModulus().bitLength())
            );
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data 解密数据
     * @param publicKey 公钥
     * @return 解密结果
     */

    public static String publicDecrypt(String data, String  publicKey){
        try{
            RSAPublicKey key = getPublicKey(publicKey);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(rsaSplitCodec(
                    cipher,
                    Cipher.DECRYPT_MODE,
                    Base64.getUrlDecoder().decode(data),
                    key.getModulus().bitLength()),
                    CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data 加密数据
     * @param privateKey 私钥
     * @return 加密结果
     */
    public static String privateEncrypt(String data, String  privateKey){
        try{
            RSAPrivateKey key = getPrivateKey(privateKey);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getUrlEncoder().encodeToString(
                    rsaSplitCodec(
                            cipher,
                            Cipher.ENCRYPT_MODE,
                            data.getBytes(CHARSET),
                            key.getModulus().bitLength())
            );
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data 解密数据
     * @param privateKey 私钥
     * @return 解密结果
     */
    public static String privateDecrypt(String data, String  privateKey){
        try{
            RSAPrivateKey key = getPrivateKey(privateKey);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(rsaSplitCodec(
                    cipher,
                    Cipher.DECRYPT_MODE,
                    Base64.getUrlDecoder().decode(data),
                    key.getModulus().bitLength()),
                    CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    private static byte[] rsaSplitCodec(Cipher cipher, int opMode, byte[] data, int keySize){
        int maxBlock;
        if(opMode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(data.length > offSet){
                if(data.length-offSet > maxBlock){
                    buff = cipher.doFinal(data, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(data, offSet, data.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultData = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }


}

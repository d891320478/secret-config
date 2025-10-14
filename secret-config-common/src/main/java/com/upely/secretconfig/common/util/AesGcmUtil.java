package com.upely.secretconfig.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * 每次加密随机生成偏移量，保证同一明文和seed每次加密结果不同，但是都能解密。
 * 
 * @author dht31261
 * @date 2025年10月12日 20:23:27
 */
public class AesGcmUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int NONCE_LENGTH = 16;

    public static String encrypt(String origin, String seedStr) {
        String nonce = RandomStringUtils.random(NONCE_LENGTH, 0, 127, false, false, null, RandomUtil.getSecureRandom());
        nonce = Hex.encodeHexString(nonce.getBytes());
        try {
            SecretKey key = new SecretKeySpec(Hex.decodeHex(seedStr), "AES");
            GCMParameterSpec params = new GCMParameterSpec(128, Hex.decodeHex(nonce), 0, NONCE_LENGTH);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            byte[] decryptData = cipher.doFinal(origin.getBytes());
            return Hex.encodeHexString(decryptData) + nonce;
        } catch (Exception e) {
            throw new RuntimeException("aes gcm encrypt exception.", e);
        }
    }

    public static byte[] decrypt(String ciphertextStr, String seedStr) {
        String nonce = ciphertextStr.substring(ciphertextStr.length() - NONCE_LENGTH * 2, ciphertextStr.length());
        ciphertextStr = ciphertextStr.substring(0, ciphertextStr.length() - NONCE_LENGTH * 2);
        try {
            SecretKey key = new SecretKeySpec(Hex.decodeHex(seedStr), "AES");
            GCMParameterSpec params = new GCMParameterSpec(128, Hex.decodeHex(nonce), 0, NONCE_LENGTH);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            byte[] ciphertext = Hex.decodeHex(ciphertextStr);
            byte[] decryptData = cipher.doFinal(ciphertext);
            return decryptData;
        } catch (Exception e) {
            throw new RuntimeException("aes gcm decrypt exception.", e);
        }
    }
}
package com.upely.secretconfig.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 获取安全随机数生成器
 * 
 * @author dht31261
 * @date 2025年10月12日 20:23:04
 */
public class RandomUtil {

    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("DRBG");
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom();
        }
    }
}
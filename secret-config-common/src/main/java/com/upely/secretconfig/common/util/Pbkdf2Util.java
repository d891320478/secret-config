package com.upely.secretconfig.common.util;

import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * @author dht31261
 * @date 2025年10月12日 20:27:12
 */
public class Pbkdf2Util {

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String key(String key, byte[] salt, int iterations, int length) {
        try {
            PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), salt, iterations, length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return Hex.encodeHexString(skf.generateSecret(spec).getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("pbdfk2 error.", e);
        }
    }

    public static byte[] xor(List<byte[]> list) {
        if (list == null) {
            return null;
        }
        byte[] rlt = null;
        for (byte[] iter : list) {
            if (rlt == null) {
                rlt = iter;
            } else {
                byte[] key2 = iter;
                byte[] nrlt = new byte[Math.max(rlt.length, key2.length)];
                for (int i = 0; i < nrlt.length; ++i) {
                    if (i < rlt.length) {
                        nrlt[i] ^= rlt[i];
                    }
                    if (i < key2.length) {
                        nrlt[i] ^= key2[i];
                    }
                }
                rlt = nrlt;
            }
        }
        return rlt;
    }
}
package com.usr.secretconfig.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

import com.usr.secretconfig.common.constants.CommonConstants;
import com.usr.secretconfig.common.domain.DbConfigDTO;
import com.usr.secretconfig.common.domain.RedisConfigDTO;
import com.usr.secretconfig.common.util.AesGcmUtil;
import com.usr.secretconfig.common.util.JacksonUtil;
import com.usr.secretconfig.common.util.Pbkdf2Util;
import com.usr.secretconfig.constants.Constants;

/**
 * @author dht31261
 * @date 2025年2月26日 20:03:05
 */
public class DecryptConfigUtil {

    private static final int ROOT_KEY_LENGTH = 32;
    private static final int ROOT_KEY_ITERATIONS = 100000;

    private static final String WORK_KEY_FILE = Constants.BASE_PATH + "/work.ey";
    private static final String CONFIG_DB_FILE = Constants.BASE_PATH + "/config_db";
    private static final String CONFIG_REDIS_FILE = Constants.BASE_PATH + "/config_redis";

    public static DbConfigDTO dbConfig() throws IOException {
        return JacksonUtil.toObject(decryptConfig(CONFIG_DB_FILE), DbConfigDTO.class);
    }

    public static RedisConfigDTO redisConfig() throws IOException {
        return JacksonUtil.toObject(decryptConfig(CONFIG_REDIS_FILE), RedisConfigDTO.class);
    }

    private static String decryptConfig(String file) throws IOException {
        String config = new String(Files.readAllBytes(Paths.get(file)));
        return new String(AesGcmUtil.decrypt(config, workKey()));
    }

    private static String workKey() throws IOException {
        String workKeySecret = new String(Files.readAllBytes(Paths.get(WORK_KEY_FILE)));
        return Hex.encodeHexString(AesGcmUtil.decrypt(workKeySecret, rootKey()));
    }

    public static String rootKey() throws IOException {
        String rtk = new String(Files.readAllBytes(Paths.get(Constants.RTK_FILE)));
        String rts = new String(Files.readAllBytes(Paths.get(Constants.RTS_FILE)));
        String xor = new String(Pbkdf2Util.xor(Arrays.asList(Base64.getDecoder().decode(rtk),
            Base64.getDecoder().decode(CommonConstants.ROOT_KEY_FACTOR))));
        return Pbkdf2Util.key(xor, Base64.getDecoder().decode(rts), ROOT_KEY_ITERATIONS, ROOT_KEY_LENGTH);
    }
}
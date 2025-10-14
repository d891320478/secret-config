package com.upely.secretconfig.client;

import java.util.HashMap;
import java.util.Map;

import com.upely.secretconfig.common.domain.DbConfigDTO;
import com.upely.secretconfig.common.domain.RedisConfigDTO;
import com.upely.secretconfig.common.enums.SecretConfigTypeEnum;
import com.upely.secretconfig.common.util.AesGcmUtil;
import com.upely.secretconfig.common.util.JacksonUtil;

/**
 * @author dht31261
 * @date 2025年10月12日 21:16:31
 */
public final class ReadConfigService {

    private static final String COMMON_SECRET_CONFIG_KEY = "common-secret";

    public static DbConfigDTO dbConfig(String dbName) {
        String workKey = SecretConfigService.workKey(dbName, SecretConfigTypeEnum.DB.getId());
        if (workKey == null) {
            throw new RuntimeException(dbName + " work key not exists.");
        }
        Map<String, String> config = SecretConfigService.secretConfig(dbName, SecretConfigTypeEnum.DB.getId());
        if (config == null) {
            throw new RuntimeException(dbName + " config not exists.");
        }
        String rootKey = SecretConfigService.rootKey();
        workKey = new String(AesGcmUtil.decrypt(workKey, rootKey));
        String configJson = new String(AesGcmUtil.decrypt(config.get(dbName), workKey));
        return JacksonUtil.toObject(configJson, DbConfigDTO.class);
    }

    public static RedisConfigDTO redisConfig(String redisName) {
        String workKey = SecretConfigService.workKey(redisName, SecretConfigTypeEnum.REDIS.getId());
        if (workKey == null) {
            throw new RuntimeException(redisName + " work key not exists.");
        }
        Map<String, String> config = SecretConfigService.secretConfig(redisName, SecretConfigTypeEnum.REDIS.getId());
        if (config == null) {
            throw new RuntimeException(redisName + " config not exists.");
        }
        String rootKey = SecretConfigService.rootKey();
        workKey = new String(AesGcmUtil.decrypt(workKey, rootKey));
        String configJson = new String(AesGcmUtil.decrypt(config.get(redisName), workKey));
        return JacksonUtil.toObject(configJson, RedisConfigDTO.class);
    }

    public static Map<String, String> appConfig(String appName) {
        String rootKey = SecretConfigService.rootKey();
        Map<String, String> config = new HashMap<>();
        String commonWorkKey = SecretConfigService.workKey(COMMON_SECRET_CONFIG_KEY, SecretConfigTypeEnum.APP.getId());
        if (commonWorkKey != null) {
            Map<String, String> mm = SecretConfigService.secretConfig(COMMON_SECRET_CONFIG_KEY,
                    SecretConfigTypeEnum.APP.getId());
            if (mm != null) {
                commonWorkKey = new String(AesGcmUtil.decrypt(commonWorkKey, rootKey));
                for (Map.Entry<String, String> iter : mm.entrySet()) {
                    config.put(iter.getKey(), new String(AesGcmUtil.decrypt(iter.getValue(), commonWorkKey)));
                }
            }
        }
        String workKey = SecretConfigService.workKey(appName, SecretConfigTypeEnum.APP.getId());
        if (workKey == null) {
            return config;
        }
        Map<String, String> mm = SecretConfigService.secretConfig(appName, SecretConfigTypeEnum.APP.getId());
        if (mm != null) {
            workKey = new String(AesGcmUtil.decrypt(workKey, rootKey));
            for (Map.Entry<String, String> iter : mm.entrySet()) {
                config.put(iter.getKey(), new String(AesGcmUtil.decrypt(iter.getValue(), workKey)));
            }
        }
        return config;
    }
}
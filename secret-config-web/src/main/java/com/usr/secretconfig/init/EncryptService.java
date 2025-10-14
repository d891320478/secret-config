package com.usr.secretconfig.init;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.usr.secretconfig.common.domain.DbConfigDTO;
import com.usr.secretconfig.common.domain.RedisConfigDTO;
import com.usr.secretconfig.common.enums.SecretConfigTypeEnum;
import com.usr.secretconfig.common.util.AesGcmUtil;
import com.usr.secretconfig.common.util.JacksonUtil;
import com.usr.secretconfig.common.util.RandomUtil;
import com.usr.secretconfig.constants.Constants;
import com.usr.secretconfig.domain.db.SecretConfigDO;
import com.usr.secretconfig.domain.db.WorkKeyDO;
import com.usr.secretconfig.mapper.SecretConfigMapper;
import com.usr.secretconfig.mapper.WorkKeyMapper;
import com.usr.secretconfig.util.DecryptConfigUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dht31261
 * @date 2025年10月12日 20:19:48
 */
@Slf4j
@Service
public class EncryptService {

    private static final String REDIS_LOCK = "SECRET-CONFIG_com.usr.secretconfig.init.EncryptService_INIT";
    private static final String DB_FILE = Constants.BASE_PATH + "/db.ini";
    private static final String REDIS_FILE = Constants.BASE_PATH + "/redis.ini";
    private static final String APP_FILE = Constants.BASE_PATH + "/app.ini";
    private static final int WORK_KEY_LENGTH = 32;

    @Resource
    private WorkKeyMapper workKeyMapper;
    @Resource
    private SecretConfigMapper secretConfigMapper;
    @Resource
    private RedissonClient redissonClient;

    @PostConstruct
    public void init() throws InvalidFileFormatException, IOException {
        RLock lock = redissonClient.getLock(REDIS_LOCK);
        lock.lock();
        try {
            String rootKey = DecryptConfigUtil.rootKey();

            File dbFile = new File(DB_FILE);
            if (dbFile.exists() && dbFile.isFile()) {
                encryptConfig(rootKey, dbFile, SecretConfigTypeEnum.DB.getId());
                dbFile.delete();
            }

            File redisFile = new File(REDIS_FILE);
            if (redisFile.exists() && redisFile.isFile()) {
                encryptConfig(rootKey, redisFile, SecretConfigTypeEnum.REDIS.getId());
                redisFile.delete();
            }

            File appFile = new File(APP_FILE);
            if (appFile.exists() && appFile.isFile()) {
                encryptConfig(rootKey, appFile, SecretConfigTypeEnum.APP.getId());
                appFile.delete();
            }
        } finally {
            lock.unlock();
        }
    }

    private void encryptConfig(String rootKey, File file, int configType)
        throws InvalidFileFormatException, IOException {
        Config cfg = new Config();
        Ini ini = new Ini();
        ini.setConfig(cfg);
        ini.load(file);
        for (Entry<String, Section> entry : ini.entrySet()) {
            WorkKeyDO workKeyDO = workKeyMapper.selectOne(Wrappers.lambdaQuery(WorkKeyDO.class)
                .eq(WorkKeyDO::getAppName, entry.getKey()).eq(WorkKeyDO::getConfigType, configType));
            String workKey;
            if (workKeyDO != null) {
                workKey = new String(AesGcmUtil.decrypt(workKeyDO.getWorkKey(), rootKey));
            } else {
                workKey = createWorkKey(entry.getKey(), configType, rootKey);
            }
            if (configType == SecretConfigTypeEnum.DB.getId()) {
                encryptDbConfig(workKey, entry.getKey(), entry.getValue());
            } else if (configType == SecretConfigTypeEnum.REDIS.getId()) {
                encryptRedisConfig(workKey, entry.getKey(), entry.getValue());
            } else {
                encryptAppConfig(workKey, entry.getKey(), entry.getValue());
            }
        }
    }

    private void encryptDbConfig(String workKey, String dbName, Section sec)
        throws InvalidFileFormatException, IOException {
        DbConfigDTO dbConfig = new DbConfigDTO();
        dbConfig.setDriver(sec.get("driver"));
        dbConfig.setDb(sec.get("db"));
        dbConfig.setSchema(sec.get("schema"));
        dbConfig.setUser(sec.get("user"));
        dbConfig.setPass(sec.get("pass"));
        dbConfig.setAddress(sec.get("address"));
        String configValue = AesGcmUtil.encrypt(JacksonUtil.toJson(dbConfig), workKey);
        saveConfig(dbName, dbName, configValue, SecretConfigTypeEnum.DB.getId());
    }

    private void encryptRedisConfig(String workKey, String redisName, Section sec)
        throws InvalidFileFormatException, IOException {
        RedisConfigDTO redisConfig = new RedisConfigDTO();
        redisConfig.setAddress(sec.get("address"));
        redisConfig.setPass(sec.get("pass"));
        String configValue = AesGcmUtil.encrypt(JacksonUtil.toJson(redisConfig), workKey);
        saveConfig(redisName, redisName, configValue, SecretConfigTypeEnum.REDIS.getId());
    }

    private void encryptAppConfig(String workKey, String appName, Section sec) {
        for (Entry<String, String> entry : sec.entrySet()) {
            saveConfig(appName, entry.getKey(), AesGcmUtil.encrypt(entry.getValue(), workKey),
                SecretConfigTypeEnum.APP.getId());
        }
    }

    private void saveConfig(String appName, String configKey, String configValue, int configType) {
        SecretConfigDO scdo = secretConfigMapper
            .selectOne(Wrappers.lambdaQuery(SecretConfigDO.class).eq(SecretConfigDO::getAppName, appName)
                .eq(SecretConfigDO::getConfigKey, configKey).eq(SecretConfigDO::getConfigType, configType));
        if (scdo == null) {
            scdo = new SecretConfigDO();
            scdo.setAppName(appName);
            scdo.setConfigKey(configKey);
            scdo.setConfigValue(configValue);
            scdo.setConfigType(configType);
            secretConfigMapper.insert(scdo);
        } else {
            SecretConfigDO upd = new SecretConfigDO();
            upd.setId(scdo.getId());
            upd.setConfigValue(configValue);
            secretConfigMapper.updateById(upd);
        }
    }

    private String createWorkKey(String appName, int configType, String rootKey) {
        String workKey = Hex.encodeHexString(RandomStringUtils
            .random(WORK_KEY_LENGTH, 0, 127, false, false, null, RandomUtil.getSecureRandom()).getBytes());
        WorkKeyDO workKeyDO = new WorkKeyDO();
        workKeyDO.setAppName(appName);
        workKeyDO.setConfigType(configType);
        workKeyDO.setWorkKey(AesGcmUtil.encrypt(workKey, rootKey));
        workKeyMapper.insert(workKeyDO);
        return workKey;
    }
}
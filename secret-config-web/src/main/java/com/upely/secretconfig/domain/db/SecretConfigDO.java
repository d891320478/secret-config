package com.upely.secretconfig.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dht31261
 * @date 2025年2月27日 00:20:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("secret_config")
public class SecretConfigDO extends SuperDbDO {

    public static final String FIELD_APP_NAME = "app_name";
    public static final String FIELD_CONFIG_KEY = "config_key";
    public static final String FIELD_CONFIG_VALUE = "config_value";
    public static final String FIELD_CONFIG_TYPE = "config_type";

    private String appName;
    private String configKey;
    private String configValue;
    /**
     * @see com.upely.secretconfig.common.enums.SecretConfigTypeEnum
     */
    private Integer configType;
}
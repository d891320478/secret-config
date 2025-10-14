package com.usr.secretconfig.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dht31261
 * @date 2025年10月12日 20:16:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_key")
public class WorkKeyDO extends SuperDbDO {

    public static final String FIELD_APP_NAME = "app_name";
    public static final String FIELD_WORK_KEY = "work_key";
    public static final String FIELD_CONFIG_TYPE = "config_type";

    private String appName;
    private String workKey;
    /**
     * @see com.usr.secretconfig.domain.enums.SecretConfigTypeEnum
     */
    private Integer configType;
}
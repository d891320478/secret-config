package com.upely.secretconfig.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dht31261
 * @date 2025年10月12日 20:17:03
 */
@Getter
@AllArgsConstructor
public enum SecretConfigTypeEnum {
    DB(1, "数据库配置"),
    REDIS(2, "redis配置"),
    APP(3, "应用配置");

    private int id;
    private String desc;

    public static SecretConfigTypeEnum getById(int id) {
        for (SecretConfigTypeEnum iter : values()) {
            if (iter.id == id) {
                return iter;
            }
        }
        return null;
    }
}
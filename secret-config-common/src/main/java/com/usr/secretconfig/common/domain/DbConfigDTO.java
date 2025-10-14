package com.usr.secretconfig.common.domain;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * @author dht31261
 * @date 2025年2月27日 16:00:55
 */
@Data
public class DbConfigDTO {

    private String driver;
    private String db;
    private String schema;
    private String user;
    private String pass;
    private String address;
    private String jdbcParam;

    public String jdbcUrl() {
        return "jdbc:" + driver + "://" + address + "/" + (StringUtils.isBlank(db) ? schema : db)
            + (StringUtils.isNotBlank(jdbcParam) ? jdbcParam
                : "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10&characterEncoding=UTF8&allowMultiQueries=true");
    }

    public String jdbcUrlWithParam(String param) {
        return "jdbc:" + driver + "://" + address + "/" + schema + "?" + param;
    }
}
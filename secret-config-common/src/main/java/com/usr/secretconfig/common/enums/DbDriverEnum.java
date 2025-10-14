package com.usr.secretconfig.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dht31261
 * @date 2025年2月26日 20:42:16
 */
@Getter
@AllArgsConstructor
public enum DbDriverEnum {
    MYSQL("mysql", "com.mysql.cj.jdbc.Driver"),
    DM("dm", "dm.jdbc.driver.DmDriver"),
    KINGBASE8("kingbase8", "com.kingbase8.Driver"),
    POSTGRESQL("postgresql", "org.postgresql.Driver"),
    OPEN_GAUSS("opengauss", "org.opengauss.Driver"),
    GAUSS_DB("gaussdb", "org.opengauss.Driver"),
    SQLITE("sqlite", "org.sqlite.JDBC"),
    ORACLE("oracle", "oracle.jdbc.OracleDriver"),
    GOLDEN_DB("goldendb", "com.goldendb.jdbc.Driver");

    private String driver;
    private String driverClass;

    public static DbDriverEnum getByDriver(String driver) {
        if (driver == null)
            return POSTGRESQL;
        for (DbDriverEnum obj : values()) {
            if (obj.getDriver().equals(driver.trim())) {
                return obj;
            }
        }
        return POSTGRESQL;
    }
}
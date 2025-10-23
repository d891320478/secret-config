package com.upely.secretconfig.constants;

import java.util.Optional;

/**
 * @author dht31261
 * @date 2025年10月12日 20:13:05
 */
public class Constants {

    public static final String BASE_PATH =
        Optional.ofNullable(System.getenv("CONFIG_ROOT_PATH")).orElse("/data/config");
    public static final String RTK_FILE = BASE_PATH + "/rtk";
    public static final String RTS_FILE = BASE_PATH + "/rts";
}
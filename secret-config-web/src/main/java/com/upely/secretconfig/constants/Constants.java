package com.upely.secretconfig.constants;

/**
 * @author dht31261
 * @date 2025年10月12日 20:13:05
 */
public class Constants {

    public static final String BASE_PATH = System.getProperty("config.root.path", "/data/config");
    public static final String RTK_FILE = BASE_PATH + "/rtk";
    public static final String RTS_FILE = BASE_PATH + "/rts";
}
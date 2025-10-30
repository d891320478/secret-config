package com.upely.secretconfig.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2Util {

    private static final Argon2 ARGON2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public static String argon2Sign(String password) {
        try {
            return ARGON2.hash(10, 32768, 1, password.toCharArray());
        } finally {
            ARGON2.wipeArray(password.toCharArray());
        }
    }

    public static boolean argon2Verify(String sign, String password) {
        try {
            return ARGON2.verify(sign, password.toCharArray());
        } finally {
            ARGON2.wipeArray(password.toCharArray());
        }
    }
}

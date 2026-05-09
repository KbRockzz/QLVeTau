package com.trainstation.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final String PREFIX = "pbkdf2";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {}

    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null) return null;
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);
            byte[] hash = pbkdf2(plainTextPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            return PREFIX + "$" + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot hash password", e);
        }
    }

    public static boolean verifyPassword(String plainTextPassword, String storedPassword) {
        if (plainTextPassword == null || storedPassword == null) return false;
        try {
            if (!storedPassword.startsWith(PREFIX + "$")) {
                return MessageDigest.isEqual(
                        storedPassword.getBytes(StandardCharsets.UTF_8),
                        plainTextPassword.getBytes(StandardCharsets.UTF_8)
                );
            }
            String[] parts = storedPassword.split("\\$");
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
            byte[] providedHash = pbkdf2(plainTextPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
            return MessageDigest.isEqual(expectedHash, providedHash);
        } catch (Exception e) {
            return false;
        }
    }

    public static String hashIfNeeded(String password) {
        if (password == null) return null;
        if (password.startsWith(PREFIX + "$")) return password;
        return hashPassword(password);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }
}

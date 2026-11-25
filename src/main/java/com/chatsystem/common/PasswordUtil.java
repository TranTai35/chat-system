package com.chatsystem.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Băm và kiểm tra mật khẩu. Dùng SHA-256 dạng hex chữ thường, giống hàm
 * {@code SHA2(mật_khẩu, 256)} trong {@code sample_data.sql}, nên tài khoản mẫu đăng nhập được ngay.
 */
public final class PasswordUtil {

    private static final String RANDOM_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM không có SHA-256", e);
        }
    }

    public static boolean matches(String rawPassword, String storedHash) {
        return storedHash != null && hash(rawPassword).equalsIgnoreCase(storedHash);
    }

    /** Mật khẩu ngẫu nhiên (bỏ các ký tự dễ nhầm như 0/O, 1/l/I) cho chức năng Quên mật khẩu. */
    public static String randomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_CHARS.charAt(RANDOM.nextInt(RANDOM_CHARS.length())));
        }
        return sb.toString();
    }
}

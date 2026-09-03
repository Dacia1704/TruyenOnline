package com.dacia1704.truyenonline.shared.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class HashUtil {

    private HashUtil() {}

    /**
     * Băm chuỗi bằng SHA-256 và trả về chuỗi hex.
     *
     * @param input Chuỗi cần băm
     * @return Chuỗi SHA-256 dạng hex
     */
    public static String sha256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", ex);
        }
    }
}

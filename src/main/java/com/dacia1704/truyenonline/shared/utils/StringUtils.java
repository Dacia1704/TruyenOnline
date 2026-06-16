package com.dacia1704.truyenonline.shared.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * Chuyển đổi chuỗi có dấu thành không dấu
     */
    public static String removeAccent(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String temp = input.replace("Đ", "D").replace("đ", "d");
        String normalized = Normalizer.normalize(temp, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}
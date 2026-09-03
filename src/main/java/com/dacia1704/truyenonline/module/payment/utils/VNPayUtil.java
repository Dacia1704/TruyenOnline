package com.dacia1704.truyenonline.module.payment.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // ----------------------------------------------------------------
    // HMAC-SHA512 — thuật toán VNPay yêu cầu - VNPay yêu cầu cac request đều phải có vpn_SecureHash
    // ----------------------------------------------------------------
    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tính HMAC-SHA512", e);
        }
    }

    // ----------------------------------------------------------------
    // Build chuỗi hash data từ Map params (sort theo key A-Z)
    // VNPay yêu cầu: sort key, encode value, nối bằng '&'
    // ----------------------------------------------------------------
    public static String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                if (!hashData.isEmpty()) {
                    hashData.append('&');
                }
                hashData.append(fieldName)
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }
        return hashData.toString();
    }

    // ----------------------------------------------------------------
    // Build query string cho redirect URL (sort key + encode)
    // ----------------------------------------------------------------
    public static String buildQueryString(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                if (!query.isEmpty()) {
                    query.append('&');
                }
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }
        return query.toString();
    }

    // ----------------------------------------------------------------
    // Format thời gian theo chuẩn VNPay: yyyyMMddHHmmss
    // ----------------------------------------------------------------
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    // ----------------------------------------------------------------
    // Tạo vnp_TxnRef duy nhất — VNPay không cho phép trùng
    // Format: timestamp_random (ví dụ: 20240615143022_A3F9)
    // ----------------------------------------------------------------
    public static String generateTxnRef() {
        String timestamp = formatDate(LocalDateTime.now());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return timestamp + "_" + random;
    }

    // ----------------------------------------------------------------
    // Verify secure hash từ VNPay callback
    // ----------------------------------------------------------------
    public static boolean verifySecureHash(
            String hashSecret, Map<String, String> params, String receivedHash) {
        String computedHash = hmacSHA512(hashSecret, buildHashData(params));
        return computedHash.equalsIgnoreCase(receivedHash);
    }

    // ----------------------------------------------------------------
    // Convert amount VND → VNPay amount (nhân 100)
    // VNPay quy định: 100,000đ → gửi 10000000
    // ----------------------------------------------------------------
    public static long toVNPayAmount(long amountVND) {
        return amountVND * 100L;
    }

    public static long fromVNPayAmount(long vnpayAmount) {
        return vnpayAmount / 100L;
    }
}

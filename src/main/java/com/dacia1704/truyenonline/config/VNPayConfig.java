package com.dacia1704.truyenonline.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig { // Dùng để đọc cấu hình VNPay từ application.yml
    String tmnCode;
    String hashSecret;
    String payUrl;
    String returnUrl;
    String ipnUrl;
    String version;
    String command;
    String orderType;
    String locale;
    String currencyCode;
}

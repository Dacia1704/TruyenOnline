package com.dacia1704.truyenonline.module.payment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCallbackResult {
    String txnRef;
    boolean success;
    String responseCode;
    boolean validSignature;
    String message;
}

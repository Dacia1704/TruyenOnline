package com.dacia1704.truyenonline.module.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpgradeToUploaderRequest {

    @NotBlank(message = "Refresh token không được để trống")
    String refreshToken;
}

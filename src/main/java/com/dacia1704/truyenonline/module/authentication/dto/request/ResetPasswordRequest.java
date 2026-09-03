package com.dacia1704.truyenonline.module.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "TOKEN_REQUIRED")
    private String token;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, max = 100, message = "PASSWORD_INVALID")
    private String newPassword;
}

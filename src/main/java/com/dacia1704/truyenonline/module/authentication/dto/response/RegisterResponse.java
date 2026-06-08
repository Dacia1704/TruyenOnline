package com.dacia1704.truyenonline.module.authentication.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterResponse {
    String id;
    String email;
    String username;
}
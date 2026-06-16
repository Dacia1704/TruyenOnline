package com.dacia1704.truyenonline.module.authentication.dto.response;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String id;
    String email;
    String username;
    String avatarUrl;
    List<String> roles;
    List<String> permissions;
    String accessToken;
    String refreshToken;
}

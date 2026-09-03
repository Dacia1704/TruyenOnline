package com.dacia1704.truyenonline.module.user.dto.response;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String email;
    String username;
    String avatarUrl;
    boolean isActive;
    Set<RoleResponse> roles = new HashSet<>();
}

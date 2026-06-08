package com.dacia1704.truyenonline.module.user.dto.response;

import com.dacia1704.truyenonline.module.user.entity.AuthProvider;
import com.dacia1704.truyenonline.module.user.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

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
    Set<Role> roles = new HashSet<>();
}

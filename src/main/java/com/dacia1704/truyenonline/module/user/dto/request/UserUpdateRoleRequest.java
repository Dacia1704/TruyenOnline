package com.dacia1704.truyenonline.module.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRoleRequest {
    @NotEmpty(message = "Danh sách role không được để trống")
    List<Integer> roles;
}

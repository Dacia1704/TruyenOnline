package com.dacia1704.truyenonline.module.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 4, max = 50, message = "Tên đăng nhập phải từ 4 đến 50 ký tự")
    String username;

    MultipartFile avatar;

    @Email(message = "Email không đúng định dạng")
    String email;
}

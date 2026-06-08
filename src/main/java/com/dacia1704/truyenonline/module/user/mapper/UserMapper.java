package com.dacia1704.truyenonline.module.user.mapper;

import com.dacia1704.truyenonline.module.authentication.dto.request.RegisterRequest;
import com.dacia1704.truyenonline.module.authentication.dto.response.RegisterResponse;
import com.dacia1704.truyenonline.module.user.dto.request.UserCreateRequest;
import com.dacia1704.truyenonline.module.user.dto.request.UserUpdateRequest;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // 1. Chặn MapStruct đụng vào mật khẩu và Role khi tạo/đăng ký
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(RegisterRequest request);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreateRequest request);

    // 2. Chặn đụng vào mật khẩu khi update (Update mật khẩu phải có API riêng)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    // 3. Map từ User sang UserResponse
    UserResponse toUserResponse(User user);

    RegisterResponse toRegisterResponse(User user);

}

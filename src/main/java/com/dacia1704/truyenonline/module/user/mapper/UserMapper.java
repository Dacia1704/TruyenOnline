package com.dacia1704.truyenonline.module.user.mapper;

import com.dacia1704.truyenonline.module.authentication.dto.request.RegisterRequest;
import com.dacia1704.truyenonline.module.authentication.dto.response.RegisterResponse;
import com.dacia1704.truyenonline.module.user.dto.request.UserCreateRequest;
import com.dacia1704.truyenonline.module.user.dto.request.UserUpdateRequest;
import com.dacia1704.truyenonline.module.user.dto.response.RoleResponse;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(RegisterRequest request);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserResponse toUserResponse(User user);

    RegisterResponse toRegisterResponse(User user);

    RoleResponse toRoleResponse(Role role);
}

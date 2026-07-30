package com.dacia1704.truyenonline.module.user.controller;

import com.dacia1704.truyenonline.module.authentication.dto.request.LoginRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryBanRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryUnbanRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.user.dto.request.*;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.dto.response.UserUpgradeToUploaderResponse;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    // User - Uploader
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponse> updateMyInfo(@ModelAttribute @Valid UserUpdateRequest request) throws IOException {
        return ApiResponse.success(userService.updateMyInfo(request));
    }

    @GetMapping("/me")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.success(userService.getMyInfo());
    }

    @PatchMapping("/me/upgrade-to-uploader")
    ApiResponse<UserUpgradeToUploaderResponse> upgradeToUploader(
            HttpServletRequest servletRequest,
            @RequestHeader(value = "Device-Id", required = false) String deviceId
    ) {
        return ApiResponse.success(userService.upgradeToUploader(servletRequest, deviceId));
    }

    // Admin
    @GetMapping("")
    @PreAuthorize("hasAuthority('user:read')")
    public ApiResponse<PageResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserResponse> result = userService.getAll(page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.success(userService.getUserById(userId));
    }

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> banUser(
            @PathVariable("id") String id, @RequestBody @Valid UserBanRequest request) throws IOException {
        UserResponse result = userService.banUser(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> unbanUser(
            @PathVariable("id") String id, @RequestBody @Valid UserUnbanRequest request) throws IOException {
        UserResponse result = userService.unbanUser(id, request);
        return ApiResponse.success(result);
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:manage_roles')")
    ApiResponse<UserResponse> updateRoles(
            @PathVariable("userId") String userId,
            @RequestBody @Valid UserUpdateRoleRequest request) {
        return ApiResponse.success(userService.updateRoles(userId, request));
    }
}

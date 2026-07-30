package com.dacia1704.truyenonline.module.user.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.service.AuthenticationService;
import com.dacia1704.truyenonline.module.authentication.service.RefreshTokenService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.user.dto.request.*;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.dto.response.UserUpgradeToUploaderResponse;
import com.dacia1704.truyenonline.module.user.entity.Permission;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.RoleName;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import com.dacia1704.truyenonline.module.user.repository.RoleRepository;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ModerationActionService moderationActionService;
    AuditLogService auditLogService;
    ObjectMapper objectMapper;
    CloudinaryService cloudinaryService;
    MediaFileService mediaFileService;
    AuthenticationService authenticationService;
    RefreshTokenService refreshTokenService;

    String folderPath = "truyenonline/users/%s";

    public UserResponse createUser(UserCreateRequest request) throws JsonProcessingException {
        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        List<Role> defaultRoles = roleRepository.findByIsDefaultTrue();
        user.setRoles(new HashSet<>(defaultRoles));
        user = userRepository.save(user);
        auditLogService.log(AuditAction.CREATE, AuditObjectType.USER, user.getId(), null, user, null);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        User oldValue = objectMapper.convertValue(user, User.class);
        userMapper.updateUser(user, request);

        if (request.getAvatar()!= null && !request.getAvatar().isEmpty()) {
            String path = String.format(folderPath, user.getId());
            CloudinaryUploadResult fileUploadResult = cloudinaryService.uploadImage(request.getAvatar(), path);
            mediaFileService.createMediaFile(fileUploadResult);
            user.setAvatarUrl(fileUploadResult.getSecureUrl());
        }

        user = userRepository.save(user);
        auditLogService.log(AuditAction.UPDATE, AuditObjectType.USER, user.getId(), oldValue, user, null);
        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        User user = getCurrentUser();
        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserById(String userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateMyInfo(UserUpdateRequest request) throws IOException {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return updateUser(userId, request);
    }

    public UserUpgradeToUploaderResponse upgradeToUploader(HttpServletRequest servletRequest, String deviceId) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        User oldValue = objectMapper.convertValue(user, User.class);
        Role uploaderRole = roleRepository.findByName(RoleName.UPLOADER.toString()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.getRoles().add(uploaderRole);
        user = userRepository.save(user);
        RefreshTokenResponse refreshTokenResponse = refreshTokenService.rotateRefreshToken(servletRequest, deviceId, user);
        auditLogService.log(AuditAction.UPDATE, AuditObjectType.USER, user.getId(), oldValue, user, null);
        return UserUpgradeToUploaderResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .accessToken(refreshTokenResponse.getAccessToken())
                .refreshToken(refreshTokenResponse.getRefreshToken())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .permissions(
                        user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(Permission::getName)
                                .distinct()
                                .toList())
                .build();
    }

    public void deleteUser(String userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);
        auditLogService.log(AuditAction.DELETE, AuditObjectType.USER, user.getId(), user, null, null);
    }

    public UserResponse banUser(String userId, UserBanRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.isBanned()) throw new AppException(ErrorCode.USER_ALREADY_BANNED);

        User oldValue = objectMapper.convertValue(user, User.class);
        user.setBanned(true);
        user = userRepository.save(user);
        refreshTokenService.revokedRefreshTokenByUser(userId);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(userId)
                        .objectType(ModerationObjectType.USER)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build()
        );
        auditLogService.log(AuditAction.BAN, AuditObjectType.USER,userId,oldValue, user, request.getReason());
        return userMapper.toUserResponse(user);
    }

    public UserResponse unbanUser(String userId, UserUnbanRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!user.isBanned()) throw new AppException(ErrorCode.USER_NOT_GET_BANNED);
        User oldValue = objectMapper.convertValue(user, User.class);
        user.setBanned(false);
        user = userRepository.save(user);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(userId)
                        .objectType(ModerationObjectType.USER)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build()
        );
        auditLogService.log(AuditAction.UNBAN, AuditObjectType.USER,userId,oldValue,user, request.getReason());
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public PageResponse<UserResponse> getAll(int page, int size) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses =
                userPage.getContent().stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(userPage.getSize())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .data(userResponses)
                .build();
    }

    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse updateRoles(String userId, UserUpdateRoleRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        User oldValue = objectMapper.convertValue(user, User.class);
        List<Role> roles = roleRepository.findAllById(request.getRoles());
        if (roles.size() != request.getRoles().size()) throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        user.setRoles(new HashSet<>(roles));
        user = userRepository.save(user);
        auditLogService.log(AuditAction.UPDATE, AuditObjectType.USER, user.getId(), oldValue, user, null);
        return userMapper.toUserResponse(user);
    }

    public User getCurrentUser() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}

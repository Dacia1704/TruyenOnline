package com.dacia1704.truyenonline.module.user.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.AuditMapper;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.entity.UserSocialAccount;
import com.dacia1704.truyenonline.module.authentication.service.RefreshTokenService;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.user.dto.request.*;
import com.dacia1704.truyenonline.module.user.dto.response.RoleResponse;
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
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    CloudinaryService cloudinaryService;
    MediaFileService mediaFileService;
    RefreshTokenService refreshTokenService;

    public UserResponse createUser(UserCreateRequest request) {
        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        List<Role> defaultRoles = roleRepository.findByIsDefaultTrue();
        user.setRoles(new HashSet<>(defaultRoles));
        user = userRepository.save(user);
        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.USER,
                user.getId(),
                null,
                buildAuditLogUser(user),
                null);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var oldValue = buildAuditLogUser(user);
        userMapper.updateUser(user, request);

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String folderPath = "truyenonline/users/%s";
            String path = String.format(folderPath, user.getId());
            CloudinaryUploadResult fileUploadResult =
                    cloudinaryService.uploadImage(request.getAvatar(), path);
            mediaFileService.createMediaFile(fileUploadResult);
            user.setAvatarUrl(fileUploadResult.getSecureUrl());
        }

        user = userRepository.save(user);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.USER,
                user.getId(),
                oldValue,
                buildAuditLogUser(user),
                null);
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

    public UserResponse updateMyInfo(UserUpdateRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return updateUser(userId, request);
    }

    public UserUpgradeToUploaderResponse upgradeToUploader(
            HttpServletRequest servletRequest, String deviceId) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var oldValue = buildAuditLogUser(user);
        Role uploaderRole =
                roleRepository
                        .findByName(RoleName.UPLOADER.toString())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.getRoles().add(uploaderRole);
        user = userRepository.save(user);
        RefreshTokenResponse refreshTokenResponse =
                refreshTokenService.rotateRefreshToken(servletRequest, deviceId, user);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.USER,
                user.getId(),
                oldValue,
                buildAuditLogUser(user),
                null);
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
        auditLogService.log(
                AuditAction.DELETE,
                AuditObjectType.USER,
                user.getId(),
                buildAuditLogUser(user),
                null,
                null);
    }

    public UserResponse banUser(String userId, UserBanRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (Boolean.TRUE.equals(user.getIsBanned()))
            throw new AppException(ErrorCode.USER_ALREADY_BANNED);

        var oldValue = buildAuditLogUser(user);
        user.setIsBanned(true);
        user = userRepository.save(user);
        refreshTokenService.revokedRefreshTokenByUser(userId);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(userId)
                        .objectType(ModerationObjectType.USER)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build());
        auditLogService.log(
                AuditAction.BAN,
                AuditObjectType.USER,
                userId,
                oldValue,
                buildAuditLogUser(user),
                request.getReason());
        return userMapper.toUserResponse(user);
    }

    public UserResponse unbanUser(String userId, UserUnbanRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (Boolean.FALSE.equals(user.getIsBanned()))
            throw new AppException(ErrorCode.USER_NOT_GET_BANNED);
        var oldValue = buildAuditLogUser(user);
        user.setIsBanned(false);
        user = userRepository.save(user);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(userId)
                        .objectType(ModerationObjectType.USER)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build());
        auditLogService.log(
                AuditAction.UNBAN,
                AuditObjectType.USER,
                userId,
                oldValue,
                buildAuditLogUser(user),
                request.getReason());
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

    public List<RoleResponse> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(userMapper::toRoleResponse).toList();
    }

    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse updateRoles(String userId, UserUpdateRoleRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var oldValue = buildAuditLogUser(user);
        List<Role> roles = roleRepository.findAllById(request.getRoles());
        if (roles.size() != request.getRoles().size())
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        user.setRoles(new HashSet<>(roles));
        user = userRepository.save(user);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.USER,
                user.getId(),
                oldValue,
                buildAuditLogUser(user),
                null);
        return userMapper.toUserResponse(user);
    }

    public User getCurrentUser() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public Map<String, Object> buildAuditLogUser(User user) {
        if (user == null) {
            return Map.of();
        }

        return AuditMapper.of(user)
                .add("id", User::getId)
                .add("email", User::getEmail)
                .add("username", User::getUsername)
                .add(
                        "roles",
                        u ->
                                u.getRoles() == null
                                        ? List.of()
                                        : u.getRoles().stream().map(Role::getName).toList())
                .add(
                        "socialAccounts",
                        u ->
                                u.getSocialAccounts() == null
                                        ? List.of()
                                        : u.getSocialAccounts().stream()
                                                .map(UserSocialAccount::getId)
                                                .toList())
                .add(
                        "currentModeration",
                        u ->
                                u.getCurrentModeration() == null
                                        ? null
                                        : u.getCurrentModeration().getId())
                .build();
    }
}

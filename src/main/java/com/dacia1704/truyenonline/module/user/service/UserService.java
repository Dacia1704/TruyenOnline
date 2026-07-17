package com.dacia1704.truyenonline.module.user.service;

import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterBanRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.user.dto.request.*;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import com.dacia1704.truyenonline.module.user.repository.RoleRepository;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public UserResponse createUser(UserCreateRequest request) {
        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        List<Role> defaultRoles = roleRepository.findByIsDefaultTrue();
        user.setRoles(new HashSet<>(defaultRoles));
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(user, request);
        user = userRepository.save(user);
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

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserResponse banUser(String userId, UserBanRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.isBanned()) {
            throw new AppException(ErrorCode.USER_ALREADY_BANNED);
        }

        String oldValue;
        String newValue;

        try {
            oldValue = objectMapper.writeValueAsString(user);

            user.setBanned(true);

            newValue = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize user", e);
        }

        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(userId)
                        .objectType(ModerationObjectType.USER)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build()
        );

        auditLogService.createAuditLog(
                AuditLogCreateRequest.builder()
                        .action(AuditAction.BAN)
                        .objectType(AuditObjectType.USER)
                        .objectId(userId)
                        .description(request.getReason())
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .build()
        );

        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public UserResponse unbanUser(String userId, UserUnbanRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.isBanned()) {
            throw new AppException(ErrorCode.USER_NOT_GET_BANNED);
        }

        String oldValue;
        String newValue;

        try {
            oldValue = objectMapper.writeValueAsString(user);

            user.setBanned(false);

            newValue = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize user", e);
        }

        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(userId)
                        .objectType(ModerationObjectType.USER)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build()
        );

        auditLogService.createAuditLog(
                AuditLogCreateRequest.builder()
                        .action(AuditAction.UNBAN)
                        .objectType(AuditObjectType.USER)
                        .objectId(userId)
                        .description(request.getReason())
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .build()
        );

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

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Role> roles = roleRepository.findAllById(request.getRoles());
        if (roles.size() != request.getRoles().size()) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        user.setRoles(new HashSet<>(roles));

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public User getCurrentUser() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}

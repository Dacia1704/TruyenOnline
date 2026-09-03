package com.dacia1704.truyenonline.module.authentication.service;

import com.dacia1704.truyenonline.config.PasswordEncoderConfig;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.authentication.dto.request.*;
import com.dacia1704.truyenonline.module.authentication.dto.response.IntrospectResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.LoginResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RegisterResponse;
import com.dacia1704.truyenonline.module.authentication.entity.PasswordResetToken;
import com.dacia1704.truyenonline.module.authentication.repository.PasswordResetTokenRepository;
import com.dacia1704.truyenonline.module.interaction.service.ReadingHistoryService;
import com.dacia1704.truyenonline.module.user.entity.*;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import com.dacia1704.truyenonline.module.user.repository.RoleRepository;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.service.EmailService;
import com.dacia1704.truyenonline.shared.service.EmailTemplateService;
import com.dacia1704.truyenonline.shared.utils.HashUtil;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationService {
    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${jwt.refresh-expiration}")
    long refreshExpiration;

    final UserRepository userRepository;
    final UserMapper userMapper;
    final RoleRepository roleRepository;
    final JwtTokenService jwtTokenService;
    final PasswordEncoderConfig passwordEncoderConfig;
    final ReadingHistoryService readingHistoryService;
    final PasswordResetTokenRepository passwordResetTokenRepository;
    final RefreshTokenService refreshTokenService;

    final EmailTemplateService emailTemplateService;
    final EmailService emailService;
    final AuditLogService auditLogService;
    final UserService userService;

    public LoginResponse authenticate(
            HttpServletRequest servletRequest,
            String deviceId,
            String sessionId,
            LoginRequest request) {
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!user.isActive()) throw new AppException(ErrorCode.NOT_ACTIVE);
        if (Boolean.TRUE.equals(user.getIsBanned()))
            throw new AppException(ErrorCode.USER_ALREADY_BANNED);
        boolean authenticated =
                passwordEncoderConfig
                        .passwordEncoder()
                        .matches(request.getPassword(), user.getPasswordHash());
        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);
        //        readingHistoryService.mergeSessionHistory(user.getId(), sessionId);
        auditLogService.log(
                AuditAction.LOGIN,
                AuditObjectType.USER,
                user.getId(),
                null,
                userService.buildAuditLogUser(user),
                null,
                user.getId());
        return buildLoginResponse(servletRequest, deviceId, user);
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        return IntrospectResponse.builder()
                .valid(jwtTokenService.isTokenValid(request.getToken()))
                .build();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        SignedJWT signedJWT = jwtTokenService.verifyToken(request.getRefreshToken());
        try {
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            User user =
                    userRepository
                            .findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            if (!user.isActive()) throw new AppException(ErrorCode.NOT_ACTIVE);
            if (Boolean.TRUE.equals(user.getIsBanned()))
                throw new AppException(ErrorCode.USER_ALREADY_BANNED);

            String newAccessToken = jwtTokenService.generateAccessToken(user);
            String newRefreshToken =
                    refreshTokenService.renewRefreshToken(request.getRefreshToken(), user);

            return RefreshTokenResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();

        } catch (ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())
                || userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoderConfig.passwordEncoder().encode(request.getPassword()));
        List<Role> defaultRoles = roleRepository.findByIsDefaultTrue();
        if (Boolean.TRUE.equals(request.getIsUploader())) {
            Role uploaderRole =
                    roleRepository
                            .findByName(RoleName.UPLOADER.toString())
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            defaultRoles.add(uploaderRole);
        }
        user.setRoles(new HashSet<>(defaultRoles));
        user = userRepository.save(user);

        auditLogService.log(
                AuditAction.REGISTER,
                AuditObjectType.USER,
                user.getId(),
                null,
                userService.buildAuditLogUser(user),
                null);

        return userMapper.toRegisterResponse(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        // ko báo lỗi tránh hack
        if (user == null) return;

        passwordResetTokenRepository.deleteAllByUser_Id(user.getId());

        String rawToken = UUID.randomUUID().toString();

        String tokenHash = HashUtil.sha256(rawToken);

        PasswordResetToken token =
                PasswordResetToken.builder()
                        .user(user)
                        .tokenHash(tokenHash)
                        .expiredAt(LocalDateTime.now().plusMinutes(15))
                        .build();

        passwordResetTokenRepository.save(token);
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        Map<String, Object> variables =
                Map.of("username", user.getUsername(), "resetLink", resetLink);
        String html = emailTemplateService.render("email/forgot-password", variables);
        emailService.sendHtmlEmail(user.getEmail(), "Đặt lại mật khẩu", html);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = HashUtil.sha256(request.getToken());
        PasswordResetToken token =
                passwordResetTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
        if (token.getUsedAt() != null) throw new AppException(ErrorCode.INVALID_TOKEN);
        if (token.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new AppException(ErrorCode.INVALID_TOKEN);
        User user = token.getUser();
        var oldValue = userService.buildAuditLogUser(user);
        user.setPasswordHash(
                passwordEncoderConfig.passwordEncoder().encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);
        refreshTokenService.revokedRefreshTokenByUser(user.getId());
        auditLogService.log(
                AuditAction.RESET_PASSWORD,
                AuditObjectType.USER,
                user.getId(),
                oldValue,
                userService.buildAuditLogUser(user),
                null,
                user.getId());
    }

    public void logout(LogoutRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        refreshTokenService.revokedRefreshToken(request.getRefreshToken());
        auditLogService.log(
                AuditAction.LOGOUT,
                AuditObjectType.USER,
                user.getId(),
                null,
                userService.buildAuditLogUser(user),
                null);
    }

    public void logoutAll() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        refreshTokenService.revokedRefreshTokenByUser(userId);
        auditLogService.log(
                AuditAction.LOGOUT_ALL,
                AuditObjectType.USER,
                user.getId(),
                null,
                userService.buildAuditLogUser(user),
                null);
    }

    public LoginResponse buildLoginResponse(
            HttpServletRequest servletRequest, String deviceId, User user) {
        var accessToken = jwtTokenService.generateAccessToken(user);
        var rawRefreshToken = jwtTokenService.generateRefreshToken(user);
        refreshTokenService.createRefreshToken(servletRequest, deviceId, rawRefreshToken, user);
        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .permissions(
                        user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(Permission::getName)
                                .distinct()
                                .toList())
                .build();
    }
}

package com.dacia1704.truyenonline.module.authentication.service;

import com.dacia1704.truyenonline.config.PasswordEncoderConfig;
import com.dacia1704.truyenonline.module.authentication.dto.GoogleUserInfo;
import com.dacia1704.truyenonline.module.authentication.dto.request.*;
import com.dacia1704.truyenonline.module.authentication.dto.response.IntrospectResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.LoginResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RegisterResponse;
import com.dacia1704.truyenonline.module.authentication.entity.AuthProvider;
import com.dacia1704.truyenonline.module.authentication.entity.PasswordResetToken;
import com.dacia1704.truyenonline.module.authentication.entity.RefreshToken;
import com.dacia1704.truyenonline.module.authentication.entity.UserSocialAccount;
import com.dacia1704.truyenonline.module.authentication.repository.PasswordResetTokenRepository;
import com.dacia1704.truyenonline.module.authentication.repository.RefreshTokenRepository;
import com.dacia1704.truyenonline.module.interaction.service.ReadingHistoryService;
import com.dacia1704.truyenonline.module.user.entity.*;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import com.dacia1704.truyenonline.module.user.repository.RoleRepository;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.module.user.repository.UserSocialAccountRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.service.EmailService;
import com.dacia1704.truyenonline.shared.service.EmailTemplateService;
import com.dacia1704.truyenonline.shared.utils.HashUtil;
import com.dacia1704.truyenonline.shared.utils.UserAgentUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationService {
    @Value("${jwt.secret}")
    String secretKey;

    @Value("${jwt.expiration}")
    long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    long refreshExpiration;

    final UserRepository userRepository;
    final UserMapper userMapper;
    final RoleRepository roleRepository;
    final JwtTokenService jwtTokenService;
    final PasswordEncoderConfig passwordEncoderConfig;
    final ReadingHistoryService readingHistoryService;
    final UserSocialAccountRepository userSocialAccountRepository;
    final GoogleTokenVerifier googleTokenVerifier;
    final PasswordResetTokenRepository passwordResetTokenRepository;
    final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    final EmailTemplateService emailTemplateService;
    final EmailService emailService;

    public LoginResponse authenticate(HttpServletRequest servletRequest,String deviceId, String sessionId, LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if(!user.isActive()) throw new AppException(ErrorCode.NOT_ACTIVE);
        if(user.isBanned()) throw new AppException(ErrorCode.USER_ALREADY_BANNED);

        boolean authenticated = passwordEncoderConfig.passwordEncoder().matches(request.getPassword(), user.getPasswordHash());
        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        readingHistoryService.mergeSessionHistory(user.getId(), sessionId);
        return buildLoginResponse(servletRequest,deviceId,user);
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
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            if (!user.isActive()) throw new AppException(ErrorCode.NOT_ACTIVE);
            if (user.isBanned()) throw new AppException(ErrorCode.USER_ALREADY_BANNED);

            RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(HashUtil.sha256(request.getRefreshToken()))
                    .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

            if (!refreshToken.isActive()) throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);

            String newAccessToken = generateAccessToken(user);
            String newRefreshToken = generateRefreshToken(user);
            refreshToken.setLastUsedAt(LocalDateTime.now());
            refreshToken.setTokenHash(HashUtil.sha256(newRefreshToken));
            refreshTokenRepository.save(refreshToken);
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
        if(Boolean.TRUE.equals(request.getIsUploader())) {
            Role uploaderRole = roleRepository.findByName(RoleName.UPLOADER.toString()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            defaultRoles.add(uploaderRole);
        }
        user.setRoles(new HashSet<>(defaultRoles));
        user = userRepository.save(user);

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

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();

        passwordResetTokenRepository.save(token);
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        Map<String, Object> variables = Map.of(
                "username", user.getUsername(),
                "resetLink", resetLink
        );
        String html = emailTemplateService.render("email/forgot-password", variables);
        emailService.sendHtmlEmail(user.getEmail(), "Đặt lại mật khẩu", html);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = HashUtil.sha256(request.getToken());
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(tokenHash).orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
        if (token.getUsedAt() != null) throw new AppException(ErrorCode.INVALID_TOKEN);
        if (token.getExpiredAt().isBefore(LocalDateTime.now())) throw new AppException(ErrorCode.INVALID_TOKEN);
        User user = token.getUser();
        user.setPasswordHash(passwordEncoderConfig.passwordEncoder().encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUser_Id(user.getId());
        refreshTokens.forEach(refreshToken -> refreshToken.setRevokedAt(LocalDateTime.now()));
        refreshTokenRepository.saveAll(refreshTokens);

    }

    public void logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(HashUtil.sha256(request.getRefreshToken()))
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    public void logoutAll() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUser_Id(userId);
        refreshTokens.forEach(refreshToken -> refreshToken.setRevokedAt(LocalDateTime.now()));
        refreshTokenRepository.saveAll(refreshTokens);
    }

    private String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getId())
                        .issuer("dacia1704")
                        .issueTime(new Date())
                        .expirationTime(
                                new Date(
                                        Instant.now()
                                                .plus(jwtExpiration, ChronoUnit.MILLIS)
                                                .toEpochMilli()))
                        .jwtID(UUID.randomUUID().toString())
                        .claim("scope", buildScope(user))
                        .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getId())
                        .issuer("dacia1704")
                        .issueTime(new Date())
                        .expirationTime(
                                new Date(
                                        Instant.now()
                                                .plus(refreshExpiration, ChronoUnit.MILLIS)
                                                .toEpochMilli()))
                        .jwtID(UUID.randomUUID().toString())
                        .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot create refresh token", e);
        }
    }

    // build string role
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles()
                    .forEach(
                            role -> {
                                log.info(role.getName());
                                stringJoiner.add("ROLE_" + role.getName());
                                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                                    role.getPermissions()
                                            .forEach(
                                                    permission ->
                                                            stringJoiner.add(permission.getName()));
                                }
                            });
        }
        return stringJoiner.toString();
    }

    private LoginResponse buildLoginResponse(HttpServletRequest servletRequest, String deviceId, User user) {
        var accessToken = generateAccessToken(user);
        var rawRefreshToken = generateRefreshToken(user);

        var hashRefreshToken = HashUtil.sha256(rawRefreshToken);

        String userAgent = servletRequest.getHeader("User-Agent");

        String deviceName = UserAgentUtil.getDeviceName(userAgent);
        String ip = servletRequest.getRemoteAddr();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashRefreshToken)
                .expiredAt(LocalDateTime.now().plus(Duration.ofMillis(refreshExpiration)))
                .lastUsedAt(LocalDateTime.now())
                .deviceName(deviceName)
                .deviceId(deviceId)
                .ipAddress(ip)
                .userAgent(userAgent)
                .build();

        refreshTokenRepository.save(refreshToken);


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

    @Transactional
    public LoginResponse loginWithGoogle(HttpServletRequest servletRequest,String deviceId,String sessionId,String idToken) {

        GoogleUserInfo googleUser = googleTokenVerifier.verify(idToken);

        if (!googleUser.isEmailVerified()) throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        UserSocialAccount socialAccount = userSocialAccountRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, googleUser.getGoogleId()).orElse(null);

        // Đã từng đăng nhập Google
        if (socialAccount != null) {
            User user = socialAccount.getUser();
            if (!user.isActive()) throw new AppException(ErrorCode.NOT_ACTIVE);
            if (user.isBanned()) throw new AppException(ErrorCode.USER_ALREADY_BANNED);
            return buildLoginResponse(servletRequest, deviceId, user);
        }

        // Email đã tồn tại bằng tài khoản LOCAL
        if (userRepository.existsByEmail(googleUser.getEmail())) throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);

        // Chưa có tài khoản -> tạo mới
        User user = createGoogleUser(googleUser);

        createGoogleSocialAccount(user, googleUser);
        readingHistoryService.mergeSessionHistory(user.getId(), sessionId);
        return buildLoginResponse(servletRequest, deviceId, user);
    }

    private UserSocialAccount createGoogleSocialAccount(User user, GoogleUserInfo googleUser) {
        UserSocialAccount socialAccount =
                UserSocialAccount.builder()
                        .user(user)
                        .provider(AuthProvider.GOOGLE)
                        .providerUserId(googleUser.getGoogleId())
                        .build();
        socialAccount = userSocialAccountRepository.save(socialAccount);
        return socialAccount;
    }
    private User createGoogleUser(GoogleUserInfo googleUser) {
        User user = User.builder()
                .email(googleUser.getEmail())
                .username(generateUniqueUsername(googleUser.getEmail()))
                .avatarUrl(googleUser.getPicture())
                .isActive(true)
                .build();
        List<Role> defaultRoles = roleRepository.findByIsDefaultTrue();
        user.setRoles(new HashSet<>(defaultRoles));

        return userRepository.save(user);
    }

    private String generateUniqueUsername(String email) {

        String baseUsername = email.substring(0, email.indexOf("@"));
        String username = baseUsername;

        int suffix = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + "_" + suffix++;
        }

        return username;
    }
}

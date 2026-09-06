package com.dacia1704.truyenonline.module.authentication.service;

import com.dacia1704.truyenonline.module.authentication.dto.GoogleUserInfo;
import com.dacia1704.truyenonline.module.authentication.dto.request.*;
import com.dacia1704.truyenonline.module.authentication.dto.response.LoginResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.SocialAccountResponse;
import com.dacia1704.truyenonline.module.authentication.entity.AuthProvider;
import com.dacia1704.truyenonline.module.authentication.entity.UserSocialAccount;
import com.dacia1704.truyenonline.module.authentication.repository.UserSocialAccountRepository;
import com.dacia1704.truyenonline.module.interaction.service.ReadingHistoryService;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.RoleRepository;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSocialAccountService {
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final ReadingHistoryService readingHistoryService;
    final UserSocialAccountRepository userSocialAccountRepository;
    final GoogleTokenVerifier googleTokenVerifier;
    final JwtTokenService jwtTokenService;
    final AuthenticationService authenticationService;

    @Transactional
    public LoginResponse loginWithGoogle(
            HttpServletRequest servletRequest, String deviceId, String sessionId, String idToken) {

        GoogleUserInfo googleUser = googleTokenVerifier.verify(idToken);

        if (!googleUser.isEmailVerified()) throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);

        UserSocialAccount socialAccount =
                userSocialAccountRepository
                        .findByProviderAndProviderUserId(
                                AuthProvider.GOOGLE, googleUser.getGoogleId())
                        .orElse(null);

        // Đã từng đăng nhập Google
        if (socialAccount != null) {
            User user = socialAccount.getUser();
            if (!user.isActive()) throw new AppException(ErrorCode.NOT_ACTIVE);
            if (Boolean.TRUE.equals(user.getIsBanned()))
                throw new AppException(ErrorCode.USER_ALREADY_BANNED);
            readingHistoryService.mergeSessionHistory(user.getId(), sessionId);
            return authenticationService.buildLoginResponse(servletRequest, deviceId, user);
        }

        // Email đã tồn tại bằng tài khoản LOCAL
        if (userRepository.existsByEmail(googleUser.getEmail()))
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);

        // Chưa có tài khoản -> tạo mới
        User user = createGoogleUser(googleUser);

        createGoogleSocialAccount(user, googleUser);
        readingHistoryService.mergeSessionHistory(user.getId(), sessionId);
        return authenticationService.buildLoginResponse(servletRequest, deviceId, user);
    }

    @Transactional
    public void linkGoogle(LinkGoogleRequest request) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        GoogleUserInfo googleUser = googleTokenVerifier.verify(request.getIdToken());

        if (!googleUser.isEmailVerified()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (!user.getEmail().equalsIgnoreCase(googleUser.getEmail())) {
            throw new AppException(ErrorCode.GOOGLE_ACCOUNT_EMAIL_NOT_MATCH);
        }

        if (userSocialAccountRepository.existsByUser_IdAndProvider(userId, AuthProvider.GOOGLE)) {
            throw new AppException(ErrorCode.SOCIAL_ACCOUNT_ALREADY_LINKED);
        }

        UserSocialAccount existed =
                userSocialAccountRepository
                        .findByProviderAndProviderUserId(
                                AuthProvider.GOOGLE, googleUser.getGoogleId())
                        .orElse(null);

        if (existed != null) {
            throw new AppException(ErrorCode.SOCIAL_ACCOUNT_ALREADY_LINKED);
        }

        createGoogleSocialAccount(user, googleUser);
    }

    @Transactional
    public void unlinkGoogle() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserSocialAccount socialAccount =
                userSocialAccountRepository
                        .findByUser_IdAndProvider(userId, AuthProvider.GOOGLE)
                        .orElseThrow(() -> new AppException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));

        long providerCount = userSocialAccountRepository.countByUser_Id(userId);
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (providerCount <= 1 && user.getPasswordHash() == null) {
            throw new AppException(
                    ErrorCode.DONOT_HAVE_LOCAL_ACCOUNT_SO_CANNOT_UNLINK_LAST_PROVIDER);
        }

        userSocialAccountRepository.delete(socialAccount);
    }

    @Transactional(readOnly = true)
    public List<SocialAccountResponse> getMyProviders() {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return userSocialAccountRepository.findAllByUser_Id(userId).stream()
                .map(
                        account ->
                                SocialAccountResponse.builder()
                                        .provider(account.getProvider())
                                        .linked(true)
                                        .build())
                .toList();
    }

    private void createGoogleSocialAccount(User user, GoogleUserInfo googleUser) {
        UserSocialAccount socialAccount =
                UserSocialAccount.builder()
                        .user(user)
                        .provider(AuthProvider.GOOGLE)
                        .providerUserId(googleUser.getGoogleId())
                        .build();
        userSocialAccountRepository.save(socialAccount);
    }

    private User createGoogleUser(GoogleUserInfo googleUser) {
        User user =
                User.builder()
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

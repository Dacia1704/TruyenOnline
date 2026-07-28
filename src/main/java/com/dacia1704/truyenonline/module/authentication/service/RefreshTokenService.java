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
import com.dacia1704.truyenonline.module.user.entity.Permission;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.RoleName;
import com.dacia1704.truyenonline.module.user.entity.User;
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

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;

    public long deleteExpiredTokens() {
        long deleteRevoked =  refreshTokenRepository.deleteByRevokedAtIsNotNull();
        long deleteExpired =  refreshTokenRepository.deleteByExpiredAtBefore(LocalDateTime.now());
        return deleteExpired + deleteRevoked;
    }
}

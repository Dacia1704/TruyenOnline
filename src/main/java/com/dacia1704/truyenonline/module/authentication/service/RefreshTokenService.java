package com.dacia1704.truyenonline.module.authentication.service;

import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.entity.RefreshToken;
import com.dacia1704.truyenonline.module.authentication.repository.RefreshTokenRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.utils.HashUtil;
import com.dacia1704.truyenonline.shared.utils.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    long refreshExpiration;

    final JwtTokenService jwtTokenService;
    final RefreshTokenRepository refreshTokenRepository;

    public long deleteExpiredTokens() {
        long deleteRevoked =  refreshTokenRepository.deleteByRevokedAtIsNotNull();
        long deleteExpired =  refreshTokenRepository.deleteByExpiredAtBefore(LocalDateTime.now());
        return deleteExpired + deleteRevoked;
    }

    public RefreshToken createRefreshToken(HttpServletRequest servletRequest, String deviceId,String rawRefreshToken, User user) {
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

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    public RefreshToken createRefreshToken(String userAgent, String deviceName, String ip, String deviceId,String rawRefreshToken, User user) {
        var hashRefreshToken = HashUtil.sha256(rawRefreshToken);
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

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken revokedRefreshToken(String token) {
        String hashToken = HashUtil.sha256(token);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashToken)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if (!refreshToken.isActive()) throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        refreshToken.setLastUsedAt(LocalDateTime.now());
        refreshToken.setTokenHash(hashToken);
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public List<RefreshToken> revokedRefreshTokenByUser(String userId) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUser_Id(userId);
        refreshTokens.forEach(refreshToken -> refreshToken.setRevokedAt(LocalDateTime.now()));
        refreshTokens = refreshTokenRepository.saveAll(refreshTokens);
        return refreshTokens;
    }

    public String renewRefreshToken(String token, User user) {
        String newRefreshToken = jwtTokenService.generateRefreshToken(user);
        RefreshToken refreshToken = revokedRefreshToken(token);
        createRefreshToken(refreshToken.getUserAgent(),
                refreshToken.getDeviceName(),
                refreshToken.getId(),
                refreshToken.getDeviceId(),
                newRefreshToken,
                user);
        return newRefreshToken;
    }

    public RefreshTokenResponse rotateRefreshToken(HttpServletRequest servletRequest, String deviceId, User user) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUser_Id(user.getId());
        refreshTokens.forEach((refreshToken -> refreshToken.setRevokedAt(LocalDateTime.now())));
        refreshTokenRepository.saveAll(refreshTokens);

        var accessToken = jwtTokenService.generateAccessToken(user);
        var rawRefreshToken = jwtTokenService.generateRefreshToken(user);
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
        return RefreshTokenResponse.builder().accessToken(accessToken).refreshToken(rawRefreshToken).build();
    }
}

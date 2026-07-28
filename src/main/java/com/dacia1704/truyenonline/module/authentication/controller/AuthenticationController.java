package com.dacia1704.truyenonline.module.authentication.controller;

import com.dacia1704.truyenonline.module.authentication.dto.request.*;
import com.dacia1704.truyenonline.module.authentication.dto.response.IntrospectResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.LoginResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RegisterResponse;
import com.dacia1704.truyenonline.module.authentication.service.AuthenticationService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(
            HttpServletRequest servletRequest,
            @RequestHeader(value = "Device-Id", required = false) String deviceId,
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authenticationService.authenticate(servletRequest,deviceId,sessionId, request));
    }
    @PostMapping("/google")
    public ApiResponse<LoginResponse> loginWithGoogle(
            HttpServletRequest servletRequest,
            @RequestHeader(value = "Device-Id", required = false) String deviceId,
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @RequestBody @Valid GoogleLoginRequest request) {
        return ApiResponse.success(authenticationService.loginWithGoogle(servletRequest,deviceId,sessionId, request.getIdToken()));
    }

    @PostMapping("/refresh")
    ApiResponse<RefreshTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ApiResponse.success(authenticationService.refreshToken(request));
    }

    @PostMapping("/register")
    ApiResponse<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authenticationService.register(request));
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        var res = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().data(res).build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody @Valid LogoutRequest request) {
        authenticationService.logout(request);
        return ApiResponse.success("Đăng xuất thành công");
    }

    @PostMapping("/logout/all")
    public ApiResponse<String> logout() {
        authenticationService.logoutAll();
        return ApiResponse.success("Đăng xuất toàn bộ thiết bị thành công");
    }


    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ApiResponse.success("Gửi email thành công");
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.success("Đổi mật khẩu thành công");
    }
}

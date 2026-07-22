package com.dacia1704.truyenonline.module.authentication.controller;

import com.dacia1704.truyenonline.module.authentication.dto.request.IntrospectRequest;
import com.dacia1704.truyenonline.module.authentication.dto.request.LoginRequest;
import com.dacia1704.truyenonline.module.authentication.dto.request.RefreshTokenRequest;
import com.dacia1704.truyenonline.module.authentication.dto.request.RegisterRequest;
import com.dacia1704.truyenonline.module.authentication.dto.response.IntrospectResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.LoginResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RegisterResponse;
import com.dacia1704.truyenonline.module.authentication.service.AuthenticationService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
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
    ApiResponse<LoginResponse> login(@RequestHeader(value = "Session-Id", required = false) String sessionId, @RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authenticationService.authenticate(sessionId, request));
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
}

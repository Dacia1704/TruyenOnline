package com.dacia1704.truyenonline.module.authentication.controller;

import com.dacia1704.truyenonline.module.authentication.dto.request.IntrospectRequest;
import com.dacia1704.truyenonline.module.authentication.dto.request.LoginRequest;
import com.dacia1704.truyenonline.module.authentication.dto.request.RegisterRequest;
import com.dacia1704.truyenonline.module.authentication.dto.response.IntrospectResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.LoginResponse;
import com.dacia1704.truyenonline.module.authentication.dto.response.RegisterResponse;
import com.dacia1704.truyenonline.module.authentication.service.AuthenticationService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authenticationService.authenticate(request));
    }

    @PostMapping("/request")
    ApiResponse<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authenticationService.register(request));
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var res = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .data(res).build();
    }
}

package com.dacia1704.truyenonline.module.authentication.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleUserInfo {

    private String googleId;

    private String email;

    private String name;

    private String picture;

    private boolean emailVerified;
}

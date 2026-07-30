package com.dacia1704.truyenonline.module.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkGoogleRequest {

    @NotBlank
    private String idToken;

}
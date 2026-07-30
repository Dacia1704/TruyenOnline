package com.dacia1704.truyenonline.module.authentication.dto.response;

import com.dacia1704.truyenonline.module.authentication.entity.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccountResponse {

    private AuthProvider provider;

    private boolean linked;

}
package com.dacia1704.truyenonline.module.banner.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.authentication.dto.response.RefreshTokenResponse;
import com.dacia1704.truyenonline.module.authentication.service.AuthenticationService;
import com.dacia1704.truyenonline.module.authentication.service.RefreshTokenService;
import com.dacia1704.truyenonline.module.banner.dto.request.BannerCreateRequest;
import com.dacia1704.truyenonline.module.banner.dto.request.BannerStatusRequest;
import com.dacia1704.truyenonline.module.banner.dto.request.BannerUpdateRequest;
import com.dacia1704.truyenonline.module.banner.dto.response.BannerResponse;
import com.dacia1704.truyenonline.module.banner.entity.Banner;
import com.dacia1704.truyenonline.module.banner.mapper.BannerMapper;
import com.dacia1704.truyenonline.module.banner.repository.BannerRepository;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.repository.specification.StorySpecification;
import com.dacia1704.truyenonline.module.user.dto.request.*;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.dto.response.UserUpgradeToUploaderResponse;
import com.dacia1704.truyenonline.module.user.entity.Permission;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.RoleName;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import com.dacia1704.truyenonline.module.user.repository.RoleRepository;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class BannerService {
    BannerRepository bannerRepository;
    BannerMapper bannerMapper;
    CloudinaryService cloudinaryService;
    MediaFileService mediaFileService;

    String folderPath = "truyenonline/banners/%s";


    public BannerResponse createBanner(BannerCreateRequest request) {
        Banner banner = bannerMapper.toBanner(request);
        if(!request.getImageFile().isEmpty()) {
            String path = String.format(folderPath, banner.getId());
            CloudinaryUploadResult fileUploadResult = cloudinaryService.uploadImage(request.getImageFile(), path);
            banner.setBannerUrl(fileUploadResult.getSecureUrl());
        }
        banner = bannerRepository.save(banner);
        return bannerMapper.toBannerResponse(banner);

    }

    public BannerResponse updateBanner(String id, BannerUpdateRequest request) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));

        if(!banner.getBannerUrl().isEmpty() && !request.getImageFile().isEmpty()) {
            mediaFileService.decreaseReference(banner.getBannerUrl());
        }

        bannerMapper.updateBanner(banner, request);
        if(!request.getImageFile().isEmpty()) {
            String path = String.format(folderPath, banner.getId());
            CloudinaryUploadResult fileUploadResult = cloudinaryService.uploadImage(request.getImageFile(), path);
            banner.setBannerUrl(fileUploadResult.getSecureUrl());
        }
        banner = bannerRepository.save(banner);
        return bannerMapper.toBannerResponse(banner);
    }

    public void deleteBanner(String id) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
        bannerRepository.delete(banner);
    }

    public BannerResponse updateStatus(String id, BannerStatusRequest request) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
        banner.setActive(request.getActive());
        banner = bannerRepository.save(banner);
        return bannerMapper.toBannerResponse(banner);
    }

    public BannerResponse getById(String id) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
        return bannerMapper.toBannerResponse(banner);
    }

    public List<BannerResponse> getAllActive() {
        List<Banner> banners = bannerRepository.findByIsActiveTrueOrderBySortOrderAsc();
        return banners.stream().map(bannerMapper::toBannerResponse).toList();
    }

    public PageResponse<BannerResponse> getAll(int page, int size, boolean isActive) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size);

        Page<Banner> bannerPage = bannerRepository.findAllByIsActiveOrderBySortOrderAsc(isActive, pageable);

        List<BannerResponse> bannerResponses = bannerPage.getContent().stream().map(bannerMapper::toBannerResponse).toList();

        return PageResponse.<BannerResponse>builder()
                .currentPage(page)
                .pageSize(bannerPage.getSize())
                .totalPages(bannerPage.getTotalPages())
                .totalElements(bannerPage.getTotalElements())
                .data(bannerResponses)
                .build();
    }
}

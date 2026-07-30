package com.dacia1704.truyenonline.module.administration.service;

import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealApproveRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealRejectRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealResponse;
import com.dacia1704.truyenonline.module.administration.entity.BanAppeal;
import com.dacia1704.truyenonline.module.administration.entity.BanAppealAttachment;
import com.dacia1704.truyenonline.module.administration.entity.BanAppealStatus;
import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.administration.mapper.BanAppealMapper;
import com.dacia1704.truyenonline.module.administration.repository.BanAppealAttachmentRepository;
import com.dacia1704.truyenonline.module.administration.repository.BanAppealRepository;
import com.dacia1704.truyenonline.module.administration.repository.ModerationActionRepository;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class BanAppealService {

    BanAppealRepository banAppealRepository;
    BanAppealAttachmentRepository attachmentRepository;
    BanAppealMapper banAppealMapper;
    ModerationActionRepository moderationActionRepository;
    CloudinaryService cloudinaryService;
    MediaFileService mediaFileService;
    UserService userService;

    String folderPath = "truyenonline/ban-appeals/%s";

    public BanAppealResponse create(BanAppealCreateRequest request) {
        User currentUser = userService.getCurrentUser();
        ModerationAction moderationAction = moderationActionRepository.findById(request.getModerationActionId())
                .orElseThrow(() -> new AppException(ErrorCode.MODERATION_ACTION_NOT_FOUND));
        if (banAppealRepository.existsByModerationAction_Id(moderationAction.getId())) {
            throw new AppException(ErrorCode.BAN_APPEAL_ALREADY_EXISTS);
        }
        BanAppeal appeal = banAppealMapper.toBanAppeal(request);
        appeal.setUser(currentUser);
        appeal.setModerationAction(moderationAction);
        appeal = banAppealRepository.save(appeal);

        if (request.getAttachments() != null) {
            String path = String.format(folderPath, appeal.getId());
            List<CloudinaryUploadResult> cloudinaryUploadResultList = cloudinaryService.uploadImagesAsync(request.getAttachments(), path);
            BanAppeal finalAppeal = appeal;
            cloudinaryUploadResultList.forEach(cloudinaryUploadResult -> {
                attachmentRepository.save(
                        BanAppealAttachment.builder()
                                .appeal(finalAppeal)
                                .attachmentUrl(cloudinaryUploadResult.getSecureUrl())
                                .build()
                );
            });
        }

        return banAppealMapper.toBanAppealResponse(banAppealRepository.findById(appeal.getId()).orElseThrow());
    }

    public void delete(String id) {
        User currentUser = userService.getCurrentUser();
        BanAppeal appeal = banAppealRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BAN_APPEAL_NOT_FOUND));

        if (!appeal.getUser().getId().equals(currentUser.getId())) throw new AppException(ErrorCode.UNAUTHORIZED);
        if (appeal.getStatus() != BanAppealStatus.PENDING) throw new AppException(ErrorCode.BAN_APPEAL_ALREADY_RESOLVED);

        appeal.getAttachments().forEach(a -> mediaFileService.decreaseReference(a.getAttachmentUrl()));
        attachmentRepository.deleteAll(appeal.getAttachments());

        banAppealRepository.delete(appeal);
    }

    public BanAppealResponse approve(String id, BanAppealApproveRequest request) {

        BanAppeal appeal = banAppealRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BAN_APPEAL_NOT_FOUND));

        if (appeal.getStatus() != BanAppealStatus.PENDING) throw new AppException(ErrorCode.BAN_APPEAL_ALREADY_RESOLVED);

        appeal.setStatus(BanAppealStatus.APPROVED);
        appeal.setReviewer(userService.getCurrentUser());
        appeal.setReviewerNote(request.getReviewerNote());
        appeal.setResolvedAt(LocalDateTime.now());

        appeal = banAppealRepository.save(appeal);

        return banAppealMapper.toBanAppealResponse(appeal);
    }

    public BanAppealResponse reject(String id, BanAppealRejectRequest request) {
        BanAppeal appeal = banAppealRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BAN_APPEAL_NOT_FOUND));

        if (appeal.getStatus() != BanAppealStatus.PENDING) throw new AppException(ErrorCode.BAN_APPEAL_ALREADY_RESOLVED);

        appeal.setStatus(BanAppealStatus.REJECTED);
        appeal.setReviewer(userService.getCurrentUser());
        appeal.setReviewerNote(request.getReviewerNote());
        appeal.setResolvedAt(LocalDateTime.now());

        appeal = banAppealRepository.save(appeal);

        return banAppealMapper.toBanAppealResponse(appeal);
    }

    public PageResponse<BanAppealResponse> getMe(int page, int size) {

        int pageNo = page > 0 ? page - 1 : 0;

        Pageable pageable = PageRequest.of(pageNo, size);

        User currentUser = userService.getCurrentUser();

        Page<BanAppeal> appealPage =
                banAppealRepository.findAllByUser_IdOrderByCreatedAtDesc(
                        currentUser.getId(),
                        pageable);

        return PageResponse.<BanAppealResponse>builder()
                .currentPage(page)
                .pageSize(appealPage.getSize())
                .totalPages(appealPage.getTotalPages())
                .totalElements(appealPage.getTotalElements())
                .data(
                        appealPage.getContent()
                                .stream()
                                .map(banAppealMapper::toBanAppealResponse)
                                .toList()
                )
                .build();
    }

    public PageResponse<BanAppealResponse> getAll(int page, int size) {

        int pageNo = page > 0 ? page - 1 : 0;

        Pageable pageable = PageRequest.of(pageNo, size);

        Page<BanAppeal> appealPage =
                banAppealRepository.findAllByOrderByCreatedAtDesc(pageable);

        return PageResponse.<BanAppealResponse>builder()
                .currentPage(page)
                .pageSize(appealPage.getSize())
                .totalPages(appealPage.getTotalPages())
                .totalElements(appealPage.getTotalElements())
                .data(
                        appealPage.getContent()
                                .stream()
                                .map(banAppealMapper::toBanAppealResponse)
                                .toList()
                )
                .build();
    }
}
package com.dacia1704.truyenonline.module.story.service;

import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.story.dto.request.*;
import com.dacia1704.truyenonline.module.story.dto.response.StoryPublishRequestResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.*;
import com.dacia1704.truyenonline.module.story.mapper.AuthorMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryAuthorMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryPublishRequestMapper;
import com.dacia1704.truyenonline.module.story.repository.AuthorRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryAuthorRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryPublishRequestRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.story.repository.specification.StoryPublishRequestSpecification;
import com.dacia1704.truyenonline.module.story.repository.specification.StorySpecification;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.shared.utils.StringUtils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class StoryService {

    StoryRepository storyRepository;
    StoryMapper storyMapper;
    StoryPublishRequestRepository storyPublishRequestRepository;
    StoryPublishRequestMapper storyPublishRequestMapper;
    UserRepository userRepository;
    CloudinaryService cloudinaryService;
    UserService userService;
    ModerationActionService moderationActionService;
    AuditLogService auditLogService;
    ObjectMapper objectMapper;
    AuthorMapper authorMapper;
    StoryAuthorRepository storyAuthorRepository;
    AuthorRepository authorRepository;
    AuthorService authorService;
    StoryAuthorMapper storyAuthorMapper;
    MediaFileService mediaFileService;
    ChapterRepository chapterRepository;

    String folderPath = "truyenonline/stories/%s";

    // lấy toàn bộ truyện có phân trang, search theo tên, filter thuộc tính
    public PageResponse<StoryResponse> getStories(
            int page, int size, StoryFilter filters) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size);

        Specification<Story> spec = StorySpecification.filterStories(filters);

        Page<Story> storyPage = storyRepository.findAll(spec, pageable);

        List<StoryResponse> storyResponses = storyPage.getContent().stream().map(story -> {
            StoryResponse response = storyMapper.toStoryResponse(story);
            if (story.getAuthors() != null) {
                response.setAuthors(story.getStoryAuthors().stream().map(storyAuthorMapper::toStoryAuthorResponse).toList());
            }

            return response;
        }).toList();

        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(storyPage.getSize())
                .totalPages(storyPage.getTotalPages())
                .totalElements(storyPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    public StoryResponse getStoryBySlug(String slug) {
        Story story =
                storyRepository
                        .findBySlug(slug)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        StoryResponse response = storyMapper.toStoryResponse(story);

        // Lấy danh sách tác giả từ helper method của entity Story và map sang DTO
        if (story.getAuthors() != null) {
            response.setAuthors(
                    story.getStoryAuthors().stream()
                            .map(storyAuthorMapper::toStoryAuthorResponse)
                            .toList()
            );
        }

        return response;
    }

    public StoryResponse createStory(StoryCreateRequest request) throws IOException {
        Story story = storyMapper.toStory(request);
        User uploader = userService.getCurrentUser();
        story.setUploader(uploader);

        if (request.getCoverImageFile() != null && !request.getCoverImageFile().isEmpty()) {
            String path = String.format(folderPath, story.getId());
            CloudinaryUploadResult fileUploadResult = cloudinaryService.uploadImage(request.getCoverImageFile(), path);
            mediaFileService.createMediaFile(fileUploadResult);
            story.setCoverImageUrl(fileUploadResult.getSecureUrl());
        }

        story.setSlug(generateSlug(request.getTitle()));
        story.setTitleNoAccent(StringUtils.removeAccent(request.getTitle()));
        story = storyRepository.save(story);

        if (request.getAuthors() != null && !request.getAuthors().isEmpty()) {
            return authorService.updateStoryAuthors(story.getId(), request.getAuthors());
        }

        auditLogService.log(AuditAction.CREATE, AuditObjectType.STORY, story.getId(), null, story, null);

        return storyMapper.toStoryResponse(story);
    }

    public StoryResponse updateStory(String storyId, StoryUpdateRequest request) throws IOException {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        Story oldValue = objectMapper.convertValue(story, Story.class);
        if(!request.getCoverImageUrl().isEmpty() && !request.getCoverImageFile().isEmpty() && !story.getCoverImageUrl().isEmpty()) {
            mediaFileService.decreaseReference(story.getCoverImageUrl());
        }
        if(!request.getCoverImageFile().isEmpty()) {
            String path = String.format(folderPath, story.getId());
            CloudinaryUploadResult fileUploadResult = cloudinaryService.uploadImage(request.getCoverImageFile(), path);
            mediaFileService.createMediaFile(fileUploadResult);
            story.setCoverImageUrl(fileUploadResult.getSecureUrl());
        }

        storyMapper.updateStory(story, request);
        story.setSlug(generateSlug(request.getTitle()));
        story.setTitleNoAccent(StringUtils.removeAccent(request.getTitle()));
        story = storyRepository.save(story);

        if(!request.getAuthors().isEmpty()) {
            return  authorService.updateStoryAuthors(story.getId(),request.getAuthors());
        }
        auditLogService.log(AuditAction.UPDATE, AuditObjectType.STORY, storyId, oldValue, story, null);
        return storyMapper.toStoryResponse(story);
    }

    public void deleteStory(String id) {
        Story story = storyRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        mediaFileService.decreaseReference(story.getCoverImageUrl());
        storyRepository.deleteById(id);
        auditLogService.log(AuditAction.DELETE, AuditObjectType.STORY, id, story, null, null);
    }

    public void deletePublishRequest(String id) {
        StoryPublishRequest storyPublishRequest = storyPublishRequestRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_PUBLISH_REQUEST_NOT_FOUND));
        storyPublishRequestRepository.deleteById(id);
        auditLogService.log(AuditAction.DELETE, AuditObjectType.PUBLISH_REQUEST, id, storyPublishRequest, null, null);
    }

    public StoryPublishRequestResponse requestPublish(
            String storyId, StoryPublishRequestCreateRequest request) {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        if(!userId.equals(story.getUploader().getId())) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }

        List<StoryPublishRequest> storyPublishRequests = storyPublishRequestRepository.findAllByStoryId(storyId);
        if (story.isPublished() && storyPublishRequests.stream().anyMatch(publishRequest -> publishRequest.getStatus() == StoryPublishRequestStatus.APPROVED)) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_APPROVED);
        }

        if (storyPublishRequests.stream().anyMatch(publishRequest -> publishRequest.getStatus() == StoryPublishRequestStatus.PENDING)) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_PENDING);
        }

        if(chapterRepository.countByStory_Id(storyId) >=1 ) {
            throw new AppException(ErrorCode.NEED_AT_LEAST_A_CHAPTER_TO_PUBLISH);
        }


        StoryPublishRequest storyPublishRequest = new StoryPublishRequest();
        storyPublishRequest.setRequesterNote(request.getRequesterNote());
        storyPublishRequest.setStory(story);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.PENDING);

        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);
        auditLogService.log(AuditAction.CREATE, AuditObjectType.PUBLISH_REQUEST, storyPublishRequest.getId(), null, storyPublishRequest, null);

        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public StoryPublishRequestResponse approvePublishRequest(
            String publishRequestId, StoryPublishRequestReviewRequest request) {
        User user = userService.getCurrentUser();
        StoryPublishRequest storyPublishRequest =
                storyPublishRequestRepository
                        .findById(publishRequestId)
                        .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        StoryPublishRequest oldValue = objectMapper.convertValue(storyPublishRequest, StoryPublishRequest.class);
        if (storyPublishRequest.getStatus() != StoryPublishRequestStatus.PENDING) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_CONFIRMED);
        }

        storyPublishRequest.setReviewerNote(request.getReviewerNote());
        storyPublishRequest.setReviewer(user);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.APPROVED);
        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);

        Story story = storyPublishRequest.getStory();
        story.setPublished(true);
        storyRepository.save(story);
        auditLogService.log(AuditAction.APPROVE, AuditObjectType.PUBLISH_REQUEST, storyPublishRequest.getId(), oldValue, storyPublishRequest, null);

        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public StoryPublishRequestResponse rejectPublishRequest(
            String publishRequestId, StoryPublishRequestReviewRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        StoryPublishRequest storyPublishRequest =
                storyPublishRequestRepository
                        .findById(publishRequestId)
                        .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        StoryPublishRequest oldValue = objectMapper.convertValue(storyPublishRequest, StoryPublishRequest.class);
        if (storyPublishRequest.getStatus() != StoryPublishRequestStatus.PENDING) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_CONFIRMED);
        }
        storyPublishRequest.setReviewerNote(request.getReviewerNote());
        storyPublishRequest.setReviewer(user);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.REJECTED);
        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);
        auditLogService.log(AuditAction.REJECT, AuditObjectType.PUBLISH_REQUEST, storyPublishRequest.getId(), oldValue, storyPublishRequest, null);

        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public PageResponse<StoryPublishRequestResponse> getPublishRequests(
            int page,
            int size,
            String storyId,
            StoryPublishRequestStatus status,
            String uploaderId) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Specification<StoryPublishRequest> spec =
                StoryPublishRequestSpecification.filterRequests(storyId, status, uploaderId);

        Page<StoryPublishRequest> storyPublishRequestPage =
                storyPublishRequestRepository.findAll(spec, pageable);
        List<StoryPublishRequestResponse> storyResponses =
                storyPublishRequestPage.getContent().stream()
                        .map(storyPublishRequestMapper::toStoryPublishRequestResponse)
                        .toList();
        return PageResponse.<StoryPublishRequestResponse>builder()
                .currentPage(page)
                .pageSize(storyPublishRequestPage.getSize())
                .totalPages(storyPublishRequestPage.getTotalPages())
                .totalElements(storyPublishRequestPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    public PageResponse<StoryPublishRequestResponse> getMyPublishRequests(
            int page, int size, String storyId, StoryPublishRequestStatus status) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());
        Specification<StoryPublishRequest> spec =
                StoryPublishRequestSpecification.filterRequests(storyId, status, userId);
        Page<StoryPublishRequest> storyPublishRequestPage =
                storyPublishRequestRepository.findAll(spec, pageable);
        List<StoryPublishRequestResponse> storyResponses =
                storyPublishRequestPage.getContent().stream()
                        .map(storyPublishRequestMapper::toStoryPublishRequestResponse)
                        .toList();
        return PageResponse.<StoryPublishRequestResponse>builder()
                .currentPage(page)
                .pageSize(storyPublishRequestPage.getSize())
                .totalPages(storyPublishRequestPage.getTotalPages())
                .totalElements(storyPublishRequestPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    public StoryResponse banStory(String storyId, StoryBanRequest request) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        if (story.isBanned()) throw new AppException(ErrorCode.STORY_ALREADY_BANNED);
        Story oldValue = objectMapper.convertValue(story, Story.class);
        story.setBanned(true);
        story = storyRepository.save(story);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(storyId)
                        .objectType(ModerationObjectType.STORY)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build()
        );
        auditLogService.log(AuditAction.BAN, AuditObjectType.STORY,storyId,oldValue,story, request.getReason());
        return storyMapper.toStoryResponse(story);
    }
    public StoryResponse unbanStory(String storyId, StoryUnbanRequest request) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        if (!story.isBanned()) throw new AppException(ErrorCode.STORY_NOT_GET_BANNED);
        Story oldValue = objectMapper.convertValue(story, Story.class);
        story.setBanned(false);
        story = storyRepository.save(story);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(storyId)
                        .objectType(ModerationObjectType.STORY)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build()
        );
        auditLogService.log(AuditAction.UNBAN, AuditObjectType.STORY,storyId,oldValue,story, request.getReason());
        return storyMapper.toStoryResponse(story);
    }

    private String generateSlug(String title) {
        // 1. Tạo title không dấu
        String titleNoAccent = StringUtils.removeAccent(title);

        // 2. Dọn dẹp ký tự đặc biệt, thay khoảng trắng thành gạch ngang
        String baseSlug = titleNoAccent.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        String finalSlug = baseSlug;
        int counter = 1;
        while (storyRepository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }

        return finalSlug;
    }


}

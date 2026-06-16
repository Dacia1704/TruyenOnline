package com.dacia1704.truyenonline.module.story.service;

import com.dacia1704.truyenonline.module.story.dto.request.*;
import com.dacia1704.truyenonline.module.story.dto.response.StoryPublishRequestResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequest;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequestStatus;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryPublishRequestMapper;
import com.dacia1704.truyenonline.module.story.repository.StoryPublishRequestRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.story.repository.specification.StoryPublishRequestSpecification;
import com.dacia1704.truyenonline.module.story.repository.specification.StorySpecification;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.dacia1704.truyenonline.shared.utils.StringUtils;
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

import java.util.List;

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

    // lấy toàn bộ truyện có phân trang, search theo tên, filter thuộc tính
    public PageResponse<StoryResponse> getStories(int page, int size, String search, StoryFilter filters) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Specification<Story> spec = StorySpecification.filterStories(search, filters);

        Page<Story> storyPage = storyRepository.findAll(spec, pageable);
        List<StoryResponse> storyResponses =
                storyPage.getContent().stream().map(storyMapper::toStoryResponse).toList();
        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(storyPage.getSize())
                .totalPages(storyPage.getTotalPages())
                .totalElements(storyPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    // lấy toàn bộ ko phân trang
    public List<StoryResponse> getStories(String search, StoryFilter filters) {

        Specification<Story> spec = StorySpecification.filterStories(search, filters);

        List<Story> stories = storyRepository.findAll(spec);
        return stories.stream().map(storyMapper::toStoryResponse).toList();
    }

    public StoryResponse getStoryBySlug(String slug) {
        Story story = storyRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        return storyMapper.toStoryResponse(story);
    }

    public StoryResponse createStory(StoryCreateRequest request) {
        Story story = storyMapper.toStory(request);
        story.setSlug(generateSlug(request.getTitle()));
        story.setTitleNoAccent(StringUtils.removeAccent(request.getTitle()));
        story = storyRepository.save(story);
        return storyMapper.toStoryResponse(story);
    }

    public StoryResponse updateStory(String storyId, StoryUpdateRequest request) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        storyMapper.updateStory(story, request);
        story.setSlug(generateSlug(request.getTitle()));
        story.setTitleNoAccent(StringUtils.removeAccent(request.getTitle()));
        story = storyRepository.save(story);
        return storyMapper.toStoryResponse(story);
    }

    public void deleteStory(String id) {
        storyRepository.deleteById(id);
    }

    public StoryPublishRequestResponse requestPublish(String storyId, StoryPublishRequestCreateRequest request) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        StoryPublishRequest storyPublishRequest = new StoryPublishRequest();
        storyPublishRequest.setRequesterNote(request.getRequesterNote());
        storyPublishRequest.setStory(story);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.PENDING);

        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);
        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public StoryPublishRequestResponse approvePublishRequest(String publishRequestId, StoryPublishRequestReviewRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        StoryPublishRequest storyPublishRequest = storyPublishRequestRepository.findById(publishRequestId).orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        storyPublishRequest.setReviewerNote(request.getReviewerNote());
        storyPublishRequest.setReviewer(user);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.APPROVED);
        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);
        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public StoryPublishRequestResponse rejectPublishRequest(String publishRequestId, StoryPublishRequestReviewRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        StoryPublishRequest storyPublishRequest = storyPublishRequestRepository.findById(publishRequestId).orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        storyPublishRequest.setReviewerNote(request.getReviewerNote());
        storyPublishRequest.setReviewer(user);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.REJECTED);
        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);
        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public PageResponse<StoryPublishRequestResponse> getPublishRequests(int page, int size, String storyId, StoryPublishRequestStatus status, String uploaderId) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Specification<StoryPublishRequest> spec = StoryPublishRequestSpecification.filterRequests(storyId, status, uploaderId);

        Page<StoryPublishRequest> storyPublishRequestPage = storyPublishRequestRepository.findAll(spec, pageable);
        List<StoryPublishRequestResponse> storyResponses =
                storyPublishRequestPage.getContent().stream().map(storyPublishRequestMapper::toStoryPublishRequestResponse).toList();
        return PageResponse.<StoryPublishRequestResponse>builder()
                .currentPage(page)
                .pageSize(storyPublishRequestPage.getSize())
                .totalPages(storyPublishRequestPage.getTotalPages())
                .totalElements(storyPublishRequestPage.getTotalElements())
                .data(storyResponses)
                .build();
    }
    public PageResponse<StoryPublishRequestResponse> getMyPublishRequests(int page, int size, String storyId, StoryPublishRequestStatus status) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());
        Specification<StoryPublishRequest> spec = StoryPublishRequestSpecification.filterRequests(storyId, status, userId);
        Page<StoryPublishRequest> storyPublishRequestPage = storyPublishRequestRepository.findAll(spec, pageable);
        List<StoryPublishRequestResponse> storyResponses =
                storyPublishRequestPage.getContent().stream().map(storyPublishRequestMapper::toStoryPublishRequestResponse).toList();
        return PageResponse.<StoryPublishRequestResponse>builder()
                .currentPage(page)
                .pageSize(storyPublishRequestPage.getSize())
                .totalPages(storyPublishRequestPage.getTotalPages())
                .totalElements(storyPublishRequestPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    private String generateSlug(String title) {
        // 1. Tạo title không dấu
        String titleNoAccent = StringUtils.removeAccent(title);

        // 2. Dọn dẹp ký tự đặc biệt, thay khoảng trắng thành gạch ngang
        String baseSlug = titleNoAccent.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        String finalSlug = baseSlug;
        int counter = 1;
        while (storyRepository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }

        return finalSlug;
    }


}

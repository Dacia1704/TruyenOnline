package com.dacia1704.truyenonline.module.chapter.service;

import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterPageListRequest;
import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterPageRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterPageResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.entity.ChapterPage;
import com.dacia1704.truyenonline.module.chapter.mapper.ChapterPageMapper;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterPageRepository;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.payment.entity.Subscription;
import com.dacia1704.truyenonline.module.payment.repository.SubscriptionRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ChapterPageService {

    CloudinaryService cloudinaryService;

    ChapterPageRepository chapterPageRepository;
    ChapterRepository chapterRepository;
    ChapterPageMapper chapterPageMapper;
    MediaFileService mediaFileService;
    UserRepository userRepository;
    SubscriptionRepository subscriptionRepository;
    String folderPath = "truyenonline/stories/%s/chapters/%s";

    public List<ChapterPageResponse> createChapterPage(ChapterPageListRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(request.getChapterId())
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        String path = String.format(folderPath, chapter.getStory().getId(), chapter.getId());

        List<ChapterPageRequest> pageRequests = request.getChapterPageRequests();
        List<MultipartFile> files = request.getFiles();

        if (files == null || files.size() != pageRequests.size()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        List<CloudinaryUploadResult> uploadResults =
                cloudinaryService.uploadImagesAsync(files, path);

        List<ChapterPage> chapterPages = new ArrayList<>();

        for (int i = 0; i < pageRequests.size(); i++) {
            ChapterPageRequest pageRequest = pageRequests.get(i);
            CloudinaryUploadResult result = uploadResults.get(i);
            mediaFileService.createMediaFile(result);

            ChapterPage chapterPage = chapterPageMapper.toChapter(pageRequest);
            chapterPage.setChapter(chapter);
            chapterPage.setCloudinaryId(result.getPublicId());
            chapterPage.setImageUrl(result.getSecureUrl());
            chapterPage.setHeight(result.getHeight());
            chapterPage.setWidth(result.getWidth());

            chapterPages.add(chapterPage);
        }

        List<ChapterPage> savedPages = chapterPageRepository.saveAll(chapterPages);

        return savedPages.stream().map(chapterPageMapper::toChapterPageResponse).toList();
    }

    public List<ChapterPageResponse> updateChapterPage(ChapterPageListRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(request.getChapterId())
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        String path = String.format(folderPath, chapter.getStory().getId(), chapter.getId());

        List<String> pageIds =
                request.getChapterPageRequests().stream()
                        .map(ChapterPageRequest::getId)
                        .filter(Objects::nonNull)
                        .toList();

        List<ChapterPage> existingPages = chapterPageRepository.findAllById(pageIds);

        Map<String, ChapterPage> pageMap =
                existingPages.stream().collect(Collectors.toMap(ChapterPage::getId, page -> page));

        List<MultipartFile> files = request.getFiles();

        List<CloudinaryUploadResult> uploadResults = Collections.emptyList();

        if (files != null && !files.isEmpty()) {
            uploadResults = cloudinaryService.uploadImagesAsync(files, path);
        }

        List<ChapterPage> pagesToSave = new ArrayList<>();
        int uploadIndex = 0;

        for (ChapterPageRequest pageReq : request.getChapterPageRequests()) {

            ChapterPage chapterPage = pageMap.get(pageReq.getId());
            if (chapterPage == null) continue;
            chapterPage.setPageNumber(pageReq.getPageNumber());

            if (Boolean.TRUE.equals(pageReq.getIsNewPage())) {

                if (uploadIndex >= uploadResults.size())
                    throw new AppException(ErrorCode.INVALID_REQUEST);

                CloudinaryUploadResult result = uploadResults.get(uploadIndex++);
                mediaFileService.createMediaFile(result);
                chapterPage.setCloudinaryId(result.getPublicId());
                chapterPage.setImageUrl(result.getSecureUrl());
                chapterPage.setHeight(result.getHeight());
                chapterPage.setWidth(result.getWidth());
            }

            pagesToSave.add(chapterPage);
        }

        List<ChapterPage> savedPages = chapterPageRepository.saveAll(pagesToSave);

        return savedPages.stream().map(chapterPageMapper::toChapterPageResponse).toList();
    }

    public List<ChapterPageResponse> getChapterPagesByChapterId(String chapterId) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        if (chapter.getStory().getFreeChapterLimit() != null
                && BigDecimal.valueOf(chapter.getStory().getFreeChapterLimit())
                                .compareTo(chapter.getChapterNumber())
                        < 0) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean authenticated =
                    authentication != null
                            && authentication.isAuthenticated()
                            && !(authentication instanceof AnonymousAuthenticationToken);
            if (authenticated) {
                String userId = authentication.getName();
                User user =
                        userRepository
                                .findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                Subscription subscription =
                        subscriptionRepository
                                .findActiveByUser(authentication.getName(), LocalDateTime.now())
                                .orElse(null);
                boolean isUploader = chapterRepository.isChapterUploader(chapterId, userId);
                boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
                if (subscription == null && !(isUploader || isAdmin)) {
                    throw new AppException(ErrorCode.PREMIUM_REQUIRED);
                }
            } else {
                throw new AppException(ErrorCode.PREMIUM_REQUIRED);
            }
        }

        List<ChapterPage> chapterPages = chapterPageRepository.findByChapterIdOrderByPageNumberAsc(chapterId);
        return chapterPages.stream().map(chapterPageMapper::toChapterPageResponse).toList();
    }

    public void deleteChapterPageByChapter(String chapterId) {
        List<ChapterPage> chapterPages = chapterPageRepository.findByChapterId(chapterId);
        for (ChapterPage chapterPage : chapterPages) {
            mediaFileService.decreaseReference(chapterPage.getImageUrl());
        }

        chapterPageRepository.deleteByChapterId(chapterId);
    }
}

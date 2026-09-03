package com.dacia1704.truyenonline.module.story.service;

import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.story.dto.request.AuthorCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.AuthorUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryAuthorUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.AuthorResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Author;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.entity.StoryAuthor;
import com.dacia1704.truyenonline.module.story.mapper.AuthorMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import com.dacia1704.truyenonline.module.story.repository.AuthorRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryAuthorRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.dacia1704.truyenonline.shared.utils.StringUtils;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AuthorService {

    AuthorRepository authorRepository;
    AuthorMapper authorMapper;
    StoryRepository storyRepository;
    StoryAuthorRepository storyAuthorRepository;
    CloudinaryService cloudinaryService;
    StoryMapper storyMapper;
    MediaFileService mediaFileService;

    String folderPath = "truyenonline/authors/%s";

    public PageResponse<AuthorResponse> getAuthors(int page, int size, String search) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size);

        Page<Author> authorPage;
        if (search != null && !search.isEmpty()) {
            authorPage = authorRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            authorPage = authorRepository.findAll(pageable);
        }

        List<AuthorResponse> responses =
                authorPage.getContent().stream().map(authorMapper::toAuthorResponse).toList();

        return PageResponse.<AuthorResponse>builder()
                .currentPage(page)
                .pageSize(authorPage.getSize())
                .totalPages(authorPage.getTotalPages())
                .totalElements(authorPage.getTotalElements())
                .data(responses)
                .build();
    }

    public AuthorResponse getAuthorBySlug(String slug) {
        Author author =
                authorRepository
                        .findBySlug(slug)
                        .orElseThrow(
                                () ->
                                        new AppException(
                                                ErrorCode
                                                        .AUTHOR_NOT_FOUND)); // Định nghĩa ErrorCode
        // thêm
        return authorMapper.toAuthorResponse(author);
    }

    public AuthorResponse createAuthor(AuthorCreateRequest request) throws IOException {
        Author author = authorMapper.toAuthor(request);
        author.setId(UUID.randomUUID().toString()); // Khởi tạo ID kiểu CHAR(36)

        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            String path = String.format(folderPath, author.getId());
            CloudinaryUploadResult uploadResult =
                    cloudinaryService.uploadImage(request.getAvatarFile(), path);
            mediaFileService.createMediaFile(uploadResult);
            author.setAvatarUrl(uploadResult.getSecureUrl());
        }

        author.setSlug(generateSlug(request.getName()));
        author.setNameNoAccent(StringUtils.removeAccent(request.getName()));

        return authorMapper.toAuthorResponse(authorRepository.save(author));
    }

    public AuthorResponse updateAuthor(String id, AuthorUpdateRequest request) throws IOException {
        Author author =
                authorRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            String path = String.format(folderPath, author.getId());
            CloudinaryUploadResult uploadResult =
                    cloudinaryService.uploadImage(request.getAvatarFile(), path);
            mediaFileService.createMediaFile(uploadResult);
            author.setAvatarUrl(uploadResult.getSecureUrl());
        }

        authorMapper.updateAuthor(author, request);

        if (request.getName() != null) {
            author.setSlug(generateSlug(request.getName()));
            author.setNameNoAccent(StringUtils.removeAccent(request.getName()));
        }

        return authorMapper.toAuthorResponse(authorRepository.save(author));
    }

    public void deleteAuthor(String id) {
        Author author =
                authorRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));
        mediaFileService.decreaseReference(author.getAvatarUrl());
        authorRepository.deleteById(id);
    }

    // --- Xử lý liên kết Story <-> Author ---

    public StoryResponse updateStoryAuthors(
            String storyId, List<StoryAuthorUpdateRequest> requests) {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        // 1. Xóa các author cũ của truyện này
        storyAuthorRepository.deleteByStoryId(storyId);

        // 2. Thêm lại list author mới
        List<StoryAuthor> storyAuthors =
                requests.stream()
                        .map(
                                req -> {
                                    Author author =
                                            authorRepository
                                                    .findById(req.getAuthorId())
                                                    .orElseThrow(
                                                            () ->
                                                                    new AppException(
                                                                            ErrorCode
                                                                                    .AUTHOR_NOT_FOUND));

                                    return StoryAuthor.builder()
                                            .storyId(story.getId())
                                            .authorId(author.getId())
                                            .story(story)
                                            .author(author)
                                            .role(req.getRole())
                                            .sortOrder(req.getSortOrder())
                                            .build();
                                })
                        .toList();

        storyAuthors = storyAuthorRepository.saveAll(storyAuthors);
        story.getStoryAuthors().clear();
        story.getStoryAuthors().addAll(storyAuthors);
        return storyMapper.toStoryResponse(story);
    }

    private String generateSlug(String name) {
        String nameNoAccent = StringUtils.removeAccent(name);
        String baseSlug =
                nameNoAccent.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        String finalSlug = baseSlug;
        int counter = 1;
        while (authorRepository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }
        return finalSlug;
    }
}

package com.dacia1704.truyenonline.module.story.service;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.AuditMapper;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.story.dto.request.GenreRequest;
import com.dacia1704.truyenonline.module.story.dto.response.GenreResponse;
import com.dacia1704.truyenonline.module.story.entity.Genre;
import com.dacia1704.truyenonline.module.story.mapper.GenreMapper;
import com.dacia1704.truyenonline.module.story.repository.GenreRepository;
import com.dacia1704.truyenonline.module.story.repository.specification.GenreSpecification;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class GenreService {
    GenreRepository genreRepository;
    GenreMapper genreMapper;
    AuditLogService auditLogService;
    ObjectMapper objectMapper;

    public List<GenreResponse> getGenres(String search) {
        Specification<Genre> spec = GenreSpecification.filterGenres(search);
        List<Genre> storyPage = genreRepository.findAll(spec);
        return storyPage.stream().map(genreMapper::toGenreResponse).toList();
    }

    public GenreResponse createGenre(GenreRequest request) {
        Genre genre = genreMapper.toGenre(request);
        genre.setSlug(generateSlug(request.getName()));
        genre.setNameNoAccent(StringUtils.removeAccent(request.getName()));
        genre = genreRepository.save(genre);
        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.GENRE,
                genre.getId().toString(),
                null,
                buildAuditLogGenre(genre),
                null);

        return genreMapper.toGenreResponse(genre);
    }

    public GenreResponse updateGenre(String id, GenreRequest request) {
        Genre genre =
                genreRepository
                        .findById(Integer.parseInt(id))
                        .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
        var oldValue = buildAuditLogGenre(genre);
        genreMapper.updateGenre(genre, request);
        genre.setSlug(generateSlug(request.getName()));
        genre.setNameNoAccent(StringUtils.removeAccent(request.getName()));
        genre = genreRepository.save(genre);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.GENRE,
                genre.getId().toString(),
                oldValue,
                buildAuditLogGenre(genre),
                null);

        return genreMapper.toGenreResponse(genre);
    }

    public void deleteGenre(String id) {
        Genre genre =
                genreRepository
                        .findById(Integer.parseInt(id))
                        .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
        genreRepository.deleteById(Integer.parseInt(id));
        auditLogService.log(
                AuditAction.DELETE,
                AuditObjectType.GENRE,
                genre.getId().toString(),
                buildAuditLogGenre(genre),
                null,
                null);
    }

    private String generateSlug(String name) {
        // 1. Tạo name không dấu
        String nameNoAccent = StringUtils.removeAccent(name);

        // 2. Dọn dẹp ký tự đặc biệt, thay khoảng trắng thành gạch ngang
        String baseSlug =
                nameNoAccent.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        String finalSlug = baseSlug;
        int counter = 1;
        while (genreRepository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }

        return finalSlug;
    }

    public Map<String, Object> buildAuditLogGenre(Genre genre) {
        if (genre == null) {
            return Map.of();
        }

        return AuditMapper.of(genre).add("id", Genre::getId).add("name", Genre::getName).build();
    }
}

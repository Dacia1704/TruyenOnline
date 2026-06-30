package com.dacia1704.truyenonline.module.story.service;

import com.dacia1704.truyenonline.module.story.dto.request.GenreRequest;
import com.dacia1704.truyenonline.module.story.dto.response.GenreResponse;
import com.dacia1704.truyenonline.module.story.entity.Genre;
import com.dacia1704.truyenonline.module.story.mapper.GenreMapper;
import com.dacia1704.truyenonline.module.story.repository.GenreRepository;
import com.dacia1704.truyenonline.module.story.repository.specification.GenreSpecification;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.utils.StringUtils;
import java.util.List;
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
        return genreMapper.toGenreResponse(genre);
    }

    public GenreResponse updateGenre(String id, GenreRequest request) {
        Genre genre =
                genreRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
        genreMapper.updateGenre(genre, request);
        genre.setSlug(generateSlug(request.getName()));
        genre.setNameNoAccent(StringUtils.removeAccent(request.getName()));
        genre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(genre);
    }

    public void deleteGenre(String id) {
        genreRepository.deleteById(id);
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
}

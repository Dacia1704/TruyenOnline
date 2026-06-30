package com.dacia1704.truyenonline.module.story.controller;

import com.dacia1704.truyenonline.module.story.dto.request.GenreRequest;
import com.dacia1704.truyenonline.module.story.dto.response.GenreResponse;
import com.dacia1704.truyenonline.module.story.service.GenreService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {
    GenreService genreService;

    @GetMapping("")
    public ApiResponse<List<GenreResponse>> getGenres(@RequestParam() String search) {
        List<GenreResponse> result = genreService.getGenres(search);
        return ApiResponse.success(result);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<GenreResponse> createGenre(@RequestBody @Valid GenreRequest request) {
        GenreResponse result = genreService.createGenre(request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<GenreResponse> updateGenre(
            @PathVariable("id") String id, @RequestBody @Valid GenreRequest request) {
        GenreResponse result = genreService.updateGenre(id, request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteGenre(@PathVariable("id") String id) {
        genreService.deleteGenre(id);
        return ApiResponse.success("Xóa thể loại thành công");
    }
}

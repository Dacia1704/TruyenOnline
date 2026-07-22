package com.dacia1704.truyenonline.module.interaction.service;

import com.dacia1704.truyenonline.module.interaction.dto.response.BookmarkResponse;
import com.dacia1704.truyenonline.module.interaction.entity.Bookmark;
import com.dacia1704.truyenonline.module.interaction.mapper.BookmarkMapper;
import com.dacia1704.truyenonline.module.interaction.repository.BookmarkRepository;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class BookmarkService {

    BookmarkRepository bookmarkRepository;
    BookmarkMapper bookmarkMapper;
    UserRepository userRepository;
    private final StoryRepository storyRepository;

    public BookmarkResponse getBookmarkByStoryId(String storyId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Ném lỗi 401
        }
        String userId = authentication.getName();
        return bookmarkRepository.findByUserIdAndStoryId(userId, storyId)
                .map(bookmarkMapper::toBookmarkResponse)
                .orElse(null);
    }

    public BookmarkResponse createBookmark(String storyId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Ném lỗi 401
        }
        String userId = authentication.getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        Bookmark bookmark = Bookmark.builder().user(user).story(story).build();
        bookmark = bookmarkRepository.save(bookmark);
        return bookmarkMapper.toBookmarkResponse(bookmark);
    }

    public void deleteBookmark(String storyId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Ném lỗi 401
        }
        String userId = authentication.getName();
        bookmarkRepository.deleteByUserIdAndStoryId(userId, storyId);
    }
}

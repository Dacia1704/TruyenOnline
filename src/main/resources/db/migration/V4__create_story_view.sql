-- ==========================================================
-- Cập nhật bảng reading_history
-- Thêm phân loại STORY / CHAPTER, cho phép chapter_id NULL
-- ==========================================================

-- 1. Xóa các Unique Key cũ không còn phù hợp
ALTER TABLE reading_history
    DROP INDEX uq_user_story,
    DROP INDEX uq_session_story;

-- 2. Sửa cột chapter_id cho phép NULL (để lưu lịch sử của Story)
ALTER TABLE reading_history
    MODIFY COLUMN chapter_id CHAR(36) DEFAULT NULL;

-- 3. Thêm cột type
ALTER TABLE reading_history
    ADD COLUMN type ENUM('STORY', 'CHAPTER') NOT NULL AFTER session_id;

-- 4. Thêm lại Unique Key mới bao gồm cả cột type
ALTER TABLE reading_history
    ADD CONSTRAINT uq_user_story_type UNIQUE (user_id, story_id, type),
    ADD CONSTRAINT uq_session_story_type UNIQUE (session_id, story_id, type);


-- ==========================================================
-- Cập nhật bảng comments
-- Thêm phân loại STORY / CHAPTER
-- ==========================================================

-- 1. Xóa Check Constraint cũ
ALTER TABLE comments
    DROP CHECK chk_comment_target;

-- 2. Thêm cột type
ALTER TABLE comments
    ADD COLUMN type ENUM('STORY', 'CHAPTER') NOT NULL AFTER user_id;

-- 3. Thêm Check Constraint mới ràng buộc logic theo type
ALTER TABLE comments
    ADD CONSTRAINT chk_comment_target CHECK (
        (type = 'STORY' AND story_id IS NOT NULL) OR
        (type = 'CHAPTER' AND chapter_id IS NOT NULL)
    );
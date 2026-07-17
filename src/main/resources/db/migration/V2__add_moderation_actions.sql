-- ==========================================================
-- Create moderation_actions table
-- ==========================================================

CREATE TABLE moderation_actions
(
    id CHAR(36) NOT NULL,

    object_id CHAR(36) NOT NULL,

    object_type ENUM (
        'STORY',
        'CHAPTER',
        'COMMENT',
        'USER'
    ) NOT NULL,

    admin_id CHAR(36) NOT NULL,

    action_type ENUM (
        'BAN',
        'UNBAN'
    ) NOT NULL,

    violation_type ENUM (
        'COPYRIGHT',
        'PORNOGRAPHY',
        'VIOLENCE',
        'SPAM',
        'HARASSMENT',
        'OTHER'
    ) NOT NULL,

    reason TEXT NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_moderation_admin
        FOREIGN KEY (admin_id)
        REFERENCES users (id)
);

CREATE INDEX idx_moderation_object
    ON moderation_actions(object_type, object_id);

CREATE INDEX idx_moderation_admin
    ON moderation_actions(admin_id);


-- ==========================================================
-- Stories
-- ==========================================================

ALTER TABLE stories
    ADD COLUMN is_banned BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN current_moderation_id CHAR(36) NULL;

ALTER TABLE stories
    ADD CONSTRAINT fk_story_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);


-- ==========================================================
-- Chapters
-- ==========================================================

ALTER TABLE chapters
    ADD COLUMN is_banned BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN current_moderation_id CHAR(36) NULL;

ALTER TABLE chapters
    ADD CONSTRAINT fk_chapter_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);


-- ==========================================================
-- Comments
-- ==========================================================

ALTER TABLE comments
    ADD COLUMN is_banned BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN current_moderation_id CHAR(36) NULL;

ALTER TABLE comments
    ADD CONSTRAINT fk_comment_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);


-- ==========================================================
-- Users
-- ==========================================================

ALTER TABLE users
    ADD COLUMN is_banned BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN current_moderation_id CHAR(36) NULL;

ALTER TABLE users
    ADD CONSTRAINT fk_user_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);
-- ============================================================
-- TruyệnOnline — V3__banner_and_ban_appeal.sql
-- Banner Manager + Ban Appeal
-- MySQL 8.0 | utf8mb4 | UUID = CHAR(36)
-- ============================================================

USE truyenonline;

-- ============================================================
-- BANNERS
-- ============================================================
CREATE TABLE banners (
    id              CHAR(36) PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    banner_url      VARCHAR(500) NOT NULL,
    link_url        VARCHAR(500) NULL,

    position        ENUM(
                        'HOME_HERO',
                        'POPUP'
                    ) NOT NULL DEFAULT 'HOME_HERO',

    sort_order      INT NOT NULL DEFAULT 1,

    is_active       TINYINT(1) NOT NULL DEFAULT 1,

    click_count     BIGINT NOT NULL DEFAULT 0,

    created_by      CHAR(36) NULL,

    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                        ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_banner_creator
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    INDEX idx_banner_position_active
        (position, is_active, sort_order),

    INDEX idx_banner_created_by
        (created_by)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BAN APPEALS
-- ============================================================
CREATE TABLE ban_appeals (
    id                     CHAR(36) PRIMARY KEY,

    user_id                CHAR(36) NOT NULL,

    moderation_action_id   CHAR(36) NOT NULL,

    content                TEXT NOT NULL,

    status ENUM(
        'PENDING',
        'APPROVED',
        'REJECTED'
    ) NOT NULL DEFAULT 'PENDING',

    reviewer_id            CHAR(36) NULL,

    reviewer_note          TEXT NULL,

    resolved_at            DATETIME(3) NULL,

    created_at             DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    updated_at             DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                               ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_appeal_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_appeal_moderation
        FOREIGN KEY (moderation_action_id)
        REFERENCES moderation_actions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_appeal_reviewer
        FOREIGN KEY (reviewer_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    -- Mỗi lệnh ban chỉ được tạo một đơn khiếu nại
    UNIQUE KEY uq_appeal_moderation
        (moderation_action_id),

    INDEX idx_appeal_user_id
        (user_id),

    INDEX idx_appeal_status_created
        (status, created_at DESC)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BAN APPEAL ATTACHMENTS
-- ============================================================
CREATE TABLE ban_appeal_attachments (
    id                  CHAR(36) PRIMARY KEY,

    appeal_id           CHAR(36) NOT NULL,

    attachment_url      VARCHAR(500) NOT NULL,

    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                            ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_appeal_attachment_appeal
        FOREIGN KEY (appeal_id)
        REFERENCES ban_appeals(id)
        ON DELETE CASCADE,

    INDEX idx_appeal_attachment_appeal
        (appeal_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
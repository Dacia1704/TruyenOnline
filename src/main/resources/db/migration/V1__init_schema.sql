-- ============================================================
-- TruyệnOnline — schema_merged.sql
-- Bản HỢP NHẤT toàn bộ V1 → V5 (dùng cho khởi tạo DB mới)
-- MySQL 8.0 | utf8mb4 | UUID = CHAR(36) sinh bởi JPA
-- ============================================================

CREATE DATABASE IF NOT EXISTS truyenonline
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE truyenonline;

-- ============================================================
-- [RBAC] BẢNG ROLES
-- ============================================================
CREATE TABLE roles (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  UNIQUE NOT NULL,  -- 'ADMIN' | 'UPLOADER' | 'USER'
    description VARCHAR(255),
    is_default  TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                             ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO roles (name, description, is_default) VALUES
    ('ADMIN',    'Toàn quyền hệ thống',                         0),
    ('UPLOADER', 'Upload và quản lý truyện của mình',           0),
    ('USER',     'Người dùng thường — đọc truyện, mua Premium', 1);


-- ============================================================
-- [RBAC] BẢNG PERMISSIONS (đã gộp permission mới từ V5)
-- ============================================================
CREATE TABLE permissions (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,  -- pattern: resource:action
    resource    VARCHAR(50)  NOT NULL,
    action      VARCHAR(50)  NOT NULL,
    description VARCHAR(255),
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                             ON UPDATE CURRENT_TIMESTAMP(3),

    INDEX idx_perm_resource (resource)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO permissions (name, resource, action, description) VALUES
    ('user:read',            'user',         'read',         'Xem danh sách và chi tiết người dùng'),
    ('user:ban',             'user',         'ban',          'Khoá/mở tài khoản người dùng'),
    ('user:manage_roles',    'user',         'manage_roles', 'Gán/thu hồi role cho người dùng'),
    ('story:create',         'story',        'create',       'Tạo bộ truyện mới'),
    ('story:update_own',     'story',        'update_own',   'Sửa bộ truyện do mình upload'),
    ('story:update_any',     'story',        'update_any',   'Sửa bất kỳ bộ truyện nào'),
    ('story:delete_own',     'story',        'delete_own',   'Xoá bộ truyện do mình upload'),
    ('story:delete_any',     'story',        'delete_any',   'Xoá bất kỳ bộ truyện nào'),
    ('story:approve',        'story',        'approve',      'Duyệt / ẩn bộ truyện'),
    ('chapter:create',       'chapter',      'create',       'Thêm chapter mới'),
    ('chapter:update_own',   'chapter',      'update_own',   'Sửa chapter do mình tạo'),
    ('chapter:delete_own',   'chapter',      'delete_own',   'Xoá chapter do mình tạo'),
    ('chapter:read_premium', 'chapter',      'read_premium', 'Đọc chapter yêu cầu Premium'),
    ('comment:create',       'comment',      'create',       'Đăng bình luận'),
    ('comment:delete_own',   'comment',      'delete_own',   'Xoá bình luận của mình'),
    ('comment:delete_any',   'comment',      'delete_any',   'Xoá bất kỳ bình luận nào'),
    ('payment:manage',       'payment',      'manage',       'Xem và quản lý toàn bộ giao dịch'),
    ('subscription:buy',     'subscription', 'buy',          'Mua gói Premium'),
    ('author:create',            'author',        'create',        'Tạo tác giả mới'),
    ('author:update',            'author',        'update',        'Sửa thông tin tác giả'),
    ('author:delete',            'author',        'delete',        'Xoá tác giả'),
    ('subscription:manage_plans','subscription',  'manage_plans',  'Quản lý gói subscription (bật/tắt, sửa giá)');


-- ============================================================
-- [RBAC] BẢNG ROLE_PERMISSIONS (M-N)
-- ============================================================
CREATE TABLE role_permissions (
    role_id       INT         NOT NULL,
    permission_id INT         NOT NULL,
    created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                              ON UPDATE CURRENT_TIMESTAMP(3),

    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role       FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ADMIN: toàn quyền (tự động bao gồm cả permission mới vì permissions đã có sẵn đủ)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN';

-- UPLOADER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN (
    'story:create', 'story:update_own', 'story:delete_own',
    'chapter:create', 'chapter:update_own', 'chapter:delete_own',
    'chapter:read_premium',
    'comment:create', 'comment:delete_own',
    'subscription:buy',
    'author:create', 'author:update'
) WHERE r.name = 'UPLOADER';

-- USER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN (
    'chapter:read_premium',
    'comment:create', 'comment:delete_own',
    'subscription:buy'
) WHERE r.name = 'USER';


-- ============================================================
-- BẢNG USERS (đã gộp is_banned, current_moderation_id từ V2)
-- Lưu ý: FK current_moderation_id -> moderation_actions được thêm
-- ở cuối file, sau khi bảng moderation_actions được tạo.
-- ============================================================
CREATE TABLE users (
    id                     CHAR(36)    PRIMARY KEY,
    email                  VARCHAR(255) UNIQUE NOT NULL,
    username               VARCHAR(100) UNIQUE NOT NULL,
    password_hash          VARCHAR(255),                   -- NULL nếu OAuth
    avatar_url             VARCHAR(500),
    auth_provider          ENUM('LOCAL','GOOGLE') NOT NULL DEFAULT 'LOCAL',
    google_id              VARCHAR(100) UNIQUE,
    is_active              TINYINT(1)  NOT NULL DEFAULT 1,
    is_banned              BOOLEAN     NOT NULL DEFAULT FALSE,
    current_moderation_id  CHAR(36)    NULL,
    created_at             DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at             DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                       ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- [RBAC] BẢNG USER_ROLES (M-N)
-- ============================================================
CREATE TABLE user_roles (
    user_id     CHAR(36)    NOT NULL,
    role_id     INT         NOT NULL,
    assigned_by CHAR(36)    DEFAULT NULL,   -- UUID admin đã gán; NULL = tự gán lúc đăng ký
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                            ON UPDATE CURRENT_TIMESTAMP(3),

    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role     FOREIGN KEY (role_id)     REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_assigner FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_ur_user_id (user_id),
    INDEX idx_ur_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_social_accounts (
    id               CHAR(36)     PRIMARY KEY,
    user_id          CHAR(36)     NOT NULL,
    provider         VARCHAR(20)  NOT NULL, -- 'GOOGLE', 'FACEBOOK', v.v.
    provider_user_id VARCHAR(100) NOT NULL, -- ID trả về từ Google/Facebook
    created_at       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                      ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_social_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uq_provider_uid (provider, provider_user_id),
    INDEX idx_social_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG SUBSCRIPTION_PLANS (từ V5 — tạo trước vì subscriptions/
-- transactions cần FK tới bảng này)
-- ============================================================
CREATE TABLE subscription_plans (
    -- Dùng VARCHAR thay AUTO_INCREMENT để khớp với Enum Java (PREMIUM_1M, ...)
    code          VARCHAR(20)  PRIMARY KEY,             -- 'PREMIUM_1M' | 'PREMIUM_3M' | 'PREMIUM_1Y'
    name          VARCHAR(100) NOT NULL,                -- "Gói 1 Tháng"
    description   TEXT,
    price         BIGINT       NOT NULL,                -- VND, VD: 49000
    duration_days INT          NOT NULL,                -- số ngày sau khi kích hoạt
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,      -- Admin tắt gói thì set 0
    sort_order    TINYINT      NOT NULL DEFAULT 1,      -- thứ tự hiển thị trên UI
    created_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                               ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dữ liệu mặc định — khớp với SubscriptionPlan enum
INSERT INTO subscription_plans (code, name, description, price, duration_days, is_active, sort_order) VALUES
    ('PREMIUM_1M',  'Gói 1 Tháng',  'Đọc không giới hạn tất cả chapter VIP trong 30 ngày',   49000,  30,  1, 1),
    ('PREMIUM_3M',  'Gói 3 Tháng',  'Đọc không giới hạn tất cả chapter VIP trong 90 ngày',  129000,  90,  1, 2),
    ('PREMIUM_1Y',  'Gói 1 Năm',    'Đọc không giới hạn tất cả chapter VIP trong 365 ngày', 399000, 365,  1, 3);


-- ============================================================
-- BẢNG SUBSCRIPTIONS
-- (cột plan dùng thẳng VARCHAR(20) + FK subscription_plans,
--  vì đây là DB mới nên không cần bước MODIFY COLUMN như V5)
-- ============================================================
CREATE TABLE subscriptions (
    id          CHAR(36)    PRIMARY KEY,
    user_id     CHAR(36)    NOT NULL,
    plan        VARCHAR(20) NOT NULL,
    status      ENUM('ACTIVE','EXPIRED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    started_at  DATETIME(3) NOT NULL,
    expires_at  DATETIME(3) NOT NULL,
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                            ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_sub_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_plan   FOREIGN KEY (plan)     REFERENCES subscription_plans(code),
    INDEX idx_sub_user_id    (user_id),
    INDEX idx_sub_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG TRANSACTIONS (VNPay)
-- (subscription_plan dùng thẳng VARCHAR(20) + FK subscription_plans)
-- ============================================================
CREATE TABLE transactions (
    id                  CHAR(36)     PRIMARY KEY,
    user_id             CHAR(36)     NOT NULL,
    subscription_plan   VARCHAR(20)  NOT NULL,
    vnp_txn_ref         VARCHAR(100) UNIQUE NOT NULL,
    vnp_amount          BIGINT       NOT NULL,          -- số tiền × 100
    vnp_bank_code       VARCHAR(20),
    vnp_transaction_no  VARCHAR(100),
    vnp_response_code   VARCHAR(10),                   -- "00" = thành công
    vnp_secure_hash     VARCHAR(256),
    vnp_pay_date        VARCHAR(20),
    status              ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    ip_address          VARCHAR(45),
    raw_callback_data   JSON,                          -- toàn bộ params VNPay (debug)
    completed_at        DATETIME(3),
    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                     ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_txn_user    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_txn_plan    FOREIGN KEY (subscription_plan) REFERENCES subscription_plans(code),
    INDEX idx_txn_user_id     (user_id),
    INDEX idx_txn_vnp_txn_ref (vnp_txn_ref),
    INDEX idx_txn_status      (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG STORIES (đã gộp is_banned, current_moderation_id từ V2)
-- ============================================================
CREATE TABLE stories (
    id                     CHAR(36)     PRIMARY KEY,
    uploader_id             CHAR(36)     NOT NULL,
    title                   VARCHAR(500) NOT NULL,
    title_no_accent         VARCHAR(500) NOT NULL,
    slug                    VARCHAR(500) UNIQUE NOT NULL,
    description             TEXT,
    cover_image_url         VARCHAR(500),
    story_type              ENUM('NOVEL','MANGA') NOT NULL,
    status                  ENUM('ONGOING','COMPLETED','HIATUS','DROPPED') NOT NULL DEFAULT 'ONGOING',
    is_published            TINYINT(1)   NOT NULL DEFAULT 0,
    -- NULL = toàn bộ free | 0 = toàn bộ cần premium | N = N chap đầu free
    free_chapter_limit      INT          DEFAULT NULL,
    view_count              BIGINT       NOT NULL DEFAULT 0,
    is_banned               BOOLEAN      NOT NULL DEFAULT FALSE,
    current_moderation_id   CHAR(36)     NULL,
    created_at              DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at              DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                         ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_story_uploader FOREIGN KEY (uploader_id) REFERENCES users(id),
    INDEX idx_story_slug         (slug),
    INDEX idx_story_uploader_id  (uploader_id),
    INDEX idx_story_status       (status),
    INDEX idx_story_view_count   (view_count DESC),
    FULLTEXT INDEX ft_story_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG AUTHORS (từ V5)
-- Tách riêng khỏi users vì:
--   - Tác giả có thể là thực thể bên ngoài (chưa có tài khoản)
--   - 1 uploader có thể upload truyện của nhiều tác giả khác nhau
--   - Thông tin tác giả (bio, quốc gia) khác với thông tin user
-- ============================================================
CREATE TABLE authors (
    id             CHAR(36)     PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    name_no_accent VARCHAR(255) NOT NULL,
    slug           VARCHAR(255) UNIQUE NOT NULL,        -- dùng cho URL: /authors/nguyen-nhat-anh
    bio            TEXT,
    avatar_url     VARCHAR(500),
    country        VARCHAR(100),                        -- VD: "Việt Nam", "Nhật Bản"

    -- Liên kết tuỳ chọn tới tài khoản user (nếu tác giả tự upload)
    user_id        CHAR(36)     DEFAULT NULL,

    created_at     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_author_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_author_user_id  (user_id),
    INDEX idx_author_slug     (slug),
    FULLTEXT INDEX ft_author_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG STORY_AUTHORS (M-N, từ V5)
-- 1 truyện có thể có nhiều tác giả (đồng tác giả)
-- 1 tác giả có thể có nhiều truyện
-- ============================================================
CREATE TABLE story_authors (
    story_id   CHAR(36)     NOT NULL,
    author_id  CHAR(36)     NOT NULL,

    -- Thứ tự hiển thị: tác giả chính = 1, phụ = 2, 3...
    role       VARCHAR(50)  NOT NULL DEFAULT 'AUTHOR',
               -- VD: 'AUTHOR' | 'CO_AUTHOR' | 'ILLUSTRATOR' | 'TRANSLATOR'
    sort_order TINYINT      NOT NULL DEFAULT 1,

    created_at DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                            ON UPDATE CURRENT_TIMESTAMP(3),

    PRIMARY KEY (story_id, author_id),
    CONSTRAINT fk_sa_story  FOREIGN KEY (story_id)  REFERENCES stories(id)  ON DELETE CASCADE,
    CONSTRAINT fk_sa_author FOREIGN KEY (author_id) REFERENCES authors(id)  ON DELETE CASCADE,
    INDEX idx_sa_author_id  (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG GENRES
-- ============================================================
CREATE TABLE genres (
    id             INT          AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) UNIQUE NOT NULL,
    name_no_accent VARCHAR(100) UNIQUE NOT NULL,
    slug           VARCHAR(100) UNIQUE NOT NULL,
    created_at     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG STORY_GENRES (M-N)
-- ============================================================
CREATE TABLE story_genres (
    story_id    CHAR(36)    NOT NULL,
    genre_id    INT         NOT NULL,
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                            ON UPDATE CURRENT_TIMESTAMP(3),

    PRIMARY KEY (story_id, genre_id),
    CONSTRAINT fk_sg_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE,
    CONSTRAINT fk_sg_genre FOREIGN KEY (genre_id) REFERENCES genres(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG STORY_PUBLISH_REQUESTS
-- ============================================================
CREATE TABLE story_publish_requests (
    id              CHAR(36)     NOT NULL, -- Khóa chính cho mỗi lượt yêu cầu (UUID)
    story_id        CHAR(36)     NOT NULL, -- Truyện được yêu cầu

    requester_note  TEXT,                  -- Ghi chú của Uploader (Lý do, mô tả thêm...)
    reviewer_note   TEXT,                  -- Ghi chú của Admin (Lý do từ chối, lời khuyên...)
    reviewer_id     CHAR(36),              -- ID của Admin thực hiện duyệt (NULL nếu đang chờ duyệt)

    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING', -- Trạng thái: PENDING, APPROVED, REJECTED

    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

    PRIMARY KEY (id),
    CONSTRAINT fk_req_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE,
    CONSTRAINT fk_req_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG CHAPTERS (đã gộp is_banned, current_moderation_id từ V2)
-- ============================================================
CREATE TABLE chapters (
    id                     CHAR(36)      PRIMARY KEY,
    story_id                CHAR(36)      NOT NULL,
    chapter_number          DECIMAL(8,1)  NOT NULL,   -- hỗ trợ chap 1.5, 10.5
    title                   VARCHAR(500),
    title_no_accent         VARCHAR(500),
    is_published            TINYINT(1)    NOT NULL DEFAULT 0,
    view_count              BIGINT        NOT NULL DEFAULT 0,
    content                 LONGTEXT,                 -- NOVEL: nội dung văn bản
    page_count              INT,                      -- MANGA: số trang ảnh
    is_banned               BOOLEAN       NOT NULL DEFAULT FALSE,
    current_moderation_id   CHAR(36)      NULL,
    created_at              DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at              DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                          ON UPDATE CURRENT_TIMESTAMP(3),

    UNIQUE KEY uq_chapter    (story_id, chapter_number),
    CONSTRAINT fk_chap_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE,
    INDEX idx_chap_story_id  (story_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG CHAPTER_PAGES (Trang ảnh Manga)
-- ============================================================
CREATE TABLE chapter_pages (
    id            CHAR(36)     PRIMARY KEY,
    chapter_id    CHAR(36)     NOT NULL,
    page_number   INT          NOT NULL,
    image_url     VARCHAR(500) NOT NULL,      -- Cloudinary URL
    cloudinary_id VARCHAR(200),
    width         INT,
    height        INT,
    created_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                               ON UPDATE CURRENT_TIMESTAMP(3),

    UNIQUE KEY uq_page        (chapter_id, page_number),
    CONSTRAINT fk_page_chap   FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    INDEX idx_page_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG READING_HISTORY
-- (đã gộp thay đổi V4: thêm cột type, chapter_id cho phép NULL,
--  unique key mới theo type thay cho unique key cũ)
-- ============================================================
-- user_id != NULL  → người dùng đã đăng nhập
-- session_id != NULL → guest (cookie HttpOnly, Max-Age 30 ngày)
-- Khi guest đăng nhập: MERGE rồi xóa bản ghi guest
CREATE TABLE reading_history (
    id           CHAR(36)     PRIMARY KEY,
    user_id      CHAR(36)     DEFAULT NULL,
    session_id   VARCHAR(128) DEFAULT NULL,
    type         ENUM('STORY', 'CHAPTER') NOT NULL,
    story_id     CHAR(36)     NOT NULL,
    chapter_id   CHAR(36)     DEFAULT NULL,
    last_read_at DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    created_at   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                              ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_rh_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_rh_story   FOREIGN KEY (story_id)   REFERENCES stories(id)  ON DELETE CASCADE,
    CONSTRAINT fk_rh_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id),

    UNIQUE KEY uq_user_story_type    (user_id,    story_id, type),
    UNIQUE KEY uq_session_story_type (session_id, story_id, type),

    INDEX idx_rh_user_id    (user_id),
    INDEX idx_rh_session_id (session_id),
    INDEX idx_rh_last_read  (last_read_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG BOOKMARKS
-- ============================================================
CREATE TABLE bookmarks (
    user_id    CHAR(36)    NOT NULL,
    story_id   CHAR(36)    NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                           ON UPDATE CURRENT_TIMESTAMP(3),

    PRIMARY KEY (user_id, story_id),
    CONSTRAINT fk_bm_user  FOREIGN KEY (user_id)  REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_bm_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG COMMENTS
-- (đã gộp thay đổi V2: is_banned, current_moderation_id;
--  và V4: cột type + check constraint mới theo type)
-- ============================================================
CREATE TABLE comments (
    id                     CHAR(36)    PRIMARY KEY,
    user_id                 CHAR(36)    NOT NULL,
    type                    ENUM('STORY', 'CHAPTER') NOT NULL,
    story_id                CHAR(36)    DEFAULT NULL,
    chapter_id              CHAR(36)    DEFAULT NULL,
    content                 TEXT        NOT NULL,
    parent_id               CHAR(36)    DEFAULT NULL,     -- reply comment
    is_banned               BOOLEAN     NOT NULL DEFAULT FALSE,
    current_moderation_id   CHAR(36)    NULL,
    created_at              DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at              DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                        ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_cmt_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_cmt_story   FOREIGN KEY (story_id)   REFERENCES stories(id)  ON DELETE CASCADE,
    CONSTRAINT fk_cmt_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmt_parent  FOREIGN KEY (parent_id)  REFERENCES comments(id),

    CONSTRAINT chk_comment_target CHECK (
        (type = 'STORY' AND story_id IS NOT NULL) OR
        (type = 'CHAPTER' AND chapter_id IS NOT NULL)
    ),

    INDEX idx_cmt_story_id   (story_id),
    INDEX idx_cmt_chapter_id (chapter_id),
    INDEX idx_cmt_user_id    (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG MODERATION_ACTIONS (từ V2)
-- ============================================================
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


-- ============================================================
-- FK current_moderation_id -> moderation_actions
-- (phải thêm sau khi bảng moderation_actions đã tồn tại)
-- ============================================================
ALTER TABLE stories
    ADD CONSTRAINT fk_story_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);

ALTER TABLE chapters
    ADD CONSTRAINT fk_chapter_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);

ALTER TABLE comments
    ADD CONSTRAINT fk_comment_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);

ALTER TABLE users
    ADD CONSTRAINT fk_user_current_moderation
        FOREIGN KEY (current_moderation_id)
        REFERENCES moderation_actions(id);


-- ============================================================
-- BẢNG AUDIT_LOGS (từ V3)
-- ============================================================
CREATE TABLE audit_logs (
    id CHAR(36) NOT NULL,

    actor_id CHAR(36) NOT NULL,
    actor_role VARCHAR(30) NOT NULL,

    action VARCHAR(50) NOT NULL,

    object_type VARCHAR(30) NOT NULL,
    object_id CHAR(36) NULL,

    description TEXT NULL,

    old_value JSON NULL,
    new_value JSON NULL,

    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_audit_logs_actor
        FOREIGN KEY (actor_id)
        REFERENCES users(id)
);

CREATE INDEX idx_audit_logs_actor
    ON audit_logs(actor_id);

CREATE INDEX idx_audit_logs_object
    ON audit_logs(object_type, object_id);

CREATE INDEX idx_audit_logs_action
    ON audit_logs(action);

CREATE INDEX idx_audit_logs_created_at
    ON audit_logs(created_at);
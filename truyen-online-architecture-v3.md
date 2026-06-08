# 📚 TruyệnOnline — Bản Thiết Kế Kiến Trúc Hệ Thống v3 (CV Project)

> **Mục tiêu:** Web đọc truyện chữ & truyện tranh/manga trực tuyến, tích hợp OAuth2, JWT, VNPay, Cloud Storage — một dự án đủ "nặng đô" để gây ấn tượng với nhà tuyển dụng.
>
> **v3 thay đổi so với v2:**
> - 🔐 **RBAC đầy đủ** — Tách bảng `roles` và `permissions` thay thế `ENUM role` trên `users`
> - 🔗 **Bảng liên kết** `user_roles`, `role_permissions` — phân quyền linh hoạt, dễ mở rộng
> - 🛡️ **Permission granular** — Từng hành động (action) trên từng resource được kiểm soát độc lập
> - ✅ Giữ nguyên toàn bộ tính năng v2 (Free Tier, Guest History, VNPay, Cloudinary)

---

## 1. 🏗️ CHỐT TECH STACK & KIẾN TRÚC

### Quyết định kiến trúc: Modular Monolith + Docker Compose

**Khuyến nghị:** KHÔNG dùng Microservices thuần — dùng **Modular Monolith được đóng gói bằng Docker Compose**.

| Tiêu chí | Microservices | Modular Monolith (Khuyến nghị) |
|---|---|---|
| Độ phức tạp setup | Rất cao (K8s, Service Mesh, API Gateway) | Vừa phải (Docker Compose) |
| Thời gian hoàn thành | 6–12 tháng | 2–3 tháng |
| Ấn tượng với NTD entry/mid | ⚠️ Rủi ro bỏ dở | ✅ Hoàn chỉnh, có chiều sâu |
| Thể hiện kỹ năng Docker | ✅ | ✅ (Docker Compose multi-container) |
| Phù hợp CV junior–mid | ❌ Over-engineering | ✅ Sweet spot |

> 💡 **Tip CV:** Nêu rõ trong README: *"Thiết kế theo hướng Modular Monolith, phân tách module rõ ràng, sẵn sàng tách thành Microservices khi scale."* — NTD sẽ đánh giá cao sự tỉnh táo kỹ thuật này.

---

### Tech Stack đầy đủ

```
┌─────────────────────────────────────────────────────────┐
│                     TECH STACK                          │
├──────────────┬──────────────────────────────────────────┤
│ Backend      │ Java 21 + Spring Boot 3.x                │
│              │ Spring Security 6 (OAuth2 + JWT)         │
│              │ Spring Data JPA + QueryDSL               │
│              │ Spring Validation                        │
├──────────────┼──────────────────────────────────────────┤
│ Frontend     │ Next.js 14 (App Router)                  │
│              │ TypeScript + Tailwind CSS                │
│              │ TanStack Query (React Query)             │
│              │ NextAuth.js                              │
├──────────────┼──────────────────────────────────────────┤
│ Database     │ MySQL 8.0 (primary)                      │
│              │ Redis 7 (cache + session)                │
├──────────────┼──────────────────────────────────────────┤
│ Storage      │ Cloudinary (Free tier)                   │
├──────────────┼──────────────────────────────────────────┤
│ Auth         │ Google OAuth2 + JWT (Access + Refresh)   │
├──────────────┼──────────────────────────────────────────┤
│ Payment      │ VNPay (Sandbox → Production)             │
├──────────────┼──────────────────────────────────────────┤
│ DevOps       │ Docker + Docker Compose                  │
│              │ Nginx (reverse proxy)                    │
│              │ GitHub Actions (CI/CD)                   │
└──────────────┴──────────────────────────────────────────┘
```

---

## 2. 👥 THIẾT KẾ ROLE & PERMISSION — v3

### So sánh kiến trúc phân quyền v2 vs v3

| Tiêu chí | v2 — ENUM trên `users` | v3 — Bảng RBAC tách biệt |
|---|---|---|
| Thêm role mới | Cần ALTER TABLE (downtime) | INSERT vào `roles` |
| Gán nhiều role cho 1 user | ❌ Không thể | ✅ `user_roles` M-N |
| Kiểm soát quyền chi tiết | ❌ Hardcode trong code | ✅ `permissions` per resource:action |
| Audit ai có quyền gì | ❌ Khó trace | ✅ Query trực tiếp |
| Spring Security integration | `hasRole("ADMIN")` | `hasAuthority("story:approve")` |
| Phức tạp implement | Thấp | Trung bình (xứng đáng!) |

---

### Thiết kế Role mặc định

| Role | Mô tả |
|---|---|
| `ADMIN` | Toàn quyền hệ thống |
| `UPLOADER` | Upload và quản lý truyện của mình |
| `USER` | Đọc truyện, mua Premium, comment |
| `MODERATOR` | Duyệt nội dung, ẩn comment — có thể thêm sau |

> 💡 Mỗi user mặc định có role `USER`. Có thể gán thêm `UPLOADER` mà không mất `USER`.

---

### Ma trận Permission mặc định

| Permission | `ADMIN` | `MODERATOR` | `UPLOADER` | `USER` |
|---|:---:|:---:|:---:|:---:|
| `user:read` | ✅ | ✅ | ❌ | ❌ |
| `user:ban` | ✅ | ❌ | ❌ | ❌ |
| `user:manage_roles` | ✅ | ❌ | ❌ | ❌ |
| `story:create` | ✅ | ❌ | ✅ | ❌ |
| `story:update_own` | ✅ | ❌ | ✅ | ❌ |
| `story:update_any` | ✅ | ❌ | ❌ | ❌ |
| `story:delete_own` | ✅ | ❌ | ✅ | ❌ |
| `story:delete_any` | ✅ | ❌ | ❌ | ❌ |
| `story:approve` | ✅ | ✅ | ❌ | ❌ |
| `chapter:create` | ✅ | ❌ | ✅ | ❌ |
| `chapter:update_own` | ✅ | ❌ | ✅ | ❌ |
| `chapter:delete_own` | ✅ | ❌ | ✅ | ❌ |
| `chapter:read_premium` | ✅ | ✅ | ✅ (bộ mình) | ✅ (nếu Premium) |
| `comment:create` | ✅ | ✅ | ✅ | ✅ |
| `comment:delete_own` | ✅ | ✅ | ✅ | ✅ |
| `comment:delete_any` | ✅ | ✅ | ❌ | ❌ |
| `payment:manage` | ✅ | ❌ | ❌ | ❌ |
| `subscription:buy` | ✅ | ✅ | ✅ | ✅ |

---

## 3. 💾 DATABASE SCHEMA v3 (MySQL 8)

### ERD Overview

```
users ──────────── user_roles ──────────── roles
                                              │
                                        role_permissions
                                              │
                                         permissions

users ──────┬────────────── subscriptions
            ├────────────── transactions
            ├────────────── stories ─────────────┬── story_genres ── genres
            │               │ free_chapter_limit  └── chapters ──────── chapter_pages
            ├────────────── reading_history (user_id OR session_id)
            ├────────────── bookmarks
            └────────────── comments

guest (no login) ── session_id ── reading_history
```

---

### Schema chi tiết (MySQL 8 Syntax)

```sql
-- MySQL 8 — v3 RBAC Edition
-- Chạy: mysql -u root -p truyenonline < init.sql

CREATE DATABASE IF NOT EXISTS truyenonline
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE truyenonline;

-- ============================================
-- [RBAC] BẢNG ROLES
-- ============================================
-- Lưu danh sách các role trong hệ thống.
-- Seed sẵn 4 role: ADMIN, MODERATOR, UPLOADER, USER
CREATE TABLE roles (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  UNIQUE NOT NULL,    -- 'ADMIN', 'UPLOADER', 'USER', 'MODERATOR'
    description VARCHAR(255),                    -- Mô tả để hiển thị trên Admin UI
    is_default  TINYINT(1)   NOT NULL DEFAULT 0, -- 1 = tự động gán khi user mới đăng ký
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed data
INSERT INTO roles (name, description, is_default) VALUES
    ('ADMIN',     'Toàn quyền hệ thống',                               0),
    ('MODERATOR', 'Duyệt nội dung, quản lý comment',                   0),
    ('UPLOADER',  'Upload và quản lý truyện',                          0),
    ('USER',      'Người dùng thường — đọc truyện, mua Premium',       1);


-- ============================================
-- [RBAC] BẢNG PERMISSIONS
-- ============================================
-- Lưu danh sách quyền theo cấu trúc: resource:action
-- Ví dụ: story:approve, chapter:create, user:ban
CREATE TABLE permissions (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,    -- 'story:approve', 'chapter:create', ...
    resource    VARCHAR(50)  NOT NULL,           -- 'story', 'chapter', 'user', 'comment', 'payment'
    action      VARCHAR(50)  NOT NULL,           -- 'create', 'read', 'update_own', 'delete_any', 'approve', ...
    description VARCHAR(255),

    INDEX idx_perm_resource (resource)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed data — toàn bộ permissions trong hệ thống
INSERT INTO permissions (name, resource, action, description) VALUES
    -- User management
    ('user:read',         'user', 'read',         'Xem danh sách và chi tiết người dùng'),
    ('user:ban',          'user', 'ban',           'Khoá/mở tài khoản người dùng'),
    ('user:manage_roles', 'user', 'manage_roles',  'Gán/thu hồi role cho người dùng'),

    -- Story management
    ('story:create',      'story', 'create',      'Tạo bộ truyện mới'),
    ('story:update_own',  'story', 'update_own',  'Sửa bộ truyện do mình upload'),
    ('story:update_any',  'story', 'update_any',  'Sửa bất kỳ bộ truyện nào'),
    ('story:delete_own',  'story', 'delete_own',  'Xoá bộ truyện do mình upload'),
    ('story:delete_any',  'story', 'delete_any',  'Xoá bất kỳ bộ truyện nào'),
    ('story:approve',     'story', 'approve',     'Duyệt/ẩn bộ truyện'),

    -- Chapter management
    ('chapter:create',      'chapter', 'create',      'Thêm chapter mới'),
    ('chapter:update_own',  'chapter', 'update_own',  'Sửa chapter do mình tạo'),
    ('chapter:delete_own',  'chapter', 'delete_own',  'Xoá chapter do mình tạo'),
    ('chapter:read_premium','chapter', 'read_premium','Đọc chapter yêu cầu Premium'),

    -- Comment management
    ('comment:create',     'comment', 'create',     'Đăng bình luận'),
    ('comment:delete_own', 'comment', 'delete_own', 'Xoá bình luận của mình'),
    ('comment:delete_any', 'comment', 'delete_any', 'Xoá bất kỳ bình luận nào'),

    -- Payment & Subscription
    ('payment:manage',    'payment',      'manage',    'Xem và quản lý toàn bộ giao dịch'),
    ('subscription:buy',  'subscription', 'buy',       'Mua gói Premium');


-- ============================================
-- [RBAC] BẢNG ROLE_PERMISSIONS (M-N)
-- ============================================
-- Gán permissions cho roles.
-- Đây là nơi định nghĩa "ADMIN được làm gì", "UPLOADER được làm gì"...
CREATE TABLE role_permissions (
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,

    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role       FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed: ADMIN — toàn quyền
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN';

-- Seed: MODERATOR
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN (
    'user:read',
    'story:approve',
    'chapter:read_premium',
    'comment:create', 'comment:delete_own', 'comment:delete_any',
    'subscription:buy'
) WHERE r.name = 'MODERATOR';

-- Seed: UPLOADER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN (
    'story:create', 'story:update_own', 'story:delete_own',
    'chapter:create', 'chapter:update_own', 'chapter:delete_own',
    'chapter:read_premium',
    'comment:create', 'comment:delete_own',
    'subscription:buy'
) WHERE r.name = 'UPLOADER';

-- Seed: USER (thường)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN (
    'comment:create', 'comment:delete_own',
    'subscription:buy'
) WHERE r.name = 'USER';


-- ============================================
-- BẢNG USERS  (v3 — bỏ cột role ENUM)
-- ============================================
CREATE TABLE users (
    id              CHAR(36)     PRIMARY KEY,
    email           VARCHAR(255) UNIQUE NOT NULL,
    username        VARCHAR(100) UNIQUE NOT NULL,
    password_hash   VARCHAR(255),                   -- NULL nếu đăng nhập OAuth
    avatar_url      VARCHAR(500),
    -- ★ v3: KHÔNG CÒN cột role ENUM — phân quyền qua user_roles
    auth_provider   ENUM('LOCAL','GOOGLE') NOT NULL DEFAULT 'LOCAL',
    google_id       VARCHAR(100) UNIQUE,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                 ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- [RBAC] BẢNG USER_ROLES (M-N)
-- ============================================
-- Gán roles cho users. Một user có thể có nhiều role.
-- Ví dụ: user vừa là UPLOADER vừa là USER (mặc định).
CREATE TABLE user_roles (
    user_id     CHAR(36) NOT NULL,
    role_id     INT      NOT NULL,
    assigned_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    assigned_by CHAR(36) DEFAULT NULL,   -- UUID của Admin đã gán (NULL = tự gán lúc đăng ký)

    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id)     REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_assigner FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_ur_user_id (user_id),
    INDEX idx_ur_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ⚙️ Trigger: Tự động gán role mặc định (is_default = 1) khi tạo user mới
DELIMITER $$
CREATE TRIGGER trg_assign_default_roles
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO user_roles (user_id, role_id)
    SELECT NEW.id, id FROM roles WHERE is_default = 1;
END$$
DELIMITER ;


-- ============================================
-- BẢNG SUBSCRIPTIONS (Gói Premium)
-- ============================================
CREATE TABLE subscriptions (
    id          CHAR(36)     PRIMARY KEY,
    user_id     CHAR(36)     NOT NULL,
    plan        ENUM('FREE','PREMIUM_1M','PREMIUM_3M','PREMIUM_1Y') NOT NULL,
    status      ENUM('ACTIVE','EXPIRED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    started_at  DATETIME(3)  NOT NULL,
    expires_at  DATETIME(3)  NOT NULL,
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_sub_user_id  (user_id),
    INDEX idx_sub_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG TRANSACTIONS (VNPay)
-- ============================================
CREATE TABLE transactions (
    id                  CHAR(36)     PRIMARY KEY,
    user_id             CHAR(36)     NOT NULL,
    subscription_plan   ENUM('PREMIUM_1M','PREMIUM_3M','PREMIUM_1Y') NOT NULL,

    vnp_txn_ref         VARCHAR(100) UNIQUE NOT NULL,
    vnp_amount          BIGINT       NOT NULL,
    vnp_bank_code       VARCHAR(20),
    vnp_transaction_no  VARCHAR(100),
    vnp_response_code   VARCHAR(10),
    vnp_secure_hash     VARCHAR(256),
    vnp_pay_date        VARCHAR(20),

    status              ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    ip_address          VARCHAR(45),
    raw_callback_data   JSON,

    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                     ON UPDATE CURRENT_TIMESTAMP(3),
    completed_at        DATETIME(3),

    CONSTRAINT fk_txn_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_txn_user_id    (user_id),
    INDEX idx_txn_vnp_txn_ref (vnp_txn_ref),
    INDEX idx_txn_status     (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG STORIES
-- ============================================
CREATE TABLE stories (
    id                  CHAR(36)     PRIMARY KEY,
    uploader_id         CHAR(36)     NOT NULL,
    title               VARCHAR(500) NOT NULL,
    slug                VARCHAR(500) UNIQUE NOT NULL,
    description         TEXT,
    cover_image_url     VARCHAR(500),
    story_type          ENUM('NOVEL','MANGA') NOT NULL,
    status              ENUM('ONGOING','COMPLETED','HIATUS','DROPPED') NOT NULL DEFAULT 'ONGOING',
    is_published        TINYINT(1)   NOT NULL DEFAULT 0,

    -- Cấu hình Free Tier per bộ truyện (giữ nguyên từ v2)
    -- NULL = toàn bộ free | 0 = toàn bộ cần premium | N = N chap đầu free
    free_chapter_limit  INT          DEFAULT NULL,

    view_count          BIGINT       NOT NULL DEFAULT 0,
    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                     ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_story_uploader FOREIGN KEY (uploader_id) REFERENCES users(id),
    INDEX idx_story_slug         (slug),
    INDEX idx_story_uploader_id  (uploader_id),
    INDEX idx_story_status       (status),
    INDEX idx_story_view_count   (view_count DESC),
    FULLTEXT INDEX ft_story_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG GENRES & STORY_GENRES (M-N)
-- ============================================
CREATE TABLE genres (
    id      INT          AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) UNIQUE NOT NULL,
    slug    VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE story_genres (
    story_id    CHAR(36) NOT NULL,
    genre_id    INT      NOT NULL,
    PRIMARY KEY (story_id, genre_id),
    CONSTRAINT fk_sg_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE,
    CONSTRAINT fk_sg_genre FOREIGN KEY (genre_id) REFERENCES genres(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG CHAPTERS
-- ============================================
CREATE TABLE chapters (
    id              CHAR(36)       PRIMARY KEY,
    story_id        CHAR(36)       NOT NULL,
    chapter_number  DECIMAL(8,1)   NOT NULL,
    title           VARCHAR(500),
    is_published    TINYINT(1)     NOT NULL DEFAULT 0,
    view_count      BIGINT         NOT NULL DEFAULT 0,
    content         LONGTEXT,       -- NOVEL
    page_count      INT,            -- MANGA
    created_at      DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                   ON UPDATE CURRENT_TIMESTAMP(3),

    UNIQUE KEY uq_chapter (story_id, chapter_number),
    CONSTRAINT fk_chap_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE,
    INDEX idx_chap_story_id (story_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG CHAPTER_PAGES
-- ============================================
CREATE TABLE chapter_pages (
    id              CHAR(36)     PRIMARY KEY,
    chapter_id      CHAR(36)     NOT NULL,
    page_number     INT          NOT NULL,
    image_url       VARCHAR(500) NOT NULL,
    cloudinary_id   VARCHAR(200),
    width           INT,
    height          INT,

    UNIQUE KEY uq_page (chapter_id, page_number),
    CONSTRAINT fk_page_chap FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    INDEX idx_page_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG READING_HISTORY
-- ============================================
CREATE TABLE reading_history (
    id              CHAR(36)     PRIMARY KEY,
    user_id         CHAR(36)     DEFAULT NULL,
    session_id      VARCHAR(128) DEFAULT NULL,
    story_id        CHAR(36)     NOT NULL,
    chapter_id      CHAR(36)     NOT NULL,
    last_read_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_rh_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_rh_story   FOREIGN KEY (story_id)   REFERENCES stories(id)  ON DELETE CASCADE,
    CONSTRAINT fk_rh_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id),

    UNIQUE KEY uq_user_story    (user_id,    story_id),
    UNIQUE KEY uq_session_story (session_id, story_id),

    INDEX idx_rh_user_id    (user_id),
    INDEX idx_rh_session_id (session_id),
    INDEX idx_rh_last_read  (last_read_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG BOOKMARKS
-- ============================================
CREATE TABLE bookmarks (
    user_id     CHAR(36)    NOT NULL,
    story_id    CHAR(36)    NOT NULL,
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (user_id, story_id),
    CONSTRAINT fk_bm_user  FOREIGN KEY (user_id)  REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_bm_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================
-- BẢNG COMMENTS
-- ============================================
CREATE TABLE comments (
    id          CHAR(36)     PRIMARY KEY,
    user_id     CHAR(36)     NOT NULL,
    story_id    CHAR(36)     DEFAULT NULL,
    chapter_id  CHAR(36)     DEFAULT NULL,
    content     TEXT         NOT NULL,
    parent_id   CHAR(36)     DEFAULT NULL,
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_cmt_user    FOREIGN KEY (user_id)    REFERENCES users(id)     ON DELETE CASCADE,
    CONSTRAINT fk_cmt_story   FOREIGN KEY (story_id)   REFERENCES stories(id)   ON DELETE CASCADE,
    CONSTRAINT fk_cmt_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id)  ON DELETE CASCADE,
    CONSTRAINT fk_cmt_parent  FOREIGN KEY (parent_id)  REFERENCES comments(id),

    CONSTRAINT chk_comment_target CHECK (story_id IS NOT NULL OR chapter_id IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

### ERD Diagram v3

```
                    ┌─────────────┐
                    │    roles    │
                    │─────────────│
                    │ id (PK)     │
                    │ name        │
                    │ description │
                    │ is_default  │
                    └──────┬──────┘
                           │ M
               ┌───────────┴───────────┐
               │                       │
         ┌─────┴──────┐        ┌───────┴────────┐
         │ user_roles │        │role_permissions│
         │────────────│        │────────────────│
         │ user_id(FK)│        │ role_id  (FK)  │
         │ role_id(FK)│        │ perm_id  (FK)  │
         │ assigned_at│        └───────┬────────┘
         │ assigned_by│                │ M
         └─────┬──────┘                │
               │ M              ┌──────┴──────┐
               │                │ permissions │
         ┌─────┴──────┐         │─────────────│
         │   users    │         │ id (PK)     │
         │────────────│         │ name        │
         │ id (PK)    │         │ resource    │
         │ email      │         │ action      │
         │ username   │         └─────────────┘
         │ password   │
         │ auth_prov  │
         └──────┬─────┘
                │
     ┌──────────┼──────────────────────┐
     │          │                      │
     ▼          ▼                      ▼
subscriptions  stories ──── chapters ──── chapter_pages
transactions    │ free_chapter_limit
                └─── story_genres ─── genres

users ──── reading_history (user_id OR session_id)
users ──── bookmarks
users ──── comments

guest ──── session_id ──── reading_history
```

---

## 4. 🔐 TÍCH HỢP SPRING SECURITY v3

### Load Permissions vào JWT

```java
// UserPrincipal.java — thay thế hasRole() bằng hasAuthority()
public class UserPrincipal implements UserDetails {

    private final String userId;
    private final String email;
    private final boolean premiumActive;
    private final Collection<GrantedAuthority> authorities; // permissions dạng "story:approve"

    public static UserPrincipal from(User user, List<String> permissionNames) {
        List<GrantedAuthority> authorities = permissionNames.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        // Thêm ROLE_ prefix để tương thích hasRole() nếu cần
        user.getRoles().forEach(role ->
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()))
        );

        return new UserPrincipal(user.getId(), user.getEmail(),
                                 user.isPremiumActive(), authorities);
    }
}
```

### Query Load User Permissions (Tối ưu — 1 query)

```java
// UserRepository.java
@Query("""
    SELECT DISTINCT p.name
    FROM User u
    JOIN u.roles r
    JOIN r.permissions p
    WHERE u.id = :userId
      AND u.isActive = true
    """)
List<String> findPermissionsByUserId(@Param("userId") String userId);
```

### JWT Token Payload v3

```java
// JwtService.java — encode permissions vào token
{
  "sub":         "user_uuid",
  "email":       "user@example.com",
  "roles":       ["USER", "UPLOADER"],           // Tên roles
  "permissions": ["story:create", "chapter:create", "story:update_own", ...],
  "isPremium":   false,
  "iat":         1234567890,
  "exp":         1234568790   // +15 phút
}

// ⚠️ Chú ý: Chỉ nhét permissions vào JWT nếu danh sách nhỏ (<20 items).
// Nếu nhiều permissions → chỉ lưu roles trong JWT,
// load permissions từ Redis cache khi cần check.
```

### Bảo vệ Endpoints với @PreAuthorize

```java
// StoryController.java
@RestController
@RequestMapping("/api/stories")
public class StoryController {

    // Chỉ UPLOADER và ADMIN mới tạo được truyện
    @PostMapping
    @PreAuthorize("hasAuthority('story:create')")
    public ResponseEntity<StoryResponse> createStory(...) { ... }

    // Duyệt truyện — chỉ ADMIN hoặc MODERATOR
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('story:approve')")
    public ResponseEntity<Void> approveStory(...) { ... }

    // Xoá truyện — phân biệt own vs any trong service layer
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('story:delete_own', 'story:delete_any')")
    public ResponseEntity<Void> deleteStory(@PathVariable String id,
                                            @AuthenticationPrincipal UserPrincipal user) {
        storyService.delete(id, user); // Service tự check _own vs _any
        return ResponseEntity.noContent().build();
    }
}

// AdminController.java — Quản lý roles
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('user:manage_roles')")
public class UserRoleController {

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> assignRole(@PathVariable String userId,
                                           @PathVariable int roleId,
                                           @AuthenticationPrincipal UserPrincipal admin) {
        userRoleService.assignRole(userId, roleId, admin.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> revokeRole(@PathVariable String userId,
                                           @PathVariable int roleId) {
        userRoleService.revokeRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
```

### Kiểm tra _own vs _any trong Service

```java
// StoryService.java
public void delete(String storyId, UserPrincipal user) {
    Story story = storyRepository.findByIdOrThrow(storyId);

    boolean canDeleteAny = user.hasAuthority("story:delete_any");
    boolean canDeleteOwn = user.hasAuthority("story:delete_own")
                           && story.getUploaderId().equals(user.getUserId());

    if (!canDeleteAny && !canDeleteOwn) {
        throw new AccessDeniedException("Bạn không có quyền xoá bộ truyện này");
    }

    storyRepository.delete(story);
}
```

### Redis Cache cho Permissions

```java
// PermissionCacheService.java
// Tránh query DB mỗi request — cache permissions theo userId
@Service
public class PermissionCacheService {

    private static final String CACHE_KEY = "perms:";
    private static final Duration TTL = Duration.ofMinutes(15);

    public Set<String> getUserPermissions(String userId) {
        String key = CACHE_KEY + userId;
        Set<String> cached = redisTemplate.opsForSet().members(key);
        if (cached != null && !cached.isEmpty()) return cached;

        // Cache miss → query DB
        List<String> perms = userRepository.findPermissionsByUserId(userId);
        redisTemplate.opsForSet().add(key, perms.toArray(String[]::new));
        redisTemplate.expire(key, TTL);
        return new HashSet<>(perms);
    }

    // Gọi khi Admin thay đổi role của user
    public void invalidate(String userId) {
        redisTemplate.delete(CACHE_KEY + userId);
    }
}
```

---

## 5. 🔓 FREE TIER ACCESS LOGIC (Giữ nguyên từ v2)

```
stories.free_chapter_limit = NULL  →  Toàn bộ truyện FREE
stories.free_chapter_limit = 0     →  Toàn bộ truyện cần PREMIUM
stories.free_chapter_limit = 20    →  20 chap đầu FREE, từ chap 21 cần PREMIUM
```

```java
// ChapterAccessService.java — v3: dùng hasAuthority thay hasRole
@Service
public class ChapterAccessService {

    public boolean canRead(Story story, int chapterRank, UserPrincipal user) {
        Integer freeLimit = story.getFreeChapterLimit();

        if (freeLimit == null) return true;          // Toàn bộ free
        if (chapterRank <= freeLimit) return true;   // Trong giới hạn free

        if (user == null) return false;              // Guest không có premium

        // Admin và Moderator đọc tất cả
        if (user.hasAuthority("chapter:read_premium") &&
            (user.hasRole("ADMIN") || user.hasRole("MODERATOR"))) return true;

        // Uploader đọc bộ của mình
        if (user.hasAuthority("chapter:read_premium") &&
            story.getUploaderId().equals(user.getUserId())) return true;

        // User Premium
        return user.hasAuthority("chapter:read_premium") && user.isPremiumActive();
    }
}
```

---

## 6. 👤 GUEST READING HISTORY (Giữ nguyên từ v2)

Xem chi tiết ở v2 — cơ chế `session_id` cookie, upsert, và merge khi đăng nhập không thay đổi.

---

## 7. ☁️ CLOUDINARY & 💳 VNPAY (Giữ nguyên từ v2)

Xem chi tiết ở v2 — không có thay đổi.

---

## 8. 🗂️ CẤU TRÚC PROJECT v3

### Backend (Spring Boot)

```
src/main/java/com/truyenonline/
├── config/
│   ├── SecurityConfig.java          # Bật @EnableMethodSecurity
│   ├── CloudinaryConfig.java
│   └── VNPayConfig.java
│
├── module/
│   ├── auth/
│   │   ├── AuthController.java
│   │   ├── AuthService.java
│   │   ├── JwtService.java          # Encode roles + permissions vào JWT
│   │   └── OAuth2UserService.java
│   │
│   ├── user/
│   │   ├── UserController.java
│   │   └── entity/
│   │       ├── User.java
│   │       ├── Role.java            # ★ Mới v3
│   │       ├── Permission.java      # ★ Mới v3
│   │       ├── UserRole.java        # ★ Mới v3
│   │       └── RolePermission.java  # ★ Mới v3
│   │
│   ├── admin/
│   │   └── UserRoleController.java  # ★ Mới v3 — Gán/thu hồi role
│   │
│   ├── story/
│   │   ├── StoryController.java
│   │   ├── StoryService.java        # delete() check _own vs _any
│   │   └── entity/Story.java
│   │
│   ├── chapter/
│   │   ├── ChapterController.java
│   │   ├── ChapterService.java
│   │   ├── ChapterAccessService.java # ★ v3: dùng hasAuthority
│   │   └── entity/{Chapter, ChapterPage}.java
│   │
│   ├── reading/
│   │   ├── ReadingHistoryController.java
│   │   ├── ReadingHistoryService.java
│   │   └── entity/ReadingHistory.java
│   │
│   ├── payment/
│   │   ├── PaymentController.java
│   │   ├── PaymentService.java
│   │   ├── VNPayUtil.java
│   │   └── entity/Transaction.java
│   │
│   └── subscription/
│       ├── SubscriptionService.java
│       └── entity/Subscription.java
│
└── shared/
    ├── exception/GlobalExceptionHandler.java
    ├── security/
    │   ├── JwtAuthFilter.java
    │   ├── UserPrincipal.java         # ★ v3: authorities từ permissions
    │   └── PermissionCacheService.java # ★ Mới v3 — Redis cache permissions
    ├── resolver/GuestSessionResolver.java
    └── util/SlugUtil.java
```

---

## 9. 🐳 DOCKER COMPOSE (Giữ nguyên từ v2)

```yaml
# docker-compose.yml — không thay đổi so với v2
version: '3.9'

services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/truyenonline?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
      - SPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver
      - SPRING_REDIS_HOST=redis
      - CLOUDINARY_URL=${CLOUDINARY_URL}
      - VNPAY_HASH_SECRET=${VNPAY_HASH_SECRET}
      - GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
      - GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy

  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    environment:
      - NEXT_PUBLIC_API_URL=http://nginx/api

  mysql:
    image: mysql:8.0
    volumes:
      - mysql_data:/var/lib/mysql
      - ./backend/src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/init.sql
    environment:
      - MYSQL_DATABASE=truyenonline
      - MYSQL_USER=${DB_USER}
      - MYSQL_PASSWORD=${DB_PASSWORD}
      - MYSQL_ROOT_PASSWORD=${DB_ROOT_PASSWORD}
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${DB_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - backend
      - frontend

volumes:
  mysql_data:
```

---

## 10. 🗓️ ROADMAP THỰC THI v3

### Phase 0 — Setup (Ngày 1–2)
- [ ] Tạo GitHub repo (monorepo: `/backend` + `/frontend`)
- [ ] Setup Docker Compose (MySQL + Redis + Backend shell)
- [ ] Tạo database schema v3 (chạy `init.sql` — bao gồm seed roles + permissions)
- [ ] Tạo Next.js project với TypeScript + Tailwind
- [ ] Setup Spring Boot project với dependencies
- [ ] Tạo file `.env.example`

### Phase 1 — Authentication + RBAC (Ngày 3–9)
- [ ] Implement User, Role, Permission entities + Repositories
- [ ] Trigger tự động gán role `USER` khi tạo tài khoản
- [ ] API đăng ký / đăng nhập — JWT chứa roles + permissions
- [ ] Tích hợp Google OAuth2
- [ ] **`PermissionCacheService`** — Redis cache permissions
- [ ] JWT Filter load permissions → `SecurityContext`
- [ ] Frontend: trang login/register + Google button

### Phase 2 — Core Features (Ngày 10–23)
- [ ] CRUD Stories — `@PreAuthorize("hasAuthority('story:create')")`
- [ ] CRUD Chapters — phân quyền _own vs _any trong Service
- [ ] Upload ảnh manga lên Cloudinary
- [ ] **`ChapterAccessService`** v3 — kiểm tra quyền đọc
- [ ] **Admin UI** — Gán/thu hồi role cho user (UserRoleController)
- [ ] **Lịch sử đọc Guest** — GuestSessionResolver + cookie
- [ ] Merge lịch sử Guest → User khi đăng nhập
- [ ] Frontend: danh sách truyện, chi tiết, reader
- [ ] Bookmark / Tủ truyện

### Phase 3 — Payment (Ngày 24–30)
- [ ] Tích hợp VNPay Sandbox
- [ ] API tạo URL thanh toán — `@PreAuthorize("hasAuthority('subscription:buy')")`
- [ ] Xử lý Return URL + IPN Callback
- [ ] Kích hoạt Subscription sau thanh toán
- [ ] Frontend: trang mua gói Premium, lịch sử giao dịch

### Phase 4 — Polish & Deploy (Ngày 31–37)
- [ ] Search truyện (MySQL FULLTEXT)
- [ ] Admin Dashboard — thống kê, duyệt truyện
- [ ] Rate limiting (Bucket4j)
- [ ] Viết README + Architecture diagram
- [ ] Deploy: Railway/Render + Vercel
- [ ] Record demo video cho CV

---

## 11. 🏆 CV HIGHLIGHTS — 5 Tính Năng "Sát Thủ" (v3 thêm #5)

### 🥇 #1: Secure VNPay Payment Flow với Idempotency *(giữ nguyên)*
> *"Tích hợp VNPay Payment Gateway, xử lý dual-callback (Return URL + IPN), implement idempotency check để tránh duplicate transactions, verify HMAC-SHA512 signature trên mỗi callback request."*

### 🥈 #2: Dual Authentication — OAuth2 + JWT với Refresh Token Rotation *(giữ nguyên)*
> *"Thiết kế authentication system hỗ trợ dual provider (Google OAuth2 + Local JWT), implement Refresh Token Rotation với Redis blacklist, bảo vệ chống token replay attack."*

### 🥉 #3: Dynamic Free Tier — Configurable per Story *(giữ nguyên)*
> *"Thiết kế Content Access Control linh hoạt: Uploader cấu hình `free_chapter_limit` per bộ truyện, quyền đọc được tính động tại Service layer, kết hợp với Premium subscription check."*

### 🎖️ #4: Guest Reading History với Seamless Merge *(giữ nguyên)*
> *"Implement Guest Session cho Reading History (cookie-based), xử lý merge lịch sử guest → tài khoản khi đăng nhập với conflict resolution."*

### 🆕 🏅 #5: RBAC đầy đủ — Role & Permission tách biệt

**Tại sao ấn tượng:**
Thay vì `role ENUM` đơn giản, hệ thống có bảng `roles`, `permissions`, `user_roles`, `role_permissions` theo mô hình RBAC chuẩn. Tích hợp với Spring Security `@PreAuthorize` ở method level, cache permissions trong Redis tránh N+1 query. Đây là pattern mà hầu hết hệ thống doanh nghiệp thực tế đều dùng.

**Cách nêu trong CV:**
> *"Thiết kế RBAC (Role-Based Access Control) với bảng `roles`/`permissions` tách biệt, hỗ trợ gán nhiều role per user, phân quyền granular theo `resource:action` pattern, tích hợp Spring Security `@PreAuthorize` + Redis cache để tối ưu performance."*

---

## 12. 📋 BIẾN MÔI TRƯỜNG (Giữ nguyên từ v2)

```bash
# .env.example

# MySQL
DB_USER=appuser
DB_PASSWORD=your_password_here
DB_ROOT_PASSWORD=your_root_password_here
DATABASE_URL=jdbc:mysql://localhost:3306/truyenonline

# Redis
REDIS_PASSWORD=your_redis_password

# JWT
JWT_SECRET=your_256bit_secret_key_here
JWT_EXPIRATION_MS=900000            # 15 phút
JWT_REFRESH_EXPIRATION_MS=604800000  # 7 ngày

# Guest Session
GUEST_SESSION_COOKIE_NAME=guest_session
GUEST_SESSION_MAX_AGE_DAYS=30

# Google OAuth2
GOOGLE_CLIENT_ID=xxxxx.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-xxxxx

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# VNPay
VNPAY_TMN_CODE=your_tmn_code
VNPAY_HASH_SECRET=your_hash_secret
VNPAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://localhost:3000/payment/return
VNPAY_IPN_URL=http://your-public-ip:8080/api/payment/vnpay-ipn
# ⚠️ VNPAY_IPN_URL phải là IP/domain public. Dùng ngrok khi dev local.
```

---

*Senior Software Architect Design | v3.0 | MySQL Edition + RBAC | Tháng 6/2025*

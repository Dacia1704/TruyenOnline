# 📚 TruyệnOnline — Bản Thiết Kế Kiến Trúc Hệ Thống v4 (CV Project)

> **Mục tiêu:** Web đọc truyện chữ & truyện tranh/manga trực tuyến, tích hợp OAuth2, JWT, VNPay, Cloud Storage — một dự án đủ "nặng đô" để gây ấn tượng với nhà tuyển dụng.
>
> **v4 thay đổi so với v3:**
> - 🎯 **Đơn giản hoá Role** — Chỉ còn 3 role: `ADMIN`, `UPLOADER`, `USER` (bỏ `MODERATOR`)
> - 🔐 **Vẫn giữ bảng RBAC** `roles` / `permissions` / `user_roles` / `role_permissions` — linh hoạt, đúng chuẩn
> - 🛡️ Quyền duyệt truyện và xóa comment bất kỳ chuyển về `ADMIN`
> - ✅ Giữ nguyên toàn bộ tính năng v3 (Free Tier, Guest History, VNPay, Cloudinary)

---

## 1. 🏗️ TECH STACK & KIẾN TRÚC

### Quyết định kiến trúc: Modular Monolith + Docker Compose

| Tiêu chí | Microservices | Modular Monolith (Khuyến nghị) |
|---|---|---|
| Độ phức tạp setup | Rất cao (K8s, Service Mesh, API Gateway) | Vừa phải (Docker Compose) |
| Thời gian hoàn thành | 6–12 tháng | 2–3 tháng |
| Ấn tượng với NTD entry/mid | ⚠️ Rủi ro bỏ dở | ✅ Hoàn chỉnh, có chiều sâu |
| Phù hợp CV junior–mid | ❌ Over-engineering | ✅ Sweet spot |

> 💡 **Tip CV:** *"Thiết kế theo hướng Modular Monolith, phân tách module rõ ràng, sẵn sàng tách thành Microservices khi scale."*

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

## 2. 👥 THIẾT KẾ ROLE & PERMISSION — v4

### 3 Role trong hệ thống

| Role | Mô tả | Gán tự động? |
|---|---|:---:|
| `ADMIN` | Toàn quyền hệ thống | ❌ (gán thủ công) |
| `UPLOADER` | Upload và quản lý truyện của mình | ❌ (gán thủ công) |
| `USER` | Đọc truyện, mua Premium, comment | ✅ (khi đăng ký) |

> Một user có thể giữ nhiều role cùng lúc. Ví dụ: một Uploader vừa có `USER` (mặc định) vừa được gán thêm `UPLOADER`.

---

### Ma trận Permission v4

| Permission | `ADMIN` | `UPLOADER` | `USER` | Mô tả |
|---|:---:|:---:|:---:|----|
| `user:read` | ✅ | ❌ | ❌ | Xem danh sách & chi tiết user |
| `user:ban` | ✅ | ❌ | ❌ | Khoá/mở tài khoản |
| `user:manage_roles` | ✅ | ❌ | ❌ | Gán/thu hồi role |
| `story:create` | ✅ | ✅ | ❌ | Tạo bộ truyện mới |
| `story:update_own` | ✅ | ✅ | ❌ | Sửa bộ truyện do mình upload |
| `story:update_any` | ✅ | ❌ | ❌ | Sửa bất kỳ bộ truyện nào |
| `story:delete_own` | ✅ | ✅ | ❌ | Xoá bộ truyện do mình upload |
| `story:delete_any` | ✅ | ❌ | ❌ | Xoá bất kỳ bộ truyện nào |
| `story:approve` | ✅ | ❌ | ❌ | Duyệt/ẩn bộ truyện |
| `chapter:create` | ✅ | ✅ | ❌ | Thêm chapter mới |
| `chapter:update_own` | ✅ | ✅ | ❌ | Sửa chapter do mình tạo |
| `chapter:delete_own` | ✅ | ✅ | ❌ | Xoá chapter do mình tạo |
| `chapter:read_premium` | ✅ | ✅ | ✅* | Đọc chapter yêu cầu Premium |
| `comment:create` | ✅ | ✅ | ✅ | Đăng bình luận |
| `comment:delete_own` | ✅ | ✅ | ✅ | Xoá bình luận của mình |
| `comment:delete_any` | ✅ | ❌ | ❌ | Xoá bất kỳ bình luận nào |
| `payment:manage` | ✅ | ❌ | ❌ | Xem toàn bộ giao dịch |
| `subscription:buy` | ✅ | ✅ | ✅ | Mua gói Premium |

> `chapter:read_premium` với `USER`: có permission nhưng Service layer còn kiểm tra thêm `subscription.status = ACTIVE`. UPLOADER đọc được chapter premium của **bộ truyện do mình upload**.

---

## 3. 💾 DATABASE SCHEMA v4 (MySQL 8)

### ERD Overview

```
roles ──────── role_permissions ──────── permissions
  │
user_roles
  │
users ──────┬──────── subscriptions
            ├──────── transactions
            ├──────── stories ────────────┬── story_genres ── genres
            │         │ free_chapter_limit └── chapters ──────── chapter_pages
            ├──────── reading_history (user_id OR session_id)
            ├──────── bookmarks
            └──────── comments

guest ── session_id ── reading_history
```

---

### Schema chi tiết (MySQL 8)

```sql
-- ============================================================
-- TruyệnOnline — init.sql  v4  (3 roles: ADMIN, UPLOADER, USER)
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
    is_default  TINYINT(1)   NOT NULL DEFAULT 0,  -- 1 = tự gán khi đăng ký
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO roles (name, description, is_default) VALUES
    ('ADMIN',    'Toàn quyền hệ thống',                         0),
    ('UPLOADER', 'Upload và quản lý truyện của mình',           0),
    ('USER',     'Người dùng thường — đọc truyện, mua Premium', 1);


-- ============================================================
-- [RBAC] BẢNG PERMISSIONS
-- ============================================================
CREATE TABLE permissions (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,  -- pattern: resource:action
    resource    VARCHAR(50)  NOT NULL,
    action      VARCHAR(50)  NOT NULL,
    description VARCHAR(255),

    INDEX idx_perm_resource (resource)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
    ('story:approve',     'story', 'approve',     'Duyệt / ẩn bộ truyện'),

    -- Chapter management
    ('chapter:create',       'chapter', 'create',       'Thêm chapter mới'),
    ('chapter:update_own',   'chapter', 'update_own',   'Sửa chapter do mình tạo'),
    ('chapter:delete_own',   'chapter', 'delete_own',   'Xoá chapter do mình tạo'),
    ('chapter:read_premium', 'chapter', 'read_premium', 'Đọc chapter yêu cầu Premium'),

    -- Comment management
    ('comment:create',     'comment', 'create',     'Đăng bình luận'),
    ('comment:delete_own', 'comment', 'delete_own', 'Xoá bình luận của mình'),
    ('comment:delete_any', 'comment', 'delete_any', 'Xoá bất kỳ bình luận nào'),

    -- Payment & Subscription
    ('payment:manage',   'payment',      'manage', 'Xem và quản lý toàn bộ giao dịch'),
    ('subscription:buy', 'subscription', 'buy',    'Mua gói Premium');


-- ============================================================
-- [RBAC] BẢNG ROLE_PERMISSIONS (M-N)
-- ============================================================
CREATE TABLE role_permissions (
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,

    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role       FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ADMIN: toàn quyền
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
    'subscription:buy'
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
-- BẢNG USERS
-- ============================================================
CREATE TABLE users (
    id              CHAR(36)     PRIMARY KEY,   -- UUID sinh bởi JPA
    email           VARCHAR(255) UNIQUE NOT NULL,
    username        VARCHAR(100) UNIQUE NOT NULL,
    password_hash   VARCHAR(255),               -- NULL nếu đăng nhập OAuth
    avatar_url      VARCHAR(500),
    auth_provider   ENUM('LOCAL','GOOGLE') NOT NULL DEFAULT 'LOCAL',
    google_id       VARCHAR(100) UNIQUE,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                 ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- [RBAC] BẢNG USER_ROLES (M-N)
-- ============================================================
CREATE TABLE user_roles (
    user_id     CHAR(36)    NOT NULL,
    role_id     INT         NOT NULL,
    assigned_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    assigned_by CHAR(36)    DEFAULT NULL,  -- UUID admin đã gán; NULL = tự gán lúc đăng ký

    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role     FOREIGN KEY (role_id)     REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_assigner FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_ur_user_id (user_id),
    INDEX idx_ur_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Trigger: tự động gán role USER (is_default = 1) khi tạo tài khoản mới
DELIMITER $$
CREATE TRIGGER trg_assign_default_roles
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO user_roles (user_id, role_id)
    SELECT NEW.id, id FROM roles WHERE is_default = 1;
END$$
DELIMITER ;


-- ============================================================
-- BẢNG SUBSCRIPTIONS (Gói Premium)
-- ============================================================
CREATE TABLE subscriptions (
    id          CHAR(36)     PRIMARY KEY,
    user_id     CHAR(36)     NOT NULL,
    plan        ENUM('FREE','PREMIUM_1M','PREMIUM_3M','PREMIUM_1Y') NOT NULL,
    status      ENUM('ACTIVE','EXPIRED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    started_at  DATETIME(3)  NOT NULL,
    expires_at  DATETIME(3)  NOT NULL,
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_sub_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_sub_user_id    (user_id),
    INDEX idx_sub_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG TRANSACTIONS (VNPay)
-- ============================================================
CREATE TABLE transactions (
    id                  CHAR(36)     PRIMARY KEY,
    user_id             CHAR(36)     NOT NULL,
    subscription_plan   ENUM('PREMIUM_1M','PREMIUM_3M','PREMIUM_1Y') NOT NULL,

    vnp_txn_ref         VARCHAR(100) UNIQUE NOT NULL,   -- Mã đơn hàng gửi VNPay
    vnp_amount          BIGINT       NOT NULL,           -- Số tiền × 100
    vnp_bank_code       VARCHAR(20),
    vnp_transaction_no  VARCHAR(100),
    vnp_response_code   VARCHAR(10),                    -- "00" = thành công
    vnp_secure_hash     VARCHAR(256),
    vnp_pay_date        VARCHAR(20),

    status              ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    ip_address          VARCHAR(45),
    raw_callback_data   JSON,                           -- Toàn bộ params VNPay (debug)

    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                     ON UPDATE CURRENT_TIMESTAMP(3),
    completed_at        DATETIME(3),

    CONSTRAINT fk_txn_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_txn_user_id     (user_id),
    INDEX idx_txn_vnp_txn_ref (vnp_txn_ref),
    INDEX idx_txn_status      (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG STORIES
-- ============================================================
CREATE TABLE stories (
    id                  CHAR(36)     PRIMARY KEY,
    uploader_id         CHAR(36)     NOT NULL,
    title               VARCHAR(500) NOT NULL,
    slug                VARCHAR(500) UNIQUE NOT NULL,
    description         TEXT,
    cover_image_url     VARCHAR(500),
    story_type          ENUM('NOVEL','MANGA') NOT NULL,
    status              ENUM('ONGOING','COMPLETED','HIATUS','DROPPED') NOT NULL DEFAULT 'ONGOING',
    is_published        TINYINT(1)   NOT NULL DEFAULT 0,  -- ADMIN duyệt mới publish

    -- Free Tier config per bộ truyện (do Uploader tự cấu hình)
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


-- ============================================================
-- BẢNG GENRES & STORY_GENRES (M-N)
-- ============================================================
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


-- ============================================================
-- BẢNG CHAPTERS
-- ============================================================
CREATE TABLE chapters (
    id              CHAR(36)     PRIMARY KEY,
    story_id        CHAR(36)     NOT NULL,
    chapter_number  DECIMAL(8,1) NOT NULL,  -- Hỗ trợ chap 1.5, 10.5
    title           VARCHAR(500),
    is_published    TINYINT(1)   NOT NULL DEFAULT 0,
    view_count      BIGINT       NOT NULL DEFAULT 0,
    content         LONGTEXT,               -- NOVEL: nội dung văn bản
    page_count      INT,                    -- MANGA: số trang ảnh
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                 ON UPDATE CURRENT_TIMESTAMP(3),

    UNIQUE KEY uq_chapter    (story_id, chapter_number),
    CONSTRAINT fk_chap_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE,
    INDEX idx_chap_story_id  (story_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Quyền đọc chapter được tính ĐỘNG theo stories.free_chapter_limit,
-- không lưu is_vip trên từng chapter.


-- ============================================================
-- BẢNG CHAPTER_PAGES (Trang ảnh Manga)
-- ============================================================
CREATE TABLE chapter_pages (
    id              CHAR(36)     PRIMARY KEY,
    chapter_id      CHAR(36)     NOT NULL,
    page_number     INT          NOT NULL,
    image_url       VARCHAR(500) NOT NULL,   -- Cloudinary URL
    cloudinary_id   VARCHAR(200),
    width           INT,
    height          INT,

    UNIQUE KEY uq_page       (chapter_id, page_number),
    CONSTRAINT fk_page_chap  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    INDEX idx_page_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG READING_HISTORY (Lịch sử đọc — User + Guest)
-- ============================================================
-- user_id != NULL  →  người dùng đã đăng nhập
-- session_id != NULL → guest (cookie HttpOnly)
-- Khi guest đăng nhập: MERGE guest → user rồi xóa bản ghi guest
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


-- ============================================================
-- BẢNG BOOKMARKS
-- ============================================================
CREATE TABLE bookmarks (
    user_id     CHAR(36)    NOT NULL,
    story_id    CHAR(36)    NOT NULL,
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (user_id, story_id),
    CONSTRAINT fk_bm_user  FOREIGN KEY (user_id)  REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_bm_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG COMMENTS
-- ============================================================
CREATE TABLE comments (
    id          CHAR(36)    PRIMARY KEY,
    user_id     CHAR(36)    NOT NULL,
    story_id    CHAR(36)    DEFAULT NULL,
    chapter_id  CHAR(36)    DEFAULT NULL,
    content     TEXT        NOT NULL,
    parent_id   CHAR(36)    DEFAULT NULL,  -- reply comment
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_cmt_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_cmt_story   FOREIGN KEY (story_id)   REFERENCES stories(id)  ON DELETE CASCADE,
    CONSTRAINT fk_cmt_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmt_parent  FOREIGN KEY (parent_id)  REFERENCES comments(id),

    CONSTRAINT chk_comment_target CHECK (story_id IS NOT NULL OR chapter_id IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 4. 🔐 SPRING SECURITY — Tích hợp RBAC v4

### Load Permissions vào UserPrincipal

```java
// UserPrincipal.java
public class UserPrincipal implements UserDetails {

    private final String userId;
    private final String email;
    private final boolean premiumActive;
    private final Collection<GrantedAuthority> authorities;

    public static UserPrincipal from(User user, List<String> permissionNames) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Permission-level authorities → dùng cho @PreAuthorize
        permissionNames.stream()
            .map(SimpleGrantedAuthority::new)
            .forEach(authorities::add);

        // Role-level authorities → tương thích hasRole() nếu cần
        user.getRoles().forEach(role ->
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()))
        );

        return new UserPrincipal(
            user.getId(), user.getEmail(),
            user.isPremiumActive(), authorities
        );
    }

    public boolean hasAuthority(String authority) {
        return authorities.stream()
            .anyMatch(a -> a.getAuthority().equals(authority));
    }
}
```

### Query load permissions — 1 truy vấn duy nhất

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

### JWT Payload v4

```java
// Access Token — permissions nhúng thẳng vào token
{
  "sub":         "user_uuid",
  "email":       "user@example.com",
  "roles":       ["USER", "UPLOADER"],
  "permissions": [
    "story:create", "story:update_own", "story:delete_own",
    "chapter:create", "chapter:update_own", "chapter:delete_own",
    "chapter:read_premium",
    "comment:create", "comment:delete_own",
    "subscription:buy"
  ],
  "isPremium":   false,
  "iat":         1234567890,
  "exp":         1234568790   // +15 phút
}

// Nếu danh sách permissions dài (ADMIN ~18 items) → chỉ lưu roles vào JWT,
// load permissions từ Redis cache khi filter chạy.
```

### Bảo vệ Endpoints

```java
// StoryController.java
@PostMapping
@PreAuthorize("hasAuthority('story:create')")
public ResponseEntity<StoryResponse> createStory(...) { ... }

@PatchMapping("/{id}/approve")
@PreAuthorize("hasAuthority('story:approve')")
public ResponseEntity<Void> approveStory(...) { ... }

@DeleteMapping("/{id}")
@PreAuthorize("hasAnyAuthority('story:delete_own','story:delete_any')")
public ResponseEntity<Void> deleteStory(
        @PathVariable String id,
        @AuthenticationPrincipal UserPrincipal user) {
    storyService.delete(id, user);  // service tự check _own vs _any
    return ResponseEntity.noContent().build();
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
@Service
public class PermissionCacheService {

    private static final String KEY_PREFIX = "perms:";
    private static final Duration TTL = Duration.ofMinutes(15);

    public Set<String> getUserPermissions(String userId) {
        String key = KEY_PREFIX + userId;
        try {
            Set<String> cached = redisTemplate.opsForSet().members(key);
            if (cached != null && !cached.isEmpty()) return cached;
        } catch (Exception ignored) { }

        List<String> perms = userRepository.findPermissionsByUserId(userId);
        redisTemplate.opsForSet().add(key, perms.toArray(String[]::new));
        redisTemplate.expire(key, TTL);
        return new HashSet<>(perms);
    }

    // Gọi khi Admin thay đổi role của user
    public void invalidate(String userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
```

---

## 5. 🔓 FREE TIER ACCESS LOGIC

```
stories.free_chapter_limit = NULL  →  Toàn bộ truyện FREE
stories.free_chapter_limit = 0     →  Toàn bộ cần Premium
stories.free_chapter_limit = N     →  N chap đầu FREE, từ chap N+1 cần Premium
```

```java
// ChapterAccessService.java
@Service
public class ChapterAccessService {

    public boolean canRead(Story story, int chapterRank, UserPrincipal user) {
        Integer freeLimit = story.getFreeChapterLimit();

        // Toàn bộ free
        if (freeLimit == null) return true;

        // Chapter nằm trong giới hạn free
        if (chapterRank <= freeLimit) return true;

        // Vượt giới hạn → cần kiểm tra thêm
        if (user == null) return false;                              // Guest không có premium

        // ADMIN: luôn được
        if (user.hasAuthority("story:approve")) return true;

        // UPLOADER: được đọc bộ của mình
        if (user.hasAuthority("chapter:read_premium")
                && story.getUploaderId().equals(user.getUserId())) return true;

        // USER: cần subscription active
        return user.hasAuthority("chapter:read_premium") && user.isPremiumActive();
    }
}
```

---

## 6. 👤 GUEST READING HISTORY

```
Lần đầu vào web (Guest)
    │
    ▼
Backend tạo session_id (UUID) → Set-Cookie: guest_session=<uuid>; HttpOnly; Max-Age=30d
    │
    ▼
User đọc truyện → POST /api/reading-history
  Cookie: guest_session=<uuid> | Header: Authorization: Bearer <token>
    │
    ▼
Backend upsert vào reading_history (user_id hoặc session_id)
    │
    ▼ (khi đăng nhập)
MERGE guest history → tài khoản → xóa bản ghi guest
```

```java
// AuthService.java — gọi sau khi xác thực thành công
@Transactional
public void mergeGuestHistory(String userId, String sessionId) {
    if (sessionId == null || sessionId.isBlank()) return;

    readingHistoryRepository.findBySessionId(sessionId).forEach(gh ->
        readingHistoryRepository.mergeIntoUser(
            userId, gh.getStoryId(), gh.getChapterId(), gh.getLastReadAt())
    );
    readingHistoryRepository.deleteBySessionId(sessionId);
    permissionCacheService.invalidate(userId);  // Xóa cache sau login
}
```

---

## 7. 💳 LUỒNG THANH TOÁN VNPAY

```
User                    Backend                    VNPay
  │                        │                          │
  ├─ POST /payment/create ─▶│                          │
  │  { plan: PREMIUM_1M }   ├─ INSERT transaction ─────▶ DB
  │                        │  status = PENDING         │
  │◀─ { paymentUrl } ───────│◀─ build VNPay URL ────────│
  ├─ redirect ─────────────────────────────────────────▶│
  │                        │               User thanh toán
  │◀─ redirect về ReturnUrl ──────────────────────────│
  ├─ GET /payment/return ──▶│                          │
  │                        ├─ Verify SecureHash        │
  │◀─ redirect /success ───│  Update Transaction       │
  │                        │                          │
  │                        │◀── IPN Callback ─────────│
  │                        ├─ Verify + Idempotency     │
  │                        ├─ Activate Subscription    │
  │                        └─ Response {"RspCode":"00"}▶│
```

**Bảo mật bắt buộc:**
- Không commit `vnp_HashSecret` lên Git — dùng biến môi trường
- Luôn verify `vnp_SecureHash` (HMAC-SHA512) trước khi xử lý callback
- IPN endpoint không yêu cầu JWT (VNPay server gọi trực tiếp)
- Kiểm tra idempotency: nếu transaction đã xử lý thì bỏ qua

---

## 8. ☁️ CLOUDINARY — Upload ảnh Manga

```java
// CloudinaryService.java
@Service
public class CloudinaryService {

    public UploadResult uploadMangaPage(MultipartFile file, String chapterId) {
        Map<String, Object> options = Map.of(
            "folder",        "manga/" + chapterId,
            "resource_type", "image",
            "quality",       "auto:good",
            "fetch_format",  "auto"   // WebP nếu browser hỗ trợ
        );
        Map result = cloudinary.uploader().upload(file.getBytes(), options);
        return new UploadResult(
            (String)  result.get("public_id"),
            (String)  result.get("secure_url"),
            (Integer) result.get("width"),
            (Integer) result.get("height")
        );
    }

    public void deleteImage(String publicId) {
        cloudinary.uploader().destroy(publicId, Map.of());
    }
}
```

---

## 9. 🗂️ CẤU TRÚC PROJECT

```
src/main/java/com/truyenonline/
├── config/
│   ├── SecurityConfig.java            # @EnableMethodSecurity, public endpoints
│   ├── CloudinaryConfig.java
│   └── VNPayConfig.java
│
├── module/
│   ├── auth/
│   │   ├── AuthController.java
│   │   ├── AuthService.java            # mergeGuestHistory() sau login
│   │   ├── JwtService.java             # encode roles + permissions vào JWT
│   │   └── OAuth2UserService.java
│   │
│   ├── user/
│   │   ├── UserController.java
│   │   └── entity/
│   │       ├── User.java
│   │       ├── Role.java               # ★ RBAC
│   │       ├── Permission.java         # ★ RBAC
│   │       ├── UserRole.java           # ★ RBAC
│   │       └── RolePermission.java     # ★ RBAC
│   │
│   ├── admin/
│   │   ├── AdminController.java        # Dashboard, pending stories
│   │   └── UserRoleController.java     # Gán/thu hồi role
│   │
│   ├── story/
│   │   ├── StoryController.java
│   │   ├── StoryService.java           # delete() check _own vs _any
│   │   └── entity/Story.java
│   │
│   ├── chapter/
│   │   ├── ChapterController.java
│   │   ├── ChapterService.java
│   │   ├── ChapterAccessService.java   # ★ Free Tier logic
│   │   └── entity/
│   │       ├── Chapter.java
│   │       └── ChapterPage.java
│   │
│   ├── reading/
│   │   ├── ReadingHistoryController.java
│   │   ├── ReadingHistoryService.java  # ★ Guest + User upsert/merge
│   │   └── entity/ReadingHistory.java
│   │
│   ├── comment/
│   │   ├── CommentController.java
│   │   ├── CommentService.java         # delete() check _own vs _any
│   │   └── entity/Comment.java
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
    │   ├── UserPrincipal.java              # authorities từ permissions
    │   └── PermissionCacheService.java     # ★ Redis cache
    ├── resolver/GuestSessionResolver.java  # Resolve session_id từ cookie
    └── util/SlugUtil.java
```

---

## 10. 🐳 DOCKER COMPOSE

```yaml
version: '3.9'

services:
  backend:
    build: ./backend
    ports: ["8080:8080"]
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
      mysql: { condition: service_healthy }
      redis: { condition: service_healthy }

  frontend:
    build: ./frontend
    ports: ["3000:3000"]
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
    ports: ["80:80"]
    volumes: ["./nginx/nginx.conf:/etc/nginx/nginx.conf"]
    depends_on: [backend, frontend]

volumes:
  mysql_data:
```

---

## 11. 🗓️ ROADMAP

### Phase 0 — Setup (Ngày 1–2)
- [ ] Tạo GitHub repo monorepo `/backend` + `/frontend`
- [ ] Docker Compose: MySQL + Redis + Backend shell
- [ ] Chạy `init.sql` — seed roles (3 role) + permissions (18 permissions)
- [ ] Next.js project: TypeScript + Tailwind
- [ ] Spring Boot project: Web, Security, OAuth2, JPA, MySQL, Redis, Validation, Lombok, Flyway
- [ ] File `.env.example`

### Phase 1 — Authentication + RBAC (Ngày 3–8)
- [ ] Entities: `User`, `Role`, `Permission`, `UserRole`, `RolePermission`
- [ ] Trigger tự gán role `USER` khi đăng ký
- [ ] API đăng ký / login — JWT chứa roles + permissions
- [ ] Google OAuth2
- [ ] `PermissionCacheService` — Redis cache permissions 15 phút
- [ ] JWT Filter → `SecurityContext`
- [ ] Frontend: trang login/register + Google button

### Phase 2 — Core Features (Ngày 9–22)
- [ ] CRUD Stories — `@PreAuthorize` per endpoint
- [ ] CRUD Chapters — phân quyền _own vs _any
- [ ] Upload ảnh manga → Cloudinary
- [ ] `ChapterAccessService` — Free Tier logic
- [ ] Admin: gán/thu hồi role (`UserRoleController`)
- [ ] Lịch sử đọc Guest — cookie + upsert + merge khi login
- [ ] Frontend: danh sách, chi tiết truyện, reader
- [ ] Bookmark, Comment

### Phase 3 — Payment (Ngày 23–29)
- [ ] VNPay Sandbox: tạo URL + Return URL + IPN
- [ ] Kích hoạt Subscription sau thanh toán
- [ ] Frontend: trang mua Premium, lịch sử giao dịch

### Phase 4 — Polish & Deploy (Ngày 30–36)
- [ ] Full-text search (MySQL FULLTEXT)
- [ ] Admin Dashboard (thống kê, duyệt truyện)
- [ ] Rate limiting (Bucket4j)
- [ ] README + Architecture diagram
- [ ] Deploy: Railway/Render + Vercel
- [ ] Record demo video

---

## 12. 🏆 CV HIGHLIGHTS

### 🥇 VNPay Payment + Idempotency
> *"Tích hợp VNPay Payment Gateway, xử lý dual-callback (Return URL + IPN), implement idempotency check tránh duplicate transactions, verify HMAC-SHA512 trên mỗi callback."*

### 🥈 Dual Auth — OAuth2 + JWT Refresh Token Rotation
> *"Dual provider (Google OAuth2 + Local JWT), Refresh Token Rotation với Redis blacklist, chống token replay attack."*

### 🥉 Dynamic Free Tier — Configurable per Story
> *"Uploader tự cấu hình `free_chapter_limit` per bộ, quyền đọc tính động tại Service layer — không cần update hàng nghìn bản ghi chapter khi thay đổi policy."*

### 🎖️ Guest Reading History + Seamless Merge
> *"Guest đọc truyện không cần login (cookie-based session_id), merge lịch sử vào tài khoản khi đăng nhập với conflict resolution."*

### 🏅 RBAC — Role & Permission tách biệt
> *"3-role RBAC (ADMIN / UPLOADER / USER) với bảng `roles`/`permissions` chuẩn, gán nhiều role per user, phân quyền granular theo `resource:action`, tích hợp Spring Security `@PreAuthorize` + Redis cache permissions."*

---

## 13. 📋 BIẾN MÔI TRƯỜNG

```bash
# .env.example

# MySQL
DB_USER=appuser
DB_PASSWORD=your_password_here
DB_ROOT_PASSWORD=your_root_password_here

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
# ⚠️ IPN_URL phải là public IP/domain. Dùng ngrok khi dev local.
```

---

*TruyệnOnline System Architecture | v4.0 | MySQL + RBAC (3 roles) | Tháng 6/2025*

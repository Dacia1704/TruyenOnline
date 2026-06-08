# 📚 TruyệnOnline — Bản Thiết Kế Kiến Trúc Hệ Thống v2 (CV Project)

> **Mục tiêu:** Web đọc truyện chữ & truyện tranh/manga trực tuyến, tích hợp OAuth2, JWT, VNPay, Cloud Storage — một dự án đủ "nặng đô" để gây ấn tượng với nhà tuyển dụng.
>
> **v2 thay đổi so với v1:**
> - 🗄️ **MySQL 8** thay PostgreSQL (primary database)
> - 👤 **Lịch sử đọc cho Guest** (không cần đăng nhập, dùng `session_id`)
> - 🔓 **Free tier chapter có giới hạn** — Uploader tự cấu hình số chap free per bộ truyện

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
│ Storage      │ Cloudinary (Free tier — xem mục 4)       │
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

### Tại sao MySQL 8 thay vì PostgreSQL?

MySQL 8 là lựa chọn phù hợp hơn cho dự án CV hướng đến môi trường doanh nghiệp Việt Nam:

| Tiêu chí | MySQL 8 | PostgreSQL 16 |
|---|---|---|
| Phổ biến tại VN | ✅ Rất cao (LAMP stack, hosting phổ biến) | Thấp hơn |
| NTD quen thuộc | ✅ Hầu hết JD đề cập MySQL | Ít hơn |
| Spring Boot support | ✅ Đầy đủ (JPA, Flyway, Liquibase) | ✅ Đầy đủ |
| JSON support | ✅ MySQL JSON type (từ 5.7+) | ✅ JSONB tốt hơn |
| Full-Text Search | ✅ FULLTEXT Index | ✅ tsvector |
| Window Functions | ✅ MySQL 8+ | ✅ |
| Free hosting | ✅ PlanetScale, Railway, Clever Cloud | Ít lựa chọn free hơn |

> 💡 **Lưu ý kỹ thuật:** MySQL không có `gen_random_uuid()` built-in như PostgreSQL. Dùng `UUID()` hoặc để Java/JPA tự sinh UUID qua `@GeneratedValue`. Schema sẽ dùng `CHAR(36)` hoặc `BINARY(16)` cho UUID.

### Tại sao Next.js cho dự án này?

- **SSR:** Trang chi tiết truyện render phía server → SEO tốt, ảnh bìa load nhanh
- **Image Optimization tích hợp sẵn:** `next/image` tự động lazy load, resize, WebP — cực kỳ quan trọng với web ảnh manga
- **App Router + Layouts:** Tái sử dụng layout reader (thanh điều hướng chapter) mà không re-render
- **API Routes:** Xử lý OAuth callback, VNPay webhook ngay trong Next.js mà không cần proxy phức tạp

---

## 2. 👥 THIẾT KẾ ROLE & AUTHENTICATION FLOW

### Quyết định: 3 Role (Admin / Uploader / User)

| Quyền | Admin | Uploader | User Free | User Premium |
|---|:---:|:---:|:---:|:---:|
| Quản lý tất cả truyện | ✅ | ❌ | ❌ | ❌ |
| Upload truyện của mình | ✅ | ✅ | ❌ | ❌ |
| Config số chap free per bộ | ✅ | ✅ (bộ của mình) | ❌ | ❌ |
| Duyệt/ẩn truyện | ✅ | ❌ | ❌ | ❌ |
| Đọc chapter thường | ✅ | ✅ | ✅ | ✅ |
| Đọc chapter trong free_chapter_limit | ✅ | ✅ | ✅ | ✅ |
| Đọc chapter vượt free_chapter_limit | ✅ | ✅ (bộ của mình) | ❌ | ✅ |
| Mua gói Premium | - | - | ✅ | (đã có) |
| Quản lý users | ✅ | ❌ | ❌ | ❌ |

---

### Authentication Flow

#### Flow 1: Đăng ký / Đăng nhập thường (JWT)

```
Client                    Backend                      DB/Redis
  │                          │                             │
  │──POST /auth/register────▶│                             │
  │  {email, password}       │──hash(bcrypt)──────────────▶│
  │                          │◀───────────────── user_id ──│
  │◀── 201 Created ──────────│                             │
  │                          │                             │
  │──POST /auth/login───────▶│                             │
  │  {email, password}       │──verify password ──────────▶│
  │                          │◀──────────── user_record ───│
  │                          │──generate JWT──┐            │
  │                          │  access_token  │ (15 phút)  │
  │                          │  refresh_token │ (7 ngày)   │
  │                          │◀───────────────┘            │
  │                          │──save refresh_token ───────▶│ (Redis)
  │◀── {access_token,        │                             │
  │     refresh_token} ──────│                             │
```

#### Flow 2: Google OAuth2 Login

```
Client                  Next.js              Backend            Google
  │                       │                     │                  │
  │──click "Login Google"▶│                     │                  │
  │                       │──redirect──────────────────────────────▶│
  │                       │◀── authorization_code ─────────────────│
  │                       │──POST /auth/google──▶│                  │
  │                       │  {code}              │──exchange code──▶│
  │                       │                     │◀── id_token ─────│
  │                       │                     │──find/create user in DB
  │                       │                     │──generate JWT─┐  │
  │                       │◀── {access_token,   │◀──────────────┘  │
  │                       │    refresh_token}   │                  │
  │◀── redirect + set     │                     │                  │
  │    cookie ────────────│                     │                  │
```

#### JWT Token Strategy

```java
// Access Token Payload
{
  "sub": "user_uuid",
  "email": "user@example.com",
  "roles": ["ROLE_USER"],
  "isPremium": true,
  "iat": 1234567890,
  "exp": 1234568790  // +15 phút
}

// Refresh Token: lưu trong Redis với key = "refresh:{user_id}"
// Khi access token hết hạn → POST /auth/refresh với refresh_token
// Backend verify refresh token trong Redis → cấp access token mới
```

---

## 3. 💾 DATABASE SCHEMA (MySQL 8)

### Lý do chọn SQL

- Dữ liệu có quan hệ chặt (User → Transaction → Subscription)
- VNPay transaction cần ACID (không để mất/trùng giao dịch)
- MySQL JSON type hỗ trợ lưu metadata ảnh Cloudinary
- Dễ query thống kê (top truyện, doanh thu, lịch sử đọc)
- MySQL 8 Window Functions đủ mạnh cho mọi query phức tạp

---

### Schema chi tiết (MySQL 8 Syntax)

```sql
-- MySQL 8 — sử dụng CHAR(36) cho UUID, JSON type cho metadata
-- Chạy: mysql -u root -p truyenonline < init.sql

CREATE DATABASE IF NOT EXISTS truyenonline
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE truyenonline;

-- ============================================
-- BẢNG USERS
-- ============================================
CREATE TABLE users (
    id              CHAR(36)     PRIMARY KEY,          -- UUID do JPA tự sinh
    email           VARCHAR(255) UNIQUE NOT NULL,
    username        VARCHAR(100) UNIQUE NOT NULL,
    password_hash   VARCHAR(255),                      -- NULL nếu đăng nhập OAuth
    avatar_url      VARCHAR(500),
    role            ENUM('ADMIN','UPLOADER','USER') NOT NULL DEFAULT 'USER',
    auth_provider   ENUM('LOCAL','GOOGLE')          NOT NULL DEFAULT 'LOCAL',
    google_id       VARCHAR(100) UNIQUE,               -- Google sub claim
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                 ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- BẢNG SUBSCRIPTIONS (Gói Premium)
-- ============================================
CREATE TABLE subscriptions (
    id              CHAR(36)     PRIMARY KEY,
    user_id         CHAR(36)     NOT NULL,
    plan            ENUM('FREE','PREMIUM_1M','PREMIUM_3M','PREMIUM_1Y') NOT NULL,
    status          ENUM('ACTIVE','EXPIRED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    started_at      DATETIME(3)  NOT NULL,
    expires_at      DATETIME(3)  NOT NULL,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_sub_user_id (user_id),
    INDEX idx_sub_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- BẢNG TRANSACTIONS (VNPay — Quan trọng nhất!)
-- ============================================
CREATE TABLE transactions (
    id                  CHAR(36)     PRIMARY KEY,
    user_id             CHAR(36)     NOT NULL,
    subscription_plan   ENUM('PREMIUM_1M','PREMIUM_3M','PREMIUM_1Y') NOT NULL,

    -- Thông tin VNPay
    vnp_txn_ref         VARCHAR(100) UNIQUE NOT NULL,  -- Mã đơn hàng gửi sang VNPay
    vnp_amount          BIGINT       NOT NULL,          -- Số tiền × 100 (VD: 100000 = 1,000đ)
    vnp_bank_code       VARCHAR(20),                    -- Ngân hàng thanh toán
    vnp_transaction_no  VARCHAR(100),                   -- Mã giao dịch phía VNPay
    vnp_response_code   VARCHAR(10),                    -- "00" = thành công
    vnp_secure_hash     VARCHAR(256),                   -- Hash để verify (KHÔNG bao giờ expose)
    vnp_pay_date        VARCHAR(20),                    -- Format: yyyyMMddHHmmss

    -- Trạng thái nội bộ
    status              ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    ip_address          VARCHAR(45),                    -- IP của user khi tạo đơn
    raw_callback_data   JSON,                           -- Toàn bộ params VNPay trả về (debug)

    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                     ON UPDATE CURRENT_TIMESTAMP(3),
    completed_at        DATETIME(3),                    -- Thời điểm thanh toán hoàn tất

    CONSTRAINT fk_txn_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_txn_user_id (user_id),
    INDEX idx_txn_vnp_txn_ref (vnp_txn_ref),
    INDEX idx_txn_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- BẢNG STORIES (Truyện)
-- ============================================
CREATE TABLE stories (
    id                  CHAR(36)     PRIMARY KEY,
    uploader_id         CHAR(36)     NOT NULL,
    title               VARCHAR(500) NOT NULL,
    slug                VARCHAR(500) UNIQUE NOT NULL,   -- "one-piece-dao-hai-tac"
    description         TEXT,
    cover_image_url     VARCHAR(500),                   -- Cloudinary URL
    story_type          ENUM('NOVEL','MANGA') NOT NULL,
    status              ENUM('ONGOING','COMPLETED','HIATUS','DROPPED') NOT NULL DEFAULT 'ONGOING',
    is_published        TINYINT(1)   NOT NULL DEFAULT 0, -- Admin duyệt mới publish

    -- ★ Cấu hình Free Tier per bộ truyện
    -- NULL = toàn bộ truyện free (không giới hạn)
    -- 0    = toàn bộ truyện cần premium
    -- N    = N chap đầu tiên free, phần còn lại cần premium
    free_chapter_limit  INT          DEFAULT NULL,

    view_count          BIGINT       NOT NULL DEFAULT 0,
    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                     ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_story_uploader FOREIGN KEY (uploader_id) REFERENCES users(id),
    INDEX idx_story_slug (slug),
    INDEX idx_story_uploader_id (uploader_id),
    INDEX idx_story_status (status),
    INDEX idx_story_view_count (view_count DESC),
    FULLTEXT INDEX ft_story_title (title)              -- Full-text search
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
-- BẢNG CHAPTERS (Chương truyện)
-- ============================================
CREATE TABLE chapters (
    id              CHAR(36)       PRIMARY KEY,
    story_id        CHAR(36)       NOT NULL,
    chapter_number  DECIMAL(8,1)   NOT NULL,          -- Hỗ trợ chap 1.5, 10.5 (chap đặc biệt)
    title           VARCHAR(500),
    is_published    TINYINT(1)     NOT NULL DEFAULT 0,
    view_count      BIGINT         NOT NULL DEFAULT 0,

    -- Dành cho NOVEL
    content         LONGTEXT,                         -- Nội dung truyện chữ

    -- Dành cho MANGA
    page_count      INT,

    created_at      DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                   ON UPDATE CURRENT_TIMESTAMP(3),

    UNIQUE KEY uq_chapter (story_id, chapter_number),
    CONSTRAINT fk_chap_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE,
    INDEX idx_chap_story_id (story_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Lưu ý: không còn cột is_vip trên chapters
-- Logic "có cần premium không" được tính động dựa vào stories.free_chapter_limit
-- và thứ tự chapter (chapter_number) — xem mục "Free Tier Access Logic" bên dưới

-- ============================================
-- BẢNG CHAPTER_PAGES (Trang ảnh Manga)
-- ============================================
CREATE TABLE chapter_pages (
    id              CHAR(36)     PRIMARY KEY,
    chapter_id      CHAR(36)     NOT NULL,
    page_number     INT          NOT NULL,
    image_url       VARCHAR(500) NOT NULL,             -- Cloudinary URL
    cloudinary_id   VARCHAR(200),                      -- Public ID để xóa/transform
    width           INT,
    height          INT,

    UNIQUE KEY uq_page (chapter_id, page_number),
    CONSTRAINT fk_page_chap FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    INDEX idx_page_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- BẢNG READING_HISTORY (Lịch sử đọc — cả User lẫn Guest)
-- ============================================
-- Thiết kế hỗ trợ 2 loại người đọc:
--   1. Người dùng đã đăng nhập: user_id != NULL, session_id = NULL
--   2. Khách (guest):           user_id = NULL,  session_id = VARCHAR (từ cookie)
--
-- Khi khách đăng nhập, backend MERGE lịch sử guest → user:
--   UPDATE reading_history SET user_id = ?, session_id = NULL WHERE session_id = ?
CREATE TABLE reading_history (
    id              CHAR(36)     PRIMARY KEY,

    -- Một trong hai phải có giá trị (CHECK constraint)
    user_id         CHAR(36)     DEFAULT NULL,         -- NULL nếu là guest
    session_id      VARCHAR(128) DEFAULT NULL,         -- NULL nếu đã đăng nhập

    story_id        CHAR(36)     NOT NULL,
    chapter_id      CHAR(36)     NOT NULL,
    last_read_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    -- Mỗi user/session chỉ có 1 record per truyện (upsert)
    -- Không thể đặt UNIQUE (user_id, story_id) đơn giản vì user_id có thể NULL
    -- → Dùng 2 unique index riêng
    CONSTRAINT fk_rh_user    FOREIGN KEY (user_id)    REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rh_story   FOREIGN KEY (story_id)   REFERENCES stories(id) ON DELETE CASCADE,
    CONSTRAINT fk_rh_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id),

    -- Unique: mỗi user chỉ 1 bản ghi per truyện
    UNIQUE KEY uq_user_story    (user_id,    story_id),
    -- Unique: mỗi session_id chỉ 1 bản ghi per truyện
    UNIQUE KEY uq_session_story (session_id, story_id),

    INDEX idx_rh_user_id    (user_id),
    INDEX idx_rh_session_id (session_id),
    INDEX idx_rh_last_read  (last_read_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- BẢNG BOOKMARKS (Tủ truyện — chỉ user đã đăng nhập)
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
    parent_id   CHAR(36)     DEFAULT NULL,             -- Reply comment
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_cmt_user    FOREIGN KEY (user_id)   REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_cmt_story   FOREIGN KEY (story_id)  REFERENCES stories(id)  ON DELETE CASCADE,
    CONSTRAINT fk_cmt_chapter FOREIGN KEY (chapter_id)REFERENCES chapters(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmt_parent  FOREIGN KEY (parent_id) REFERENCES comments(id),

    -- Ít nhất 1 trong 2 phải có giá trị
    CONSTRAINT chk_comment_target CHECK (story_id IS NOT NULL OR chapter_id IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

### ERD Diagram

```
users ──────┬────────────── subscriptions
            │                   │
            ├────────────── transactions
            │
            ├────────────── stories ─────────────┬── story_genres ── genres
            │               │                    │
            │               │ free_chapter_limit │
            │               │ (config per bộ)    │
            │               └── chapters ─────────┬── chapter_pages
            │                                     │
            ├────────────── reading_history ───────┘
            │               (user_id OR session_id)
            ├────────────── bookmarks
            └────────────── comments

guest (no login) ── session_id ── reading_history
```

---

### 🔓 Free Tier Access Logic (Quan trọng!)

Thay vì dùng cột `is_vip` trên từng chapter, logic phân quyền đọc được tính **động** dựa trên `stories.free_chapter_limit`. Điều này cho phép Uploader thay đổi config mà không cần cập nhật hàng nghìn bản ghi chapter.

```
stories.free_chapter_limit = NULL  →  Toàn bộ truyện FREE (đọc thoải mái)
stories.free_chapter_limit = 0     →  Toàn bộ truyện cần PREMIUM
stories.free_chapter_limit = 20    →  20 chap đầu FREE, từ chap 21 trở đi cần PREMIUM
```

**Service Layer — kiểm tra quyền đọc:**

```java
// ChapterAccessService.java
@Service
public class ChapterAccessService {

    /**
     * Kiểm tra user/guest có thể đọc chapter này không.
     * rank = thứ tự chương trong bộ (1-based), tính theo chapter_number tăng dần.
     */
    public boolean canRead(Story story, int chapterRank, UserPrincipal user) {
        Integer freeLimit = story.getFreeChapterLimit();

        // Truyện hoàn toàn free
        if (freeLimit == null) return true;

        // Chapter nằm trong giới hạn free
        if (chapterRank <= freeLimit) return true;

        // Vượt quá giới hạn → cần premium (hoặc là Uploader/Admin của bộ truyện)
        if (user == null) return false;                     // Guest không có premium
        if (user.hasRole("ADMIN")) return true;
        if (user.hasRole("UPLOADER") && story.getUploaderId().equals(user.getId())) return true;
        return user.isPremiumActive();                      // Check subscription còn hạn
    }
}

// Trong ChapterService.java
public ChapterDetailResponse getChapterContent(String chapterId, UserPrincipal user) {
    Chapter chapter = chapterRepository.findByIdOrThrow(chapterId);
    Story   story   = chapter.getStory();

    // Tính rank của chapter trong bộ (dùng MySQL window function hoặc đếm trước)
    int rank = chapterRepository.countByStoryIdAndChapterNumberLessThanEqual(
                   story.getId(), chapter.getChapterNumber());

    if (!chapterAccessService.canRead(story, rank, user)) {
        throw new AccessDeniedException("Chương này yêu cầu tài khoản Premium");
    }

    // Trả nội dung...
}
```

**API trả về preview cho guest/free user:**

```json
// GET /api/chapters/{id} khi không có quyền
{
  "id": "...",
  "chapterNumber": 25,
  "title": "Chương 25: ...",
  "locked": true,
  "freeChapterLimit": 20,
  "premiumRequired": true,
  "preview": "Nội dung 3 dòng đầu tiên...",  // Optional: preview ngắn để kích thích
  "upgradeUrl": "/pricing"
}
```

---

### 👤 Guest Reading History — Chi tiết

#### Cơ chế hoạt động

```
Lần đầu vào web (Guest)
        │
        ▼
Backend tạo session_id (UUID) nếu chưa có
→ Gửi về client qua Set-Cookie: guest_session=<uuid>; HttpOnly; Max-Age=30d
        │
        ▼
User đọc truyện → POST /api/reading-history
  Body: { storyId, chapterId }
  Cookie: guest_session=<uuid>  (tự động đính kèm)
        │
        ▼
Backend upsert vào reading_history:
  INSERT INTO reading_history (id, session_id, story_id, chapter_id, last_read_at)
  VALUES (...)
  ON DUPLICATE KEY UPDATE chapter_id = VALUES(chapter_id), last_read_at = NOW()
        │
        ▼
User quyết định đăng nhập → MERGE lịch sử guest → tài khoản
```

**API upsert lịch sử đọc (xử lý cả 2 trường hợp):**

```java
// ReadingHistoryService.java
@Transactional
public void upsertHistory(String storyId, String chapterId,
                          UserPrincipal user, String sessionId) {
    if (user != null) {
        // Người dùng đã đăng nhập
        readingHistoryRepository.upsertForUser(user.getId(), storyId, chapterId);
    } else if (sessionId != null && !sessionId.isBlank()) {
        // Guest — chỉ lưu nếu session_id hợp lệ (đã được set từ trước)
        readingHistoryRepository.upsertForSession(sessionId, storyId, chapterId);
    }
    // Không có cả 2 → bỏ qua (trường hợp hiếm)
}

// MySQL UPSERT (JPA native query)
@Modifying
@Query(value = """
    INSERT INTO reading_history (id, user_id, story_id, chapter_id, last_read_at)
    VALUES (UUID(), :userId, :storyId, :chapterId, NOW(3))
    ON DUPLICATE KEY UPDATE
        chapter_id   = VALUES(chapter_id),
        last_read_at = NOW(3)
    """, nativeQuery = true)
void upsertForUser(@Param("userId") String userId,
                   @Param("storyId") String storyId,
                   @Param("chapterId") String chapterId);
```

**Merge lịch sử khi đăng nhập:**

```java
// Trong AuthService.java — gọi sau khi xác thực thành công
@Transactional
public void mergeGuestHistory(String userId, String sessionId) {
    if (sessionId == null || sessionId.isBlank()) return;

    // Lấy lịch sử guest
    List<ReadingHistory> guestHistory =
        readingHistoryRepository.findBySessionId(sessionId);

    for (ReadingHistory gh : guestHistory) {
        // Upsert vào tài khoản (bỏ qua nếu user đã có bản ghi mới hơn)
        readingHistoryRepository.mergeIntoUser(userId, gh.getStoryId(),
                                               gh.getChapterId(), gh.getLastReadAt());
    }

    // Xóa lịch sử guest sau khi merge
    readingHistoryRepository.deleteBySessionId(sessionId);
}

@Modifying
@Query(value = """
    INSERT INTO reading_history (id, user_id, story_id, chapter_id, last_read_at)
    VALUES (UUID(), :userId, :storyId, :chapterId, :lastReadAt)
    ON DUPLICATE KEY UPDATE
        chapter_id   = IF(last_read_at < VALUES(last_read_at), VALUES(chapter_id), chapter_id),
        last_read_at = GREATEST(last_read_at, VALUES(last_read_at))
    """, nativeQuery = true)
void mergeIntoUser(@Param("userId") String userId,
                   @Param("storyId") String storyId,
                   @Param("chapterId") String chapterId,
                   @Param("lastReadAt") LocalDateTime lastReadAt);
```

---

## 4. ☁️ CLOUDINARY — Lưu trữ ảnh miễn phí

### So sánh Free Tier

| Service | Storage Free | Bandwidth Free | Transform | Verdict |
|---|---|---|---|---|
| **Cloudinary** | 25 GB | 25 GB/tháng | ✅ Crop, resize, format tự động | ✅ **Tốt nhất** |
| Firebase Storage | 5 GB | 1 GB/ngày | ❌ | ❌ |
| AWS S3 Free Tier | 5 GB (12 tháng) | 15 GB/tháng | ❌ | ❌ |

### Flow Upload Ảnh Manga

```
Uploader (Browser)
        │
        │ 1. POST /api/chapters/{id}/pages
        │    multipart/form-data (file ảnh)
        ▼
Backend (Spring Boot)
        │ 2. Validate file (size, type, MIME)
        │ 3. cloudinary.uploader().upload(bytes, options)
        │ 4. Nhận { public_id, secure_url, width, height }
        │ 5. INSERT INTO chapter_pages (...)
        ▼
Database (MySQL)
        │ lưu cloudinary_id, image_url, width, height
        ▼
Response: { page_id, image_url }  →  Uploader ✅
```

```java
// CloudinaryService.java
@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public UploadResult uploadMangaPage(MultipartFile file, String chapterId) {
        Map<String, Object> options = Map.of(
            "folder",        "manga/" + chapterId,
            "resource_type", "image",
            "quality",       "auto:good",
            "fetch_format",  "auto"        // WebP nếu browser hỗ trợ
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

## 5. 💳 LUỒNG THANH TOÁN VNPAY

### Yêu cầu bảo mật tối thiểu

- **KHÔNG** để `vnp_HashSecret` trong code hoặc commit lên Git
- Luôn verify `vnp_SecureHash` trước khi xử lý callback
- Callback endpoint **KHÔNG** yêu cầu JWT auth (VNPay server gọi trực tiếp)
- Dùng **Idempotency** — kiểm tra `vnp_TxnRef` đã xử lý chưa trước khi cấp Premium

### Luồng thanh toán đầy đủ

```
User (Browser)              Backend                    VNPay
      │                        │                          │
      │─ POST /payment/create ─▶│                          │
      │  { plan: "PREMIUM_1M" } │── Tạo Transaction ──────▶ DB
      │                        │   status = PENDING        │
      │                        │── Build VNPay URL ─────-──│
      │◀─ { paymentUrl } ──────│                           │
      │── redirect ────────────────────────────────────────▶│
      │                        │                    User thanh toán
      │◀── redirect về ReturnUrl ──────────────────────────│
      │─ GET /payment/return ──▶│                          │
      │                        │── Verify SecureHash       │
      │                        │── Update Transaction      │
      │◀─ redirect /success ───│   status = SUCCESS        │
      │                        │                          │
      │                        │◀── IPN Callback ─────────│
      │                        │── Verify + Idempotency    │
      │                        │── Activate Subscription   │
      │                        │── Response {"RspCode":"00"}▶│
```

| | Return URL | IPN (Instant Payment Notification) |
|---|---|---|
| Ai gọi | Browser user redirect về | VNPay server gọi trực tiếp |
| Tin cậy? | ❌ User có thể fake | ✅ Đáng tin hơn |
| Mục đích | Hiển thị UI kết quả | **Cập nhật DB chắc chắn** |

```java
// PaymentController.java — IPN Handler
@GetMapping("/vnpay-ipn")
public ResponseEntity<Map<String, String>> handleVNPayIPN(
        @RequestParam Map<String, String> params) {

    String vnpSecureHash = params.remove("vnp_SecureHash");
    params.remove("vnp_SecureHashType");

    // 1. Verify hash
    String computedHash = VNPayUtil.hmacSHA512(secretKey, VNPayUtil.buildHashData(params));
    if (!computedHash.equalsIgnoreCase(vnpSecureHash)) {
        return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid signature"));
    }

    String txnRef  = params.get("vnp_TxnRef");
    String rspCode = params.get("vnp_ResponseCode");

    // 2. Idempotency check
    Transaction tx = transactionService.findByTxnRef(txnRef);
    if (tx == null)
        return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));
    if (tx.getStatus() != TransactionStatus.PENDING)
        return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Already confirmed"));

    // 3. Xử lý kết quả
    if ("00".equals(rspCode)) {
        transactionService.markSuccess(tx, params);
        subscriptionService.activate(tx.getUserId(), tx.getSubscriptionPlan());
    } else {
        transactionService.markFailed(tx, rspCode);
    }

    return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
}
```

---

## 6. 🗂️ CẤU TRÚC PROJECT

### Backend (Spring Boot)

```
src/main/java/com/truyenonline/
├── config/
│   ├── SecurityConfig.java
│   ├── CloudinaryConfig.java
│   └── VNPayConfig.java
│
├── module/
│   ├── auth/
│   │   ├── AuthController.java
│   │   ├── AuthService.java          # mergeGuestHistory() khi login
│   │   ├── JwtService.java
│   │   └── OAuth2UserService.java
│   │
│   ├── user/
│   │   └── entity/User.java
│   │
│   ├── story/
│   │   ├── StoryController.java
│   │   ├── StoryService.java
│   │   └── entity/Story.java         # free_chapter_limit field
│   │
│   ├── chapter/
│   │   ├── ChapterController.java
│   │   ├── ChapterService.java
│   │   ├── ChapterAccessService.java  # ★ Free tier logic
│   │   └── entity/{Chapter, ChapterPage}.java
│   │
│   ├── reading/
│   │   ├── ReadingHistoryController.java
│   │   ├── ReadingHistoryService.java # ★ Guest + User history
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
    ├── security/JwtAuthFilter.java
    ├── resolver/GuestSessionResolver.java  # ★ Resolve session_id từ cookie
    └── util/SlugUtil.java
```

---

## 7. 🐳 DOCKER COMPOSE

```yaml
# docker-compose.yml
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

## 8. 🗓️ ROADMAP THỰC THI

### Phase 0 — Setup (Ngày 1–2)
- [ ] Tạo GitHub repo (monorepo: `/backend` + `/frontend`)
- [ ] Setup Docker Compose (MySQL + Redis + Backend shell)
- [ ] Tạo database schema (chạy `init.sql`)
- [ ] Tạo Next.js project với TypeScript + Tailwind
- [ ] Setup Spring Boot project với dependencies
- [ ] Tạo file `.env.example`

### Phase 1 — Authentication (Ngày 3–7)
- [ ] Implement User entity + Repository
- [ ] API đăng ký / đăng nhập với JWT (access + refresh token)
- [ ] Tích hợp Google OAuth2
- [ ] JWT Filter bảo vệ protected endpoints
- [ ] Frontend: trang login/register + Google button
- [ ] Lưu token vào httpOnly cookie

### Phase 2 — Core Features (Ngày 8–21)
- [ ] CRUD Stories (Admin + Uploader) — bao gồm `free_chapter_limit`
- [ ] CRUD Chapters (text novel + manga pages)
- [ ] Upload ảnh manga lên Cloudinary
- [ ] **`ChapterAccessService`** — kiểm tra quyền đọc theo `free_chapter_limit`
- [ ] **Lịch sử đọc Guest** — GuestSessionResolver + cookie `guest_session`
- [ ] Merge lịch sử Guest → User khi đăng nhập
- [ ] Frontend: danh sách truyện, chi tiết truyện, reader
- [ ] Hiển thị lock icon + preview cho chapter cần premium
- [ ] Bookmark / Tủ truyện (chỉ user đã đăng nhập)

### Phase 3 — Payment (Ngày 22–28)
- [ ] Tích hợp VNPay Sandbox
- [ ] API tạo URL thanh toán
- [ ] Xử lý Return URL + IPN Callback
- [ ] Kích hoạt Subscription sau thanh toán
- [ ] Frontend: trang mua gói Premium, lịch sử giao dịch

### Phase 4 — Polish & Deploy (Ngày 29–35)
- [ ] Search truyện (MySQL FULLTEXT Index)
- [ ] Admin Dashboard (thống kê, duyệt truyện)
- [ ] Rate limiting (Bucket4j)
- [ ] Viết README chi tiết + Architecture diagram
- [ ] Deploy: Railway/Render (backend + MySQL) + Vercel (frontend)
- [ ] Record demo video cho CV

---

## 9. 🏆 CV HIGHLIGHTS — 4 Tính Năng "Sát Thủ"

### 🥇 #1: Secure VNPay Payment Flow với Idempotency

**Cách nêu trong CV:**
> *"Tích hợp VNPay Payment Gateway, xử lý dual-callback (Return URL + IPN), implement idempotency check để tránh duplicate transactions, verify HMAC-SHA512 signature trên mỗi callback request."*

---

### 🥈 #2: Dual Authentication — OAuth2 + JWT với Refresh Token Rotation

**Cách nêu trong CV:**
> *"Thiết kế authentication system hỗ trợ dual provider (Google OAuth2 + Local JWT), implement Refresh Token Rotation với Redis blacklist, bảo vệ chống token replay attack."*

---

### 🥉 #3: Dynamic Free Tier — Configurable per Story

**Tại sao ấn tượng:**
Thay vì hardcode `is_vip` trên từng chapter, thiết kế một cột `free_chapter_limit` trên `stories` để Uploader tự cấu hình. Khi thay đổi policy, không cần cập nhật hàng nghìn bản ghi. Đây là tư duy thiết kế linh hoạt, schema-driven — NTD sẽ để ý.

**Cách nêu trong CV:**
> *"Thiết kế Content Access Control linh hoạt: Uploader cấu hình `free_chapter_limit` per bộ truyện, quyền đọc được tính động tại Service layer mà không cần thay đổi dữ liệu chapter, kết hợp với Premium subscription check."*

---

### 🎖️ #4: Guest Reading History với Seamless Merge

**Tại sao ấn tượng:**
Đây là tính năng UX cực kỳ thực tế mà các app lớn như Netflix, Shopee đều làm: guest dùng được trước khi đăng ký, và khi đăng nhập thì lịch sử không bị mất. Việc xử lý đúng edge case (merge conflict khi cùng truyện có cả guest lẫn user history) thể hiện sự chín chắn kỹ thuật.

**Cách nêu trong CV:**
> *"Implement Guest Session cho Reading History (cookie-based, không cần đăng nhập), xử lý merge lịch sử guest → tài khoản khi đăng nhập với conflict resolution (giữ mốc thời gian mới nhất)."*

---

## 10. ⚡ QUICK START

```bash
# 1. Init project
mkdir truyen-online && cd truyen-online && git init

# 2. Spring Boot — start.spring.io
# Dependencies: Web, Security, OAuth2 Client, Data JPA,
#               MySQL Driver, Redis, Validation, Lombok, Flyway

# 3. Tạo Next.js project
npx create-next-app@latest frontend --typescript --tailwind --app

# 4. Chạy infrastructure
docker compose up mysql redis -d

# 5. Đăng ký tài khoản miễn phí
# Cloudinary: https://cloudinary.com/users/register/free
# VNPay Sandbox: https://sandbox.vnpayment.vn/devreg/
# Google OAuth2: https://console.cloud.google.com/
```

---

## 11. 📋 BIẾN MÔI TRƯỜNG

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
JWT_EXPIRATION_MS=900000           # 15 phút
JWT_REFRESH_EXPIRATION_MS=604800000 # 7 ngày

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

*Senior Software Architect Design | v2.0 | MySQL Edition | Tháng 6/2025*

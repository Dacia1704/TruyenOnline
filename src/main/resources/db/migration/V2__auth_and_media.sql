-- ============================================================
-- TruyệnOnline — V2__auth_and_media.sql
-- Bổ sung: Forgot Password, Refresh Token, Quản lý Media Upload
-- MySQL 8.0 | utf8mb4 | UUID = CHAR(36) sinh bởi JPA
-- Không sửa bảng cũ, không xoá dữ liệu hiện có.
-- ============================================================

USE truyenonline;

-- ============================================================
-- BẢNG PASSWORD_RESET_TOKENS
-- Lưu hash của token quên mật khẩu (không lưu token gốc).
-- Một token chỉ dùng được 1 lần: khi dùng xong set used_at,
-- tầng service phải kiểm tra used_at IS NULL AND expired_at > NOW()
-- trước khi cho phép reset.
-- ============================================================
CREATE TABLE password_reset_tokens (
    id          CHAR(36)     PRIMARY KEY,
    user_id     CHAR(36)     NOT NULL,
    token_hash  VARCHAR(255) NOT NULL,          -- SHA-256/BCrypt hash của token gửi qua email
    expired_at  DATETIME(3)  NOT NULL,
    used_at     DATETIME(3)  NULL,              -- NULL = chưa sử dụng; set khi reset thành công
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Tra cứu nhanh theo hash khi user click link reset
    UNIQUE KEY uq_prt_token_hash (token_hash),
    -- Liệt kê / thu hồi các token của 1 user
    INDEX idx_prt_user_id (user_id),
    -- Hỗ trợ job dọn dẹp token hết hạn
    INDEX idx_prt_expired_at (expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG REFRESH_TOKENS
-- Access Token (JWT) không lưu DB — vẫn giữ nguyên như hiện tại.
-- Chỉ lưu Refresh Token (dạng hash) để hỗ trợ đa thiết bị,
-- revoke theo thiết bị (logout) hoặc revoke toàn bộ (logout all).
-- ============================================================
CREATE TABLE refresh_tokens (
    id            CHAR(36)     PRIMARY KEY,
    user_id       CHAR(36)     NOT NULL,
    token_hash    VARCHAR(255) NOT NULL,        -- hash của refresh token, không lưu plaintext
    device_name   VARCHAR(255) NULL,            -- VD: "Chrome on Windows", "iPhone 15"
    device_id     VARCHAR(255) NULL,            -- định danh thiết bị do client sinh ra
    ip_address    VARCHAR(45)  NULL,            -- hỗ trợ IPv6
    user_agent    VARCHAR(500) NULL,
    expired_at    DATETIME(3)  NOT NULL,
    revoked_at    DATETIME(3)  NULL,            -- NULL = còn hiệu lực; set khi logout / logout-all
    last_used_at  DATETIME(3)  NULL,            -- cập nhật mỗi lần dùng để refresh access token
    created_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                               ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Tra cứu nhanh theo hash khi client gọi API refresh
    UNIQUE KEY uq_rt_token_hash (token_hash),
    -- Logout hiện tại (theo device) / liệt kê thiết bị đang đăng nhập của 1 user
    INDEX idx_rt_user_id (user_id),
    INDEX idx_rt_user_device (user_id, device_id),
    -- Logout all devices: revoke toàn bộ token còn hiệu lực của user
    INDEX idx_rt_user_revoked (user_id, revoked_at),
    -- Hỗ trợ job dọn dẹp token hết hạn / đã revoke
    INDEX idx_rt_expired_at (expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- BẢNG MEDIA_FILES
-- Quản lý tập trung mọi file upload lên Cloudinary (ảnh, và mở
-- rộng được cho video/tài liệu sau này). Story/Chapter/User...
-- sẽ tham chiếu tới media_files.id thay vì lưu thẳng URL.

-- ============================================================
CREATE TABLE media_files (
    id                 CHAR(36)     PRIMARY KEY,
    public_id          VARCHAR(255) NOT NULL,       -- Cloudinary public_id, dùng để xoá file
    secure_url         VARCHAR(500) NOT NULL,
    uploaded_by        CHAR(36)     NULL,           -- NULL nếu upload bởi hệ thống/admin ẩn danh
    reference_count    INT          NOT NULL DEFAULT 0,  -- số entity đang tham chiếu file này
    created_at         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                    ON UPDATE CURRENT_TIMESTAMP(3),

    CONSTRAINT fk_mf_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE SET NULL,

    UNIQUE KEY uq_mf_public_id (public_id),
    UNIQUE KEY uq_mf_secure_url (secure_url),
    INDEX idx_mf_uploaded_by (uploaded_by),
    INDEX idx_mf_cleanup (reference_count, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

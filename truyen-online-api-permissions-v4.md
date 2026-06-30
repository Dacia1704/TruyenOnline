# 📡 TruyệnOnline — API Endpoints & Permission Matrix v4

> **3 Role:** `ADMIN` | `UPLOADER` | `USER`
>
> **Ký hiệu:**
>
> - ✅ Có quyền truy cập
> - ◐ Có điều kiện (xem bảng ghi chú bên dưới)
> - ❌ Không có quyền
> - ○ Public — không cần token

---

## 🔐 Auth

| Method | Endpoint             | Permission    | ADMIN | UPLOADER | USER |
| ------ | -------------------- | ------------- | :---: | :------: | :--: |
| `POST` | `/api/auth/register` | public        |   ○   |    ○     |  ○   |
| `POST` | `/api/auth/login`    | public        |   ○   |    ○     |  ○   |
| `POST` | `/api/auth/google`   | public        |   ○   |    ○     |  ○   |
| `POST` | `/api/auth/refresh`  | public        |   ○   |    ○     |  ○   |
| `POST` | `/api/auth/logout`   | authenticated |  ✅   |    ✅    |  ✅  |

---

## 👤 Users

| Method  | Endpoint                | Permission          | ADMIN | UPLOADER | USER |
| ------- | ----------------------- | ------------------- | :---: | :------: | :--: |
| `GET`   | `/api/users/me`         | authenticated       |  ✅   |    ✅    |  ✅  |
| `PATCH` | `/api/users/me`         | authenticated       |  ✅   |    ✅    |  ✅  |
| `GET`   | `/api/users`            | `user:read`         |  ✅   |    ❌    |  ❌  |
| `GET`   | `/api/users/{id}`       | `user:read`         |  ✅   |    ❌    |  ❌  |
| `PATCH` | `/api/users/{id}/ban`   | `user:ban`          |  ✅   |    ❌    |  ❌  |
| `POST`  | `/api/users/{id}/roles` | `user:manage_roles` |  ✅   |    ❌    |  ❌  |

---

## 📚 Stories

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
| --- | --- | --- | :-: | :-: | :-: |
| `GET` | `/api/stories` | public | ○ | ○ | ○ |
| `GET` | `/api/stories/{slug}` | public | ○ | ○ | ○ |
| `POST` | `/api/stories` | `story:create` | ✅ | ✅ | ❌ |
| `PATCH` | `/api/stories/{id}` | `story:update_own` / `story:update_any` | ✅ | ◐ | ❌ |
| `DELETE` | `/api/stories/{id}` | `story:delete_own` / `story:delete_any` | ✅ | ◐ | ❌ |
| `GET` | `/api/stories/publish-requests` | `admin` | ✅ | ◐ | ❌ |
| `POST` | `/api/stories/publish-requests/me` | `story:update_own` | ✅ | ◐ | ❌ |
| `POST` | `/api/stories/{id}/request-publish` | `story:update_own` | ✅ | ◐ | ❌ |
| `PATCH` | `/api/stories/publish-requests/approve/{id}` | `admin` | ✅ | ❌ | ❌ |
| `PATCH` | `/api/stories/publish-requests/reject/{id}` | `admin` | ✅ | ❌ | ❌ |

---

## 📖 Chapters

| Method   | Endpoint                       | Permission                              | ADMIN | UPLOADER | USER |
| -------- | ------------------------------ | --------------------------------------- | :---: | :------: | :--: |
| `GET`    | `/api/stories/{slug}/chapters` | public                                  |   ○   |    ○     |  ○   |
| `GET`    | `/api/chapters/{id}`           | public (sẽ limit dựa bằng biến ở story) |  ✅   |    ◐     |  ◐   |
| `POST`   | `/api/stories/{id}/chapters`   | `chapter:create`                        |  ✅   |    ✅    |  ❌  |
| `PATCH`  | `/api/chapters/{id}`           | `chapter:update_own`                    |  ✅   |    ◐     |  ❌  |
| `DELETE` | `/api/chapters/{id}`           | `chapter:delete_own`                    |  ✅   |    ◐     |  ❌  |

---

## 🖼️ Chapter Pages (Manga)

| Method   | Endpoint                            | Permission                          | ADMIN | UPLOADER | USER |
| -------- | ----------------------------------- | ----------------------------------- | :---: | :------: | :--: |
| `GET`    | `/api/chapters/{id}/pages`          | `chapter:read_premium` (nếu locked) |  ✅   |    ◐     |  ◐   |
| `POST`   | `/api/chapters/pages`               | `chapter:create`                    |  ✅   |    ✅    |  ❌  |
| `PATCH`  | `/api/chapters/pages`               | `chapter:update_own`                |  ✅   |    ✅    |  ❌  |
| `DELETE` | `/api/chapters/{id}/pages/{pageId}` | `chapter:delete_own`                |  ✅   |    ◐     |  ❌  |

---

## 🏷️ Genres

| Method   | Endpoint           | Permission | ADMIN | UPLOADER | USER |
| -------- | ------------------ | ---------- | :---: | :------: | :--: |
| `GET`    | `/api/genres`      | public     |   ○   |    ○     |  ○   |
| `POST`   | `/api/genres`      | `ADMIN`    |  ✅   |    ❌    |  ❌  |
| `PATCH`  | `/api/genres/{id}` | `ADMIN`    |  ✅   |    ❌    |  ❌  |
| `DELETE` | `/api/genres/{id}` | `ADMIN`    |  ✅   |    ❌    |  ❌  |

---

## 📜 Reading History

| Method   | Endpoint                 | Permission            | ADMIN | UPLOADER | USER |
| -------- | ------------------------ | --------------------- | :---: | :------: | :--: |
| `GET`    | `/api/reading-histories` | authenticated         |  ✅   |    ✅    |  ✅  |
| `POST`   | `/api/reading-histories` | public (guest + user) |   ○   |    ○     |  ○   |
| `PATCH`  | `/api/reading-histories` | public (guest + user) |   ○   |    ○     |  ○   |
| `DELETE` | `/api/reading-histories` | authenticated         |  ✅   |    ✅    |  ✅  |

---

## 🔖 Bookmarks

| Method   | Endpoint                  | Permission    | ADMIN | UPLOADER | USER |
| -------- | ------------------------- | ------------- | :---: | :------: | :--: |
| `GET`    | `/api/bookmarks?storyId=` | authenticated |  ✅   |    ✅    |  ✅  |
| `POST`   | `/api/bookmarks?storyId=` | authenticated |  ✅   |    ✅    |  ✅  |
| `DELETE` | `/api/bookmarks?storyId=` | authenticated |  ✅   |    ✅    |  ✅  |

---

## 💬 Comments

| Method   | Endpoint             | Permission                                  | ADMIN | UPLOADER | USER |
| -------- | -------------------- | ------------------------------------------- | :---: | :------: | :--: |
| `GET`    | `/api/comments`      | public                                      |   ○   |    ○     |  ○   |
| `POST`   | `/api/comments`      | `comment:create`                            |  ✅   |    ✅    |  ✅  |
| `PATCH`  | `/api/comments/{id}` | `comment:create`                            |  ✅   |    ✅    |  ✅  |
| `DELETE` | `/api/comments/{id}` | `comment:delete_own` / `comment:delete_any` |  ✅   |    ◐     |  ◐   |

---

## 💳 Payment & Subscription

| Method | Endpoint                       | Permission              | ADMIN | UPLOADER | USER |
| ------ | ------------------------------ | ----------------------- | :---: | :------: | :--: |
| `POST` | `/api/payment/create`          | `subscription:buy`      |  ✅   |    ✅    |  ✅  |
| `GET`  | `/api/payment/return`          | public (VNPay redirect) |   ○   |    ○     |  ○   |
| `GET`  | `/api/payment/vnpay-ipn`       | public (VNPay server)   |   ○   |    ○     |  ○   |
| `GET`  | `/api/payment/transactions`    | `payment:manage`        |  ✅   |    ❌    |  ❌  |
| `GET`  | `/api/payment/transactions/me` | authenticated           |  ✅   |    ✅    |  ✅  |
| `GET`  | `/api/subscriptions/me`        | authenticated           |  ✅   |    ✅    |  ✅  |

---

## 🛠️ Admin

| Method | Endpoint                     | Permission          | ADMIN | UPLOADER | USER |
| ------ | ---------------------------- | ------------------- | :---: | :------: | :--: |
| `GET`  | `/api/admin/roles`           | `user:manage_roles` |  ✅   |    ❌    |  ❌  |
| `GET`  | `/api/admin/permissions`     | `user:manage_roles` |  ✅   |    ❌    |  ❌  |
| `GET`  | `/api/admin/dashboard`       | `user:read`         |  ✅   |    ❌    |  ❌  |
| `GET`  | `/api/admin/stories/pending` | `story:approve`     |  ✅   |    ❌    |  ❌  |

---

## 📝 Ghi chú — Điều kiện ◐

| Endpoint | UPLOADER | USER |
| --- | --- | --- |
| `PATCH /api/stories/{id}` | Chỉ sửa bộ truyện do mình upload | — |
| `DELETE /api/stories/{id}` | Chỉ xóa bộ truyện do mình upload | — |
| `PATCH /api/stories/{id}/free-limit` | Chỉ config bộ truyện của mình | — |
| `GET /api/chapters/{id}` | Đọc premium nếu là bộ của mình; free nếu trong giới hạn | Đọc nếu trong giới hạn free hoặc có subscription active |
| `GET /api/chapters/{id}/pages` | Như trên | Như trên |
| `PATCH /api/chapters/{id}` | Chỉ sửa chapter thuộc bộ của mình | — |
| `DELETE /api/chapters/{id}` | Chỉ xóa chapter thuộc bộ của mình | — |
| `DELETE /api/chapters/{id}/pages/{pageId}` | Chỉ xóa page thuộc chapter của mình | — |
| `DELETE /api/comments/{id}` | Chỉ xóa comment do mình viết | Chỉ xóa comment do mình viết |

---

## 🔓 Logic kiểm tra quyền đọc chapter

```
stories.free_chapter_limit = NULL  →  Tất cả đọc được (kể cả guest)
stories.free_chapter_limit = 0     →  Toàn bộ chapter cần Premium
stories.free_chapter_limit = N     →  N chap đầu free, từ chap N+1 cần Premium

Ai được đọc chapter Premium?
  ADMIN                                → Luôn được
  UPLOADER (bộ truyện của mình)        → Luôn được
  USER có subscription.status = ACTIVE → Được
  Guest / USER chưa Premium           → Bị chặn, trả 403 + upgradeUrl
```

---

## ⚙️ Spring Security config mẫu

```java
// SecurityConfig.java
http.authorizeHttpRequests(auth -> auth
    // Public — không cần token
    .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
    .requestMatchers(HttpMethod.GET,  "/api/stories", "/api/stories/**").permitAll()
    .requestMatchers(HttpMethod.GET,  "/api/genres").permitAll()
    .requestMatchers(HttpMethod.GET,  "/api/stories/*/comments").permitAll()
    .requestMatchers(HttpMethod.GET,  "/api/payment/return").permitAll()
    .requestMatchers(HttpMethod.GET,  "/api/payment/vnpay-ipn").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/reading-history").permitAll()

    // Tất cả còn lại yêu cầu đăng nhập
    .anyRequest().authenticated()
);

// Trên từng method dùng @PreAuthorize:
@PreAuthorize("hasAuthority('story:create')")
@PreAuthorize("hasAuthority('story:approve')")
@PreAuthorize("hasAnyAuthority('story:delete_own','story:delete_any')")
@PreAuthorize("hasAuthority('user:read')")
@PreAuthorize("hasAuthority('user:manage_roles')")
@PreAuthorize("hasAuthority('payment:manage')")
@PreAuthorize("hasAuthority('comment:create')")
@PreAuthorize("hasAnyAuthority('comment:delete_own','comment:delete_any')")
```

---

## 🗂️ Tổng quan permissions theo role

| Permission             | ADMIN | UPLOADER | USER |
| ---------------------- | :---: | :------: | :--: |
| `user:read`            |  ✅   |    ❌    |  ❌  |
| `user:ban`             |  ✅   |    ❌    |  ❌  |
| `user:manage_roles`    |  ✅   |    ❌    |  ❌  |
| `story:create`         |  ✅   |    ✅    |  ❌  |
| `story:update_own`     |  ✅   |    ✅    |  ❌  |
| `story:update_any`     |  ✅   |    ❌    |  ❌  |
| `story:delete_own`     |  ✅   |    ✅    |  ❌  |
| `story:delete_any`     |  ✅   |    ❌    |  ❌  |
| `story:approve`        |  ✅   |    ❌    |  ❌  |
| `chapter:create`       |  ✅   |    ✅    |  ❌  |
| `chapter:update_own`   |  ✅   |    ✅    |  ❌  |
| `chapter:delete_own`   |  ✅   |    ✅    |  ❌  |
| `chapter:read_premium` |  ✅   |   ✅\*   | ✅\* |
| `comment:create`       |  ✅   |    ✅    |  ✅  |
| `comment:delete_own`   |  ✅   |    ✅    |  ✅  |
| `comment:delete_any`   |  ✅   |    ❌    |  ❌  |
| `payment:manage`       |  ✅   |    ❌    |  ❌  |
| `subscription:buy`     |  ✅   |    ✅    |  ✅  |

> \* `chapter:read_premium`: UPLOADER chỉ áp dụng cho bộ của mình · USER cần subscription active

---

---

## 📋 API Documentation

### 🔐 Auth Module

#### POST `/api/auth/login`
- **Mô tả**: Đăng nhập người dùng
- **Permission**: Public
- **Role**: Tất cả
- **Body**:
  ```json
  {
    "email": "string (required, email format)",
    "password": "string (required, min 6 characters)"
  }
  ```
- **Response**: `LoginResponse` (accessToken, refreshToken, ...)

#### POST `/api/auth/register`
- **Mô tả**: Đăng ký tài khoản mới
- **Permission**: Public
- **Role**: Tất cả
- **Body**:
  ```json
  {
    "email": "string (required, email format)",
    "username": "string (required, 4-50 characters)",
    "password": "string (required, min 6 characters)"
  }
  ```
- **Response**: `RegisterResponse`

#### POST `/api/auth/introspect`
- **Mô tả**: Kiểm tra tính hợp lệ của token
- **Permission**: Public
- **Role**: Tất cả
- **Body**:
  ```json
  {
    "token": "string"
  }
  ```
- **Response**: `IntrospectResponse` (valid, expired, ...)

---

### 👤 Users Module

#### GET `/api/users/me`
- **Mô tả**: Lấy thông tin người dùng hiện tại
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`
- **Response**: `UserResponse`

#### PATCH `/api/users/me`
- **Mô tả**: Cập nhật thông tin người dùng hiện tại
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`
- **Body**:
  ```json
  {
    "username": "string (optional, 4-50 characters)",
    "avatarUrl": "string (optional)",
    "email": "string (optional, email format)"
  }
  ```
- **Response**: `UserResponse`

#### GET `/api/users`
- **Mô tả**: Lấy danh sách tất cả người dùng (Admin)
- **Permission**: `user:read`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Query Params**:
  - `page` (int, default: 1)
  - `size` (int, default: 10)
- **Response**: `PageResponse<UserResponse>`

#### GET `/api/users/{userId}`
- **Mô tả**: Lấy thông tin người dùng theo ID (Admin)
- **Permission**: `user:read`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `userId` (string)
- **Response**: `UserResponse`

#### PATCH `/api/users/{userId}/ban`
- **Mô tả**: Ban người dùng (Admin)
- **Permission**: `user:ban`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `userId` (string)
- **Response**: `String` (message)

#### POST `/api/users/{userId}/roles`
- **Mô tả**: Cập nhật roles cho người dùng (Admin)
- **Permission**: `user:manage_roles`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `userId` (string)
- **Body**:
  ```json
  {
    "roles": [1, 2] // List of role IDs (required, not empty)
  }
  ```
- **Response**: `UserResponse`

---

### 📚 Stories Module

#### GET `/api/stories`
- **Mô tả**: Lấy danh sách truyện với bộ lọc
- **Permission**: Public
- **Role**: Tất cả
- **Query Params**:
  - `page` (int, default: 1)
  - `size` (int, default: 10)
  - `search` (string, required)
  - `type` (StoryType, required: COMICS, MANHWA, MANHUA, NOVEL)
  - `status` (StoryStatus, required: ONGOING, COMPLETED, HIATUS, CANCELLED)
  - `isPublished` (boolean, required)
- **Response**: `PageResponse<StoryResponse>`

#### GET `/api/stories/{slug}`
- **Mô tả**: Lấy thông tin truyện theo slug
- **Permission**: Public
- **Role**: Tất cả
- **Path Variable**: `slug` (string)
- **Response**: `StoryResponse`

#### POST `/api/stories`
- **Mô tả**: Tạo truyện mới
- **Permission**: `story:create`
- **Role**: ADMIN, UPLOADER
- **Headers**: `Authorization: Bearer {token}`
- **Body**:
  ```json
  {
    "title": "string (required)",
    "description": "string (optional)",
    "coverImageUrl": "string (optional)",
    "storyType": "COMICS|MANHWA|MANHUA|NOVEL (required)",
    "status": "ONGOING|COMPLETED|HIATUS|CANCELLED (default: ONGOING)",
    "isPublished": "boolean (default: false)",
    "freeChapterLimit": "integer (optional)",
    "viewCount": "long (default: 0)"
  }
  ```
- **Response**: `StoryResponse`

#### PATCH `/api/stories/{id}`
- **Mô tả**: Cập nhật truyện
- **Permission**: `story:update_own` / `story:update_any`
- **Role**: ADMIN (update_any), UPLOADER (update_own - chỉ truyện của mình)
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string)
- **Body**:
  ```json
  {
    "title": "string (optional)",
    "description": "string (optional)",
    "coverImageUrl": "string (optional)",
    "storyType": "COMICS|MANHWA|MANHUA|NOVEL (optional)",
    "status": "ONGOING|COMPLETED|HIATUS|CANCELLED (optional)",
    "isPublished": "boolean (optional)",
    "freeChapterLimit": "integer (optional)",
    "viewCount": "long (optional)"
  }
  ```
- **Response**: `StoryResponse`

#### DELETE `/api/stories/{id}`
- **Mô tả**: Xóa truyện
- **Permission**: `story:delete_own` / `story:delete_any`
- **Role**: ADMIN (delete_any), UPLOADER (delete_own - chỉ truyện của mình)
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string)
- **Response**: `String` (message)

#### GET `/api/stories/publish-requests`
- **Mô tả**: Lấy danh sách yêu cầu xuất bản (Admin)
- **Permission**: `ADMIN` role
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Query Params**:
  - `page` (int, default: 1)
  - `size` (int, default: 10)
  - `storyId` (string, required)
  - `status` (StoryPublishRequestStatus, required)
  - `uploaderId` (string, required)
- **Response**: `PageResponse<StoryPublishRequestResponse>`

#### GET `/api/stories/publish-requests/me`
- **Mô tả**: Lấy danh sách yêu cầu xuất bản của mình
- **Permission**: `story:update_own`
- **Role**: UPLOADER
- **Headers**: `Authorization: Bearer {token}`
- **Query Params**:
  - `page` (int, default: 1)
  - `size` (int, default: 10)
  - `storyId` (string, required)
  - `status` (StoryPublishRequestStatus, required)
- **Response**: `PageResponse<StoryPublishRequestResponse>`

#### PATCH `/api/stories/publish-requests/approve/{id}`
- **Mô tả**: Phê duyệt yêu cầu xuất bản (Admin)
- **Permission**: `ADMIN` role
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string)
- **Body**:
  ```json
  {
    "reviewerNote": "string (optional)"
  }
  ```
- **Response**: `StoryPublishRequestResponse`

#### PATCH `/api/stories/publish-requests/reject/{id}`
- **Mô tả**: Từ chối yêu cầu xuất bản (Admin)
- **Permission**: `ADMIN` role
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string)
- **Body**:
  ```json
  {
    "reviewerNote": "string (optional)"
  }
  ```
- **Response**: `StoryPublishRequestResponse`

---

### 📖 Chapters Module

#### GET `/api/stories/{slug}/chapters`
- **Mô tả**: Lấy danh sách chương theo slug truyện
- **Permission**: Public
- **Role**: Tất cả
- **Path Variable**: `slug` (string)
- **Query Params**:
  - `page` (int, default: 1)
  - `size` (int, default: 10)
  - `search` (string, required)
- **Response**: `PageResponse<ChapterResponse>`

#### GET `/api/chapters/{id}`
- **Mô tả**: Lấy thông tin chương theo ID
- **Permission**: Public (có giới hạn dựa vào story settings)
- **Role**: Tất cả
- **Path Variable**: `id` (string)
- **Logic**:
  - ADMIN: Luôn được đọc
  - UPLOADER: Đọc premium nếu là truyện của mình
  - USER: Đọc nếu trong giới hạn free hoặc có subscription active
  - Guest: Bị chặn nếu cần premium
- **Response**: `ChapterResponse`

#### POST `/api/stories/{id}/chapters`
- **Mô tả**: Tạo chương mới cho truyện
- **Permission**: `chapter:create`
- **Role**: ADMIN, UPLOADER
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string - story ID)
- **Body**:
  ```json
  {
    "chapterNumber": "BigDecimal (optional)",
    "title": "string (optional)",
    "isPublished": "boolean (default: false)",
    "pageCount": "integer (optional)",
    "content": "string (optional)",
    "pages": "List<MultipartFile> (optional)"
  }
  ```
- **Response**: `ChapterResponse`

#### PATCH `/api/chapters/{id}`
- **Mô tả**: Cập nhật chương
- **Permission**: `chapter:update_own`
- **Role**: ADMIN, UPLOADER (chỉ chapter thuộc truyện của mình)
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string - chapter ID)
- **Body**:
  ```json
  {
    "chapterNumber": "BigDecimal (optional)",
    "title": "string (optional)",
    "isPublished": "boolean (optional)",
    "content": "string (optional)",
    "pageCount": "integer (optional)",
    "pages": "List<MultipartFile> (optional)"
  }
  ```
- **Response**: `ChapterResponse`

#### DELETE `/api/chapter{id}`
- **Mô tả**: Xóa chương
- **Permission**: `chapter:delete_own`
- **Role**: ADMIN, UPLOADER (chỉ chapter thuộc truyện của mình)
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string - chapter ID)
- **Response**: `String` (message)

---

### 🖼️ Chapter Pages Module

#### GET `/api/chapters/{id}/pages`
- **Mô tả**: Lấy danh sách trang của chương
- **Permission**: `chapter:read_premium` (nếu chapter bị khóa)
- **Role**: Tất cả (có điều kiện)
- **Path Variable**: `id` (string - chapter ID)
- **Logic**:
  - ADMIN: Luôn được đọc
  - UPLOADER: Đọc nếu là truyện của mình
  - USER: Đọc nếu trong giới hạn free hoặc có subscription active
- **Response**: `List<ChapterPageResponse>`

#### POST `/api/chapters/pages`
- **Mô tả**: Tạo trang cho chương (upload ảnh)
- **Permission**: `chapter:create`
- **Role**: ADMIN, UPLOADER
- **Headers**: `Authorization: Bearer {token}`, `Content-Type: multipart/form-data`
- **Body** (multipart/form-data):
  ```json
  {
    "chapterPageRequests": [
      {
        "chapterId": "string",
        "pageNumber": "integer",
        "imageUrl": "string",
        "width": "integer (optional)",
        "height": "integer (optional)"
      }
    ],
    "chapterId": "string"
  }
  ```
- **Response**: `List<ChapterPageResponse>`

#### PATCH `/api/chapters/pages`
- **Mô tả**: Cập nhật trang chương
- **Permission**: `chapter:update_own`
- **Role**: ADMIN, UPLOADER
- **Headers**: `Authorization: Bearer {token}`, `Content-Type: multipart/form-data`
- **Body** (multipart/form-data):
  ```json
  {
    "chapterPageRequests": [
      {
        "id": "string (existing page id)",
        "chapterId": "string",
        "pageNumber": "integer",
        "imageUrl": "string",
        "isNewPage": "boolean",
        "width": "integer (optional)",
        "height": "integer (optional)"
      }
    ],
    "chapterId": "string"
  }
  ```
- **Response**: `List<ChapterPageResponse>`

#### DELETE `/api/chapters/{id}/page`
- **Mô tả**: Xóa tất cả trang của chương
- **Permission**: `chapter:delete_own`
- **Role**: ADMIN, UPLOADER (chỉ chapter thuộc truyện của mình)
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string - chapter ID)
- **Response**: `String` (message)

---

### 🏷️ Genres Module

#### GET `/api/genres`
- **Mô tả**: Lấy danh sách thể loại
- **Permission**: Public
- **Role**: Tất cả
- **Query Params**:
  - `search` (string, required)
- **Response**: `List<GenreResponse>`

#### POST `/api/genres`
- **Mô tả**: Tạo thể loại mới (Admin)
- **Permission**: `ADMIN` role
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Body**:
  ```json
  {
    "name": "string"
  }
  ```
- **Response**: `GenreResponse`

#### PATCH `/api/genres/{id}`
- **Mô tả**: Cập nhật thể loại (Admin)
- **Permission**: `ADMIN` role
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string)
- **Body**:
  ```json
  {
    "name": "string"
  }
  ```
- **Response**: `GenreResponse`

#### DELETE `/api/genres/{id}`
- **Mô tả**: Xóa thể loại (Admin)
- **Permission**: `ADMIN` role
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string)
- **Response**: `String` (message)

---

### 📜 Reading History Module

#### GET `/api/reading-histories`
- **Mô tả**: Lấy lịch sử đọc của người dùng hiện tại
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`, `Session-Id` (optional cho guest)
- **Query Params**:
  - `page` (int, default: 1)
  - `size` (int, default: 10)
- **Response**: `PageResponse<ReadingHistoryResponse>`

#### POST `/api/reading-histories`
- **Mô tả**: Tạo lịch sử đọc mới (guest + user)
- **Permission**: Public
- **Role**: Tất cả
- **Headers**: `Session-Id` (optional)
- **Body**:
  ```json
  {
    "chapterId": "string (required)",
    "storyId": "string (required)"
  }
  ```
- **Response**: `ReadingHistoryResponse`

#### PATCH `/api/reading-histories`
- **Mô tả**: Cập nhật lịch sử đọc (guest + user)
- **Permission**: Public
- **Role**: Tất cả
- **Headers**: `Session-Id` (optional)
- **Body**:
  ```json
  {
    "chapterId": "string (required)",
    "storyId": "string (required)"
  }
  ```
- **Response**: `ReadingHistoryResponse`

#### DELETE `/api/reading-histories`
- **Mô tả**: Xóa lịch sử đọc
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`, `Session-Id` (optional)
- **Response**: `String` (message)

---

### 🔖 Bookmarks Module

#### GET `/api/bookmarks`
- **Mô tả**: Lấy bookmark theo story ID
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`
- **Query Params**:
  - `storyId` (string, required)
- **Response**: `BookmarkResponse`

#### POST `/api/bookmarks`
- **Mô tả**: Tạo bookmark mới
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`
- **Query Params**:
  - `storyId` (string, required)
- **Response**: `BookmarkResponse`

#### DELETE `/api/bookmarks`
- **Mô tả**: Xóa bookmark
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`
- **Query Params**:
  - `storyId` (string, required)
- **Response**: `String` (message)

---

### 💬 Comments Module

#### GET `/api/comments`
- **Mô tả**: Lấy danh sách bình luận theo chương
- **Permission**: Public
- **Role**: Tất cả
- **Query Params**:
  - `page` (int, default: 1)
  - `size` (int, default: 10)
  - `chapterId` (string, required)
- **Response**: `PageResponse<CommentResponse>`

#### POST `/api/comments`
- **Mô tả**: Tạo bình luận mới
- **Permission**: `comment:create`
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`
- **Body**:
  ```json
  {
    "chapterId": "string",
    "content": "string",
    "parentId": "string (optional, cho reply comment)"
  }
  ```
- **Response**: `CommentResponse`

#### PATCH `/api/comments/{id}`
- **Mô tả**: Cập nhật bình luận
- **Permission**: `comment:create`
- **Role**: ADMIN, UPLOADER, USER (chỉ comment của mình)
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string - comment ID)
- **Body**:
  ```json
  {
    "content": "string"
  }
  ```
- **Response**: `CommentResponse`

#### DELETE `/api/comments/{id}`
- **Mô tả**: Xóa bình luận
- **Permission**: `comment:delete_own` / `comment:delete_any`
- **Role**: ADMIN (delete_any), UPLOADER/USER (delete_own - chỉ comment của mình)
- **Headers**: `Authorization: Bearer {token}`
- **Path Variable**: `id` (string - comment ID)
- **Response**: `String` (message)

---

### 💳 Payment & Subscription Module

> **Lưu ý**: Các endpoint thanh toán chưa được implement trong codebase hiện tại. Thông tin dưới đây dựa trên permission matrix trong tài liệu.

#### POST `/api/payment/create`
- **Mô tả**: Tạo yêu cầu thanh toán VNPay
- **Permission**: `subscription:buy`
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`
- **Body**: (phụ thuộc implementation)
- **Response**: Payment URL hoặc response details

#### GET `/api/payment/return`
- **Mô tả**: VNPay redirect sau khi thanh toán
- **Permission**: Public (VNPay redirect)
- **Role**: Tất cả
- **Query Params**: VNPay parameters

#### GET `/api/payment/vnpay-ipn`
- **Mô tả**: VNPay IPN (Instant Payment Notification)
- **Permission**: Public (VNPay server)
- **Role**: VNPay server only
- **Query Params**: VNPay IPN parameters

#### GET `/api/payment/transactions`
- **Mô tả**: Lấy danh sách giao dịch (Admin)
- **Permission**: `payment:manage`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`

#### GET `/api/payment/transactions/me`
- **Mô tả**: Lấy danh sách giao dịch của mình
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`

#### GET `/api/subscriptions/me`
- **Mô tả**: Lấy thông tin subscription của người dùng hiện tại
- **Permission**: Authenticated
- **Role**: ADMIN, UPLOADER, USER
- **Headers**: `Authorization: Bearer {token}`

---

### 🛠️ Admin Module

> **Lưu ý**: Các endpoint admin có thể nằm trong các controller riêng biệt. Thông tin dưới đây dựa trên permission matrix.

#### GET `/api/admin/roles`
- **Mô tả**: Lấy danh sách roles
- **Permission**: `user:manage_roles`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`

#### GET `/api/admin/permissions`
- **Mô tả**: Lấy danh sách permissions
- **Permission**: `user:manage_roles`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`

#### GET `/api/admin/dashboard`
- **Mô tả**: Lấy dashboard stats
- **Permission**: `user:read`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`

#### GET `/api/admin/stories/pending`
- **Mô tả**: Lấy danh sách truyện chờ duyệt
- **Permission**: `story:approve`
- **Role**: ADMIN
- **Headers**: `Authorization: Bearer {token}`

---

### 📝 Common Enum Values

#### StoryType
- `COMICS`
- `MANHWA`
- `MANHUA`
- `NOVEL`

#### StoryStatus
- `ONGOING`
- `COMPLETED`
- `HIATUS`
- `CANCELLED`

#### StoryPublishRequestStatus
- `PENDING`
- `APPROVED`
- `REJECTED`

#### SubscriptionStatus
- `ACTIVE`
- `EXPIRED`
- `CANCELLED`
- `PENDING`

#### Roles
- `1` = USER
- `2` = UPLOADER
- `3` = ADMIN

---

_TruyệnOnline API Permission Matrix | v4.0 | 3 roles | Tháng 6/2025_

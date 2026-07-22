# TruyenOnline - API Endpoints & Permission Matrix v5.1

> **3 Role:** `ADMIN` | `UPLOADER` | `USER`
>
> **Ký hiệu:**
>
> - Public - khong can token
> - Authenticated - can token
> - Permission - can permission cu the

---

# TOM TAT QUYEN TRUY CAP

## Bang tong hop quyen theo role

| Permission              | ADMIN | UPLOADER | USER |
| ---------------------- | ----- | -------- | ---- |
| `user:read`            | Yes   | No       | No   |
| `user:ban`             | Yes   | No       | No   |
| `user:manage_roles`    | Yes   | No       | No   |
| `story:create`         | Yes   | Yes      | No   |
| `story:update_own`     | Yes   | Yes      | No   |
| `story:update_any`     | Yes   | No       | No   |
| `story:delete_own`     | Yes   | Yes      | No   |
| `story:delete_any`     | Yes   | No       | No   |
| `story:approve`        | Yes   | No       | No   |
| `chapter:create`       | Yes   | Yes      | No   |
| `chapter:update_own`   | Yes   | Yes      | No   |
| `chapter:delete_own`   | Yes   | Yes      | No   |
| `chapter:read_premium` | Yes   | Yes      | Yes  |
| `comment:create`       | Yes   | Yes      | Yes  |
| `comment:delete_own`  | Yes   | Yes      | Yes  |
| `comment:delete_any`  | Yes   | No       | No   |
| `subscription:buy`    | Yes   | Yes      | Yes  |

> **Ghi chú:** `chapter:read_premium` - UPLOADER chi ap dung cho bo cua minh, USER can subscription active

---

# DANH SACH API

## Bang tong hop cac endpoint

### Auth Module (`/auth`)

| Method | Endpoint           | Permission | ADMIN | UPLOADER | USER |
| ------ | ------------------ | ---------- | ----- | -------- | ---- |
| POST   | `/auth/login`      | Public     | Yes   | Yes      | Yes  |
| POST   | `/auth/register`   | Public     | Yes   | Yes      | Yes  |
| POST   | `/auth/refresh`   | Public     | Yes   | Yes      | Yes  |
| POST   | `/auth/introspect` | Public     | Yes   | Yes      | Yes  |

### User Module (`/api/users`)

| Method | Endpoint                  | Permission          | ADMIN | UPLOADER | USER |
| ------ | ------------------------- | ------------------- | ----- | -------- | ---- |
| GET    | `/api/users/me`           | Authenticated       | Yes   | Yes      | Yes  |
| PATCH  | `/api/users/me`           | Authenticated       | Yes   | Yes      | Yes  |
| GET    | `/api/users`              | `user:read`         | Yes   | No       | No   |
| GET    | `/api/users/{userId}`     | `user:read`         | Yes   | No       | No   |
| PATCH  | `/api/users/{id}/ban`     | `user:ban`          | Yes   | No       | No   |
| PATCH  | `/api/users/{id}/unban`   | `user:ban`          | Yes   | No       | No   |
| POST   | `/api/users/{userId}/roles` | `user:manage_roles` | Yes   | No       | No   |

### Story Module (`/api/stories`)

| Method | Endpoint                                      | Permission                            | ADMIN | UPLOADER | USER |
| ------ | --------------------------------------------- | ------------------------------------- | ----- | -------- | ---- |
| GET    | `/api/stories`                                | Public                                | Yes   | Yes      | Yes  |
| GET    | `/api/stories/{slug}`                         | Public                                | Yes   | Yes      | Yes  |
| POST   | `/api/stories`                                | `story:create`                        | Yes   | Yes      | No   |
| PATCH  | `/api/stories/{id}`                           | `story:update_own` / `story:update_any` | Yes   | Own only | No   |
| DELETE | `/api/stories/{id}`                           | `story:delete_own` / `story:delete_any` | Yes   | Own only | No   |
| POST   | `/api/stories/{id}/publish-requests`          | `story:create`                        | Yes   | Yes      | No   |
| GET    | `/api/stories/publish-requests`               | ADMIN role                            | Yes   | No       | No   |
| GET    | `/api/stories/publish-requests/me`             | `story:update_own`                    | Yes   | Yes      | No   |
| PATCH  | `/api/stories/publish-requests/approve/{id}`   | ADMIN role                            | Yes   | No       | No   |
| PATCH  | `/api/stories/publish-requests/reject/{id}`   | ADMIN role                            | Yes   | No       | No   |
| DELETE | `/api/stories/publish-requests/{id}`           | `story:delete_own` / `story:delete_any` | Yes   | Own only | No   |
| PATCH  | `/api/stories/{id}/ban`                       | ADMIN role                            | Yes   | No       | No   |
| PATCH  | `/api/stories/{id}/unban`                     | ADMIN role                            | Yes   | No       | No   |

### Genre Module (`/api/genres`)

| Method | Endpoint           | Permission | ADMIN | UPLOADER | USER |
| ------ | ----------------- | ---------- | ----- | -------- | ---- |
| GET    | `/api/genres`     | Public     | Yes   | Yes      | Yes  |
| POST   | `/api/genres`     | ADMIN      | Yes   | No       | No   |
| PATCH  | `/api/genres/{id}` | ADMIN      | Yes   | No       | No   |
| DELETE | `/api/genres/{id}` | ADMIN      | Yes   | No       | No   |

### Chapter Module (`/api`)

| Method | Endpoint                                | Permission              | ADMIN | UPLOADER | USER |
| ------ | --------------------------------------- | ---------------------- | ----- | -------- | ---- |
| GET    | `/api/stories/{slug}/chapters`          | Public                 | Yes   | Yes      | Yes  |
| GET    | `/api/chapters/{id}`                    | Public (limited)       | Yes   | Limited  | Limited |
| POST   | `/api/stories/{id}/chapters`            | `chapter:create`       | Yes   | Yes      | No   |
| PATCH  | `/api/chapters/{id}`                    | `chapter:update_own`   | Yes   | Own only | No   |
| PATCH  | `/api/chapters/{id}/update-content`     | `chapter:update_own`   | Yes   | Own only | No   |
| PATCH  | `/api/chapters/update-publish-status`    | `chapter:update_own`   | Yes   | Own only | No   |
| DELETE | `/api/chapter/{id}`                     | `chapter:delete_own`   | Yes   | Own only | No   |
| PATCH  | `/api/{id}/ban`                        | ADMIN role             | Yes   | No       | No   |
| PATCH  | `/api/{id}/unban`                      | ADMIN role             | Yes   | No       | No   |

### Chapter Page Module (`/api/chapters`)

| Method | Endpoint                         | Permission              | ADMIN | UPLOADER | USER |
| ------ | -------------------------------- | ---------------------- | ----- | -------- | ---- |
| GET    | `/api/chapters/{id}/pages`       | Public (limited)       | Yes   | Limited  | Limited |
| POST   | `/api/chapters/pages`             | `chapter:create`       | Yes   | Yes      | No   |
| PATCH  | `/api/chapters/pages`             | `chapter:update_own`    | Yes   | Yes      | No   |
| DELETE | `/api/chapters/{id}/page`         | `chapter:delete_own`   | Yes   | Own only | No   |

### Bookmark Module (`/api/bookmarks`)

| Method | Endpoint                  | Permission    | ADMIN | UPLOADER | USER |
| ------ | ------------------------- | ------------- | ----- | -------- | ---- |
| GET    | `/api/bookmarks`           | Authenticated | Yes   | Yes      | Yes  |
| POST   | `/api/bookmarks`           | Authenticated | Yes   | Yes      | Yes  |
| DELETE | `/api/bookmarks`           | Authenticated | Yes   | Yes      | Yes  |

### Comment Module (`/api/comments`)

| Method | Endpoint                     | Permission              | ADMIN | UPLOADER | USER |
| ------ | ---------------------------- | ---------------------- | ----- | -------- | ---- |
| GET    | `/api/comments/chapter/{id}` | Public                 | Yes   | Yes      | Yes  |
| GET    | `/api/comments/story/{id}`    | Public                 | Yes   | Yes      | Yes  |
| POST   | `/api/comments`              | `comment:create`       | Yes   | Yes      | Yes  |
| PATCH  | `/api/comments/{id}`         | `comment:create`       | Yes   | Own only | Own only |
| DELETE | `/api/comments/{id}`         | `comment:delete_own`   | Yes   | Own only | Own only |

### Reading History Module (`/api/reading-histories`)

| Method | Endpoint                          | Permission              | ADMIN | UPLOADER | USER |
| ------ | --------------------------------- | ---------------------- | ----- | -------- | ---- |
| GET    | `/api/reading-histories`           | Public (guest + user) | Yes   | Yes      | Yes  |
| GET    | `/api/reading-histories/story/{id}` | Public (guest + user) | Yes   | Yes      | Yes  |
| POST   | `/api/reading-histories`           | Public (guest + user) | Yes   | Yes      | Yes  |
| DELETE | `/api/reading-histories`           | Public (guest + user) | Yes   | Yes      | Yes  |

### Audit Log Module (`/api/audit-logs`)

| Method | Endpoint           | Permission    | ADMIN | UPLOADER | USER |
| ------ | ----------------- | ------------- | ----- | -------- | ---- |
| GET    | `/api/audit-logs` | Authenticated | Yes   | Yes      | Yes  |

### Moderation Action Module (`/api/moderation-actions`)

| Method | Endpoint                        | Permission    | ADMIN | UPLOADER | USER |
| ------ | ------------------------------- | ------------- | ----- | -------- | ---- |
| GET    | `/api/moderation-actions`       | Authenticated | Yes   | Yes      | Yes  |

---

# CHI TIET API

## COMMON - Cau truc chung

### ApiResponse<T>

| Field  | Type | Description           |
| ------ | ---- | --------------------- |
| `code` | int  | HTTP status code      |
| `message` | String | Response message  |
| `data` | T    | Response data         |

### PageResponse<T>

| Field          | Type    | Description                    |
| -------------- | ------- | ------------------------------ |
| `currentPage`  | int     | Current page number (1-based)  |
| `pageSize`     | int     | Number of items per page       |
| `totalPages`   | int     | Total number of pages          |
| `totalElements`| long    | Total number of items          |
| `data`         | List<T> | List of items                  |

---

## AUTH MODULE

### POST `/auth/login`

**Mo ta**: Dang nhap nguoi dung

**Permission**: Public
**Role**: Tat ca

**Headers**:
- `Session-Id` (Optional - guest session, neu khong co se tu tao moi)

**Request Body** (`LoginRequest`):

| Field      | Type   | Required | Description                  |
| ---------- | ------ | -------- | ---------------------------- |
| `email`    | String | Yes     | Email (dinh dang email)       |
| `password` | String | Yes     | Mat khau (toi thieu 6 ky tu)  |

**Example Request**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** (`ApiResponse<LoginResponse>`):

| Field          | Type         | Description              |
| -------------- | ------------ | ------------------------ |
| `id`           | String       | User ID (UUID)           |
| `email`        | String       | Email                    |
| `username`     | String       | Username                 |
| `avatarUrl`    | String       | Avatar URL               |
| `roles`        | List<String> | List of role names       |
| `permissions`  | List<String> | List of permissions      |
| `accessToken`  | String       | JWT access token         |
| `refreshToken` | String       | JWT refresh token        |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "username": "johndoe",
    "avatarUrl": "https://example.com/avatar.jpg",
    "roles": ["UPLOADER"],
    "permissions": ["story:create", "chapter:create"],
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Luu y**:
- Neu truyen `Session-Id` header, he thong se link guest session voi tai khoan sau khi dang nhap thanh cong.
- Neu khong truyen `Session-Id`, he thong se tao session moi cho nguoi dung.

---

### POST `/auth/register`

**Mo ta**: Dang ky tai khoan moi

**Permission**: Public
**Role**: Tat ca

**Request Body** (`RegisterRequest`):

| Field      | Type   | Required | Description                  |
| ---------- | ------ | -------- | ---------------------------- |
| `email`    | String | Yes     | Email (dinh dang email)       |
| `username` | String | Yes     | Username (4-50 ky tu)          |
| `password` | String | Yes     | Mat khau (toi thieu 6 ky tu)  |

**Example Request**:
```json
{
  "email": "newuser@example.com",
  "username": "newuser",
  "password": "password123"
}
```

**Response** (`ApiResponse<RegisterResponse>`):

| Field      | Type   | Description     |
| ---------- | ------ | --------------- |
| `id`       | String | User ID (UUID) |
| `email`    | String | Email          |
| `username` | String | Username       |

**Example Response**:
```json
{
  "code": 201,
  "message": "Success",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "email": "newuser@example.com",
    "username": "newuser"
  }
}
```

---

### POST `/auth/refresh`

**Mo ta**: Lam moi access token

**Permission**: Public
**Role**: Tat ca

**Request Body** (`RefreshTokenRequest`):

| Field          | Type   | Required | Description      |
| -------------- | ------ | -------- | ---------------- |
| `refreshToken` | String | Yes     | Refresh token    |

**Example Request**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response** (`ApiResponse<RefreshTokenResponse>`):

| Field          | Type   | Description     |
| -------------- | ------ | --------------- |
| `accessToken`  | String | New access token |
| `refreshToken` | String | New refresh token |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

### POST `/auth/introspect`

**Mo ta**: Kiem tra tinh hop le cua token

**Permission**: Public
**Role**: Tat ca

**Request Body** (`IntrospectRequest`):

| Field  | Type   | Required | Description     |
| ------ | ------ | -------- | --------------- |
| `token` | String | Yes     | Token can kiem tra |

**Example Request**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response** (`ApiResponse<IntrospectResponse>`):

| Field   | Type    | Description              |
| ------- | ------- | ------------------------ |
| `valid` | boolean | Token co hop le khong     |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "valid": true
  }
}
```

---

## USER MODULE

### GET `/api/users/me`

**Mo ta**: Lay thong tin nguoi dung hien tai

**Permission**: Authenticated
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Response** (`ApiResponse<UserResponse>`):

| Field       | Type       | Description      |
| ----------- | ---------- | ---------------- |
| `id`        | String     | User ID (UUID)   |
| `email`     | String     | Email            |
| `username`  | String     | Username         |
| `avatarUrl` | String     | Avatar URL       |
| `isActive`  | boolean    | Trang thai hoat dong |
| `roles`     | Set<Role>  | Tap hop role     |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "username": "johndoe",
    "avatarUrl": "https://example.com/avatar.jpg",
    "isActive": true,
    "roles": [
      {"id": 2, "name": "UPLOADER", "description": "Content uploader"}
    ]
  }
}
```

---

### PATCH `/api/users/me`

**Mo ta**: Cap nhat thong tin nguoi dung hien tai

**Permission**: Authenticated
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Request Body** (`UserUpdateRequest`):

| Field       | Type   | Required | Description                |
| ----------- | ------ | -------- | -------------------------- |
| `username` | String | No      | Username (4-50 ky tu)       |
| `avatarUrl` | String | No      | Avatar URL                  |
| `email`    | String | No      | Email (dinh dang email)      |

**Example Request**:
```json
{
  "username": "newname",
  "avatarUrl": "https://example.com/new-avatar.jpg"
}
```

**Response** (`ApiResponse<UserResponse>`): Xem GET `/api/users/me`

---

### GET `/api/users`

**Mo ta**: Lay danh sach tat ca nguoi dung (Admin)

**Permission**: `user:read`
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Query Params**:

| Field | Type    | Required | Default | Description           |
| ----- | ------- | -------- | ------- | --------------------- |
| `page`| Integer | No       | 1       | So trang              |
| `size`| Integer | No       | 10      | So phan tu tren trang |

**Response** (`ApiResponse<PageResponse<UserResponse>>`): Xem cau truc UserResponse o tren

---

### GET `/api/users/{userId}`

**Mo ta**: Lay thong tin nguoi dung theo ID (Admin)

**Permission**: `user:read`
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field    | Type   | Description     |
| -------- | ------ | --------------- |
| `userId` | String | User ID (UUID)  |

**Response** (`ApiResponse<UserResponse>`): Xem cau truc UserResponse o tren

---

### PATCH `/api/users/{id}/ban`

**Mo ta**: Ban nguoi dung (Admin)

**Permission**: `user:ban`
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | User ID      |

**Request Body** (`UserBanRequest`):

| Field          | Type         | Required | Description              |
| -------------- | ------------ | -------- | ------------------------ |
| `violationType`| ViolationType| No       | Loai vi pham             |
| `reason`       | String       | Yes      | Ly do ban (toi da 1000 ky tu) |

**ViolationType enum values**: `COPYRIGHT`, `PORNOGRAPHY`, `VIOLENCE`, `SPAM`, `HARASSMENT`, `OTHER`

**Example Request**:
```json
{
  "violationType": "SPAM",
  "reason": "Spam comments on multiple chapters"
}
```

**Response** (`ApiResponse<UserResponse>`): Xem cau truc UserResponse o tren

---

### PATCH `/api/users/{id}/unban`

**Mo ta**: Unban nguoi dung (Admin)

**Permission**: `user:ban`
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | User ID      |

**Request Body** (`UserUnbanRequest`):

| Field   | Type   | Required | Description                 |
| ------- | ------ | -------- | --------------------------- |
| `reason`| String | Yes      | Ly do unban (toi da 1000 ky tu) |

**Example Request**:
```json
{
  "reason": "User apologized and deleted spam content"
}
```

**Response** (`ApiResponse<UserResponse>`): Xem cau truc UserResponse o tren

---

### POST `/api/users/{userId}/roles`

**Mo ta**: Cap nhat roles cho nguoi dung (Admin)

**Permission**: `user:manage_roles`
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field     | Type   | Description  |
| --------- | ------ | ------------ |
| `userId`  | String | User ID      |

**Request Body** (`UserUpdateRoleRequest`):

| Field  | Type        | Required | Description                |
| ------ | ----------- | -------- | -------------------------- |
| `roles`| List<Integer>| Yes     | Danh sach role IDs          |

**Role IDs**:
- `1` = USER
- `2` = UPLOADER
- `3` = ADMIN

**Example Request**:
```json
{
  "roles": [1, 2]
}
```

**Response** (`ApiResponse<UserResponse>`): Xem cau truc UserResponse o tren

---

## STORY MODULE

### GET `/api/stories`

**Mo ta**: Lay danh sach truyen voi bo loc, sap xep va loc theo the loai

**Permission**: Public
**Role**: Tat ca

**Query Params**:

| Field   | Type    | Required | Default | Description              |
| ------- | ------- | -------- | ------- | ----------------------- |
| `page`  | Integer | No       | 1       | So trang               |
| `size`  | Integer | No       | 10      | So phan tu tren trang   |

**Request Body** (`StoryFilter`):

| Field        | Type           | Required | Default   | Description                                      |
| ------------ | -------------- | -------- | --------- | ----------------------------------------------- |
| `search`     | String         | No       | -         | Tu khoa tim kiem theo title hoac titleNoAccent |
| `uploaderId` | String         | No       | -         | Loc theo uploader ID (UUID)                    |
| `type`       | StoryType      | No       | -         | Loai truyen                                    |
| `status`     | StoryStatus    | No       | -         | Trang thai truyen                              |
| `isPublished`| Boolean        | No       | -         | Da duyet chua                                  |
| `genres`     | List\<String\> | No       | -         | Loc theo the loai (genre slug), tra ve truyen co it nhat 1 genre trong danh sach |
| `sortType`   | SortType       | No       | NEWEST    | Kieu sap xep                                   |

**StoryType values**: `NOVEL`, `MANGA`

**StoryStatus values**: `ONGOING`, `COMPLETED`, `HIATUS`, `DROPPED`

**SortType values**:
- `NEWEST` - Moi nhat (theo createdAt giam dan) - **default**
- `UPDATED` - Moi cap nhat (theo updatedAt giam dan)
- `VIEW` - Luot xem nhieu nhat (theo viewCount giam dan)
- `FOLLOW` - Nhieu nguoi theo doi nhat (theo so bookmark giam dan)
- `ALPHABET_ASC` - A -> Z (theo title tang dan)
- `ALPHABET_DESC` - Z -> A (theo title giam dan)
- `OLDEST` - Cu nhat (theo createdAt tang dan)

**Example Request** (loc truyen NOVEL, the loai Action hoac Fantasy, sap xep theo luot xem):
```
GET /api/stories?page=1&size=20
Body: { "type": "NOVEL", "genres": ["action", "fantasy"], "sortType": "VIEW" }
```

**Example Request** (tim kiem truyen co tu "dragon", sap xep moi nhat):
```
GET /api/stories
Body: { "search": "dragon", "sortType": "NEWEST" }
```

**Response** (`ApiResponse<PageResponse<StoryResponse>>`):

| Field                | Type              | Description                                 |
| -------------------- | ----------------- | ------------------------------------------- |
| `id`                 | String            | Story ID (UUID)                             |
| `uploader`           | UserResponse      | Thong tin nguoi upload                      |
| `title`              | String            | Tieu de                                     |
| `slug`               | String            | Slug URL                                    |
| `description`        | String            | Mo ta                                      |
| `coverImageUrl`      | String            | URL hinh bia                               |
| `storyType`          | StoryType         | Loai truyen (NOVEL/MANGA)                  |
| `status`             | StoryStatus       | Trang thai                                 |
| `isPublished`        | boolean           | Da duyet chua                              |
| `freeChapterLimit`   | Integer           | So chuong free (null = all free)            |
| `viewCount`          | Long              | Luot xem                                   |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 5,
    "totalElements": 50,
    "data": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "uploader": {
          "id": "550e8400-e29b-41d4-a716-446655440001",
          "username": "uploader1"
        },
        "title": "The Great Story",
        "slug": "the-great-story",
        "description": "An amazing story...",
        "coverImageUrl": "https://example.com/cover.jpg",
        "storyType": "NOVEL",
        "status": "ONGOING",
        "isPublished": true,
        "freeChapterLimit": 5,
        "viewCount": 1000
      }
    ]
  }
}
```

---

### GET `/api/stories/{slug}`

**Mo ta**: Lay thong tin truyen theo slug

**Permission**: Public
**Role**: Tat ca

**Path Variables**:

| Field | Type   | Description      |
| ----- | ------ | ---------------- |
| `slug`| String | Story slug URL   |

**Response** (`ApiResponse<StoryResponse>`): Xem cau truc StoryResponse o tren

---

### POST `/api/stories`

**Mo ta**: Tao truyen moi (multipart/form-data)

**Permission**: `story:create`
**Role**: ADMIN, UPLOADER

**Headers**:
- `Authorization: Bearer {token}` (Required)
- `Content-Type: multipart/form-data`

**Request Body** (multipart/form-data):

| Field              | Type          | Required | Description                          |
| ------------------ | ------------- | -------- | ------------------------------------ |
| `title`            | String        | Yes      | Tieu de truyen                       |
| `description`      | String        | No       | Mo ta truyen                         |
| `coverImageUrl`    | String        | No       | URL hinh bia                         |
| `coverImageFile`   | MultipartFile | No       | File hinh bia (upload)               |
| `storyType`        | StoryType     | Yes      | Loai truyen: NOVEL, MANGA            |
| `status`           | StoryStatus   | No       | Trang thai (default: ONGOING)        |
| `isPublished`      | boolean       | No       | Da duyet chua (default: false)      |
| `freeChapterLimit` | Integer       | No       | So chuong free (null = all, 0 = premium all) |

**Example Request** (multipart):
```
title: "New Story"
description: "An exciting new story..."
storyType: NOVEL
status: ONGOING
isPublished: false
freeChapterLimit: 5
```

**Response** (`ApiResponse<StoryResponse>`): Xem cau truc StoryResponse o tren

---

### PATCH `/api/stories/{id}`

**Mo ta**: Cap nhat truyen

**Permission**: `story:update_own` / `story:update_any`
**Role**: ADMIN (update_any), UPLOADER (update_own - chi truyen cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | Story ID     |

**Request Body** (multipart/form-data):

| Field              | Type          | Required | Description                          |
| ------------------ | ------------- | -------- | ------------------------------------ |
| `title`            | String        | No       | Tieu de truyen                       |
| `description`      | String        | No       | Mo ta truyen                         |
| `coverImageUrl`    | String        | No       | URL hinh bia                         |
| `coverImageFile`   | MultipartFile | No       | File hinh bia (upload)               |
| `storyType`        | StoryType     | No       | Loai truyen                          |
| `status`           | StoryStatus   | No       | Trang thai                           |
| `isPublished`      | boolean       | No       | Da duyet chua                        |
| `freeChapterLimit` | Integer       | No       | So chuong free                       |
| `viewCount`        | Long          | No       | Luot xem                             |

**Response** (`ApiResponse<StoryResponse>`): Xem cau truc StoryResponse o tren

---

### DELETE `/api/stories/{id}`

**Mo ta**: Xoa truyen

**Permission**: `story:delete_own` / `story:delete_any`
**Role**: ADMIN (delete_any), UPLOADER (delete_own - chi truyen cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | Story ID     |

**Response** (`ApiResponse<String>`):

| Field | Type   | Description     |
| ----- | ------ | --------------- |
| code  | int    | 200             |
| message| String| "Success"      |
| data  | String | "Xoa truyen thanh cong" |

---

### POST `/api/stories/{id}/publish-requests`

**Mo ta**: Tao yeu cau xuat ban truyen

**Permission**: `story:create`
**Role**: ADMIN, UPLOADER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | Story ID     |

**Request Body** (`StoryPublishRequestCreateRequest`):

| Field           | Type   | Required | Description    |
| --------------- | ------ | -------- | -------------- |
| `requesterNote` | String | No      | Ghi chu cua nguoi yeu cau |

**Example Request**:
```json
{
  "requesterNote": "Truyen da hoan thien 100 chuong, mong duoc duyet"
}
```

**Response** (`ApiResponse<StoryPublishRequestResponse>`):

| Field           | Type                       | Description           |
| --------------- | -------------------------- | --------------------- |
| `id`            | String                     | Request ID (UUID)     |
| `story`         | StoryResponse              | Thong tin truyen      |
| `requesterNote` | String                     | Ghi chu nguoi yeu cau |
| `reviewerNote`  | String                     | Ghi chu nguoi duyet   |
| `reviewer`      | UserResponse               | Nguoi duyet           |
| `status`        | StoryPublishRequestStatus  | Trang thai            |

**StoryPublishRequestStatus values**: `PENDING`, `APPROVED`, `REJECTED`

---

### GET `/api/stories/publish-requests`

**Mo ta**: Lay danh sach yeu cau xuat ban (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Query Params**:

| Field       | Type                       | Required | Default | Description       |
| ----------- | -------------------------- | -------- | ------- | ----------------- |
| `page`      | Integer                    | No       | 1       | So trang          |
| `size`      | Integer                    | No       | 10      | So phan tu/trang  |
| `storyId`   | String                     | No       | -       | Loc theo story ID |
| `status`    | StoryPublishRequestStatus  | No       | -       | Trang thai        |
| `uploaderId`| String                     | No       | -       | Loc theo uploader |

**Response** (`ApiResponse<PageResponse<StoryPublishRequestResponse>>`): Xem cau truc o tren

---

### GET `/api/stories/publish-requests/me`

**Mo ta**: Lay danh sach yeu cau xuat ban cua minh

**Permission**: `story:update_own`
**Role**: UPLOADER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Query Params**:

| Field     | Type                       | Required | Default | Description     |
| --------- | -------------------------- | -------- | ------- | --------------- |
| `page`    | Integer                    | No       | 1       | So trang        |
| `size`    | Integer                    | No       | 10      | So phan tu/trang|
| `storyId` | String                     | No       | -       | Loc theo story  |
| `status`  | StoryPublishRequestStatus  | No       | -       | Trang thai      |

**Response** (`ApiResponse<PageResponse<StoryPublishRequestResponse>>`): Xem cau truc o tren

---

### PATCH `/api/stories/publish-requests/approve/{id}`

**Mo ta**: Phe duyet yeu cau xuat ban (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description     |
| ----- | ------ | --------------- |
| `id`  | String | Request ID      |

**Request Body** (`StoryPublishRequestReviewRequest`):

| Field          | Type   | Required | Description      |
| -------------- | ------ | -------- | ---------------- |
| `reviewerNote` | String | No      | Ghi chu nguoi duyet |

**Example Request**:
```json
{
  "reviewerNote": "Truyen da duyet, chuc mung!"
}
```

**Response** (`ApiResponse<StoryPublishRequestResponse>`): Xem cau truc o tren

---

### PATCH `/api/stories/publish-requests/reject/{id}`

**Mo ta**: Tu choi yeu cau xuat ban (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description     |
| ----- | ------ | --------------- |
| `id`  | String | Request ID      |

**Request Body** (`StoryPublishRequestReviewRequest`):

| Field          | Type   | Required | Description      |
| -------------- | ------ | -------- | ---------------- |
| `reviewerNote` | String | No      | Ghi chu nguoi tu choi |

**Example Request**:
```json
{
  "reviewerNote": "Truyen chua dat yeu cau, vui long chinh sua"
}
```

**Response** (`ApiResponse<StoryPublishRequestResponse>`): Xem cau truc o tren

---

### DELETE `/api/stories/publish-requests/{id}`

**Mo ta**: Xoa yeu cau xuat ban

**Permission**: `story:delete_own` / `story:delete_any`
**Role**: ADMIN (delete_any), UPLOADER (delete_own - chi request cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description     |
| ----- | ------ | --------------- |
| `id`  | String | Request ID      |

**Response** (`ApiResponse<String>`): Xem cau truc o tren

---

### PATCH `/api/stories/{id}/ban`

**Mo ta**: Ban truyen (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | Story ID     |

**Request Body** (`StoryBanRequest`):

| Field          | Type         | Required | Description              |
| -------------- | ------------ | -------- | ------------------------ |
| `violationType`| ViolationType| No       | Loai vi pham             |
| `reason`       | String       | Yes      | Ly do ban (toi da 1000 ky tu) |

**Example Request**:
```json
{
  "violationType": "COPYRIGHT",
  "reason": "Noi dung truyen chap quyen"
}
```

**Response** (`ApiResponse<StoryResponse>`): Xem cau truc StoryResponse o tren

---

### PATCH `/api/stories/{id}/unban`

**Mo ta**: Unban truyen (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | Story ID     |

**Request Body** (`StoryUnbanRequest`):

| Field   | Type   | Required | Description                 |
| ------- | ------ | -------- | --------------------------- |
| `reason`| String | Yes      | Ly do unban (toi da 1000 ky tu) |

**Example Request**:
```json
{
  "reason": "Noi dung da duoc xu ly, cho phep xuat ban lai"
}
```

**Response** (`ApiResponse<StoryResponse>`): Xem cau truc StoryResponse o tren

---

## GENRE MODULE

### GET `/api/genres`

**Mo ta**: Lay danh sach the loai

**Permission**: Public
**Role**: Tat ca

**Query Params**:

| Field   | Type   | Required | Description          |
| ------- | ------ | -------- | -------------------- |
| `search`| String | No       | Tim kiem theo ten    |

**Response** (`ApiResponse<List<GenreResponse>>`):

| Field            | Type    | Description              |
| ---------------- | ------- | ------------------------ |
| `id`             | Integer | Genre ID                 |
| `name`           | String  | Ten the loai            |
| `storyQuantity`   | Integer | So luong truyen (default: 0) |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {"id": 1, "name": "Action", "storyQuantity": 50},
    {"id": 2, "name": "Romance", "storyQuantity": 30}
  ]
}
```

---

### POST `/api/genres`

**Mo ta**: Tao the loai moi (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Request Body** (`GenreRequest`):

| Field | Type   | Required | Description     |
| ----- | ------ | -------- | --------------- |
| `name`| String | Yes      | Ten the loai    |

**Example Request**:
```json
{
  "name": "Fantasy"
}
```

**Response** (`ApiResponse<GenreResponse>`): Xem cau truc o tren

---

### PATCH `/api/genres/{id}`

**Mo ta**: Cap nhat the loai (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type    | Description  |
| ----- | ------- | ------------ |
| `id`  | Integer | Genre ID     |

**Request Body** (`GenreRequest`):

| Field | Type   | Required | Description     |
| ----- | ------ | -------- | --------------- |
| `name`| String | Yes      | Ten the loai moi |

**Response** (`ApiResponse<GenreResponse>`): Xem cau truc o tren

---

### DELETE `/api/genres/{id}`

**Mo ta**: Xoa the loai (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type    | Description  |
| ----- | ------- | ------------ |
| `id`  | Integer | Genre ID     |

**Response** (`ApiResponse<String>`): Xem cau truc chung

---

## CHAPTER MODULE

### GET `/api/stories/{slug}/chapters`

**Mo ta**: Lay danh sach chuong theo slug truyen

**Permission**: Public
**Role**: Tat ca

**Path Variables**:

| Field | Type   | Description      |
| ----- | ------ | ---------------- |
| `slug`| String | Story slug URL   |

**Query Params**:

| Field   | Type    | Required | Default | Description          |
| ------- | ------- | -------- | ------- | -------------------- |
| `page`  | Integer | No       | 1       | So trang             |
| `size`  | Integer | No       | 10      | So phan tu tren trang|
| `search`| String  | No       | -       | Tim kiem            |

**Response** (`ApiResponse<PageResponse<ChapterResponse>>`):

| Field            | Type                    | Description              |
| ---------------- | ----------------------- | ------------------------ |
| `id`             | String                  | Chapter ID (UUID)        |
| `story`          | StoryResponse           | Thong tin truyen         |
| `chapterNumber`   | BigDecimal              | So chuong (1, 1.5, 2...) |
| `title`          | String                  | Tieu de chuong          |
| `isPublished`    | boolean                 | Da xuat ban chua        |
| `viewCount`      | Long                    | Luot xem                |
| `content`        | String                  | Noi dung (cho novel)     |
| `pageCount`      | Integer                 | So trang (cho manga)    |
| `pages`          | List<ChapterPageResponse>| Danh sach trang        |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 10,
    "totalElements": 100,
    "data": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "story": {"id": "...", "title": "The Great Story"},
        "chapterNumber": 1.0,
        "title": "Chapter 1: The Beginning",
        "isPublished": true,
        "viewCount": 100,
        "content": null,
        "pageCount": 15,
        "pages": [
          {"id": "...", "pageNumber": 1, "imageUrl": "..."}
        ]
      }
    ]
  }
}
```

---

### GET `/api/chapters/{id}`

**Mo ta**: Lay thong tin chuong theo ID

**Permission**: Public (co gioi han dua vao story settings)
**Role**: Tat ca

**Logic xem noi dung**:
- **ADMIN**: Luon duoc doc
- **UPLOADER**: Doc premium neu la truyen cua minh
- **USER**: Doc neu trong gioi han free hoac co subscription active
- **Guest**: Bi chan neu can premium

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Response** (`ApiResponse<ChapterResponse>`): Xem cau truc o tren

---

### POST `/api/stories/{id}/chapters`

**Mo ta**: Tao chuong moi cho truyen

**Permission**: `chapter:create`
**Role**: ADMIN, UPLOADER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description  |
| ----- | ------ | ------------ |
| `id`  | String | Story ID     |

**Request Body** (`ChapterCreateRequest`):

| Field           | Type      | Required | Default | Description            |
| --------------- | --------- | -------- | ------- | ---------------------- |
| `chapterNumber` | BigDecimal| No       | -       | So chuong (1, 1.5, 2...)|
| `title`        | String    | No       | -       | Tieu de chuong         |
| `isPublished`  | boolean   | No       | false   | Da xuat ban chua       |

**Example Request**:
```json
{
  "chapterNumber": 1.5,
  "title": "Chapter 1.5: The Journey Begins",
  "isPublished": false
}
```

**Response** (`ApiResponse<ChapterResponse>`): Xem cau truc o tren

---

### PATCH `/api/chapters/{id}`

**Mo ta**: Cap nhat chuong

**Permission**: `chapter:update_own`
**Role**: ADMIN, UPLOADER (chi chuong thuoc truyen cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Request Body** (`ChapterUpdateRequest`):

| Field           | Type      | Required | Description            |
| --------------- | --------- | -------- | ---------------------- |
| `chapterNumber` | BigDecimal| No       | So chuong moi          |
| `title`        | String    | No       | Tieu de chuong moi     |
| `isPublished`  | boolean   | No       | Trang thai xuat ban    |

**Response** (`ApiResponse<ChapterResponse>`): Xem cau truc o tren

---

### PATCH `/api/chapters/{id}/update-content`

**Mo ta**: Cap nhat noi dung chu (cho novel)

**Permission**: `chapter:update_own`
**Role**: ADMIN, UPLOADER (chi chuong thuoc truyen cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Request Body** (`ChapterContentUpdateRequest`):

| Field    | Type   | Required | Description        |
| -------- | ------ | -------- | ------------------ |
| `content`| String | No       | Noi dung chuong moi |

**Example Request**:
```json
{
  "content": "Day la noi dung chuong 1...\n\nTiep tuc..."
}
```

**Response** (`ApiResponse<ChapterResponse>`): Xem cau truc o tren

---

### PATCH `/api/chapters/update-publish-status`

**Mo ta**: Cap nhat trang thai xuat ban nhieu chuong cung luc

**Permission**: `chapter:update_own`
**Role**: ADMIN, UPLOADER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Request Body** (`ChapterUpdatePublishStatusRequest`):

| Field            | Type        | Required | Default | Description              |
| ---------------- | ----------- | -------- | ------- | ------------------------ |
| `chapterIdList`  | List<String>| Yes      | -       | Danh sach chapter IDs    |
| `publishStatus`  | Boolean     | No       | true    | Trang thai xuat ban      |

**Example Request**:
```json
{
  "chapterIdList": ["id1", "id2", "id3"],
  "publishStatus": true
}
```

**Response** (`ApiResponse<String>`): Xem cau truc chung

---

### DELETE `/api/chapter/{id}`

**Mo ta**: Xoa chuong

**Permission**: `chapter:delete_own`
**Role**: ADMIN, UPLOADER (chi chuong thuoc truyen cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Response** (`ApiResponse<String>`): Xem cau truc chung

---

### PATCH `/{id}/ban`

**Mo ta**: Ban chuong (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Request Body** (`ChapterBanRequest`):

| Field          | Type         | Required | Description              |
| -------------- | ------------ | -------- | ------------------------ |
| `violationType`| ViolationType| No       | Loai vi pham             |
| `reason`       | String       | Yes      | Ly do ban (toi da 1000 ky tu) |

**Response** (`ApiResponse<ChapterResponse>`): Xem cau truc ChapterResponse o tren

---

### PATCH `/{id}/unban`

**Mo ta**: Unban chuong (Admin)

**Permission**: ADMIN role
**Role**: ADMIN

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Request Body** (`ChapterUnbanRequest`):

| Field   | Type   | Required | Description                 |
| ------- | ------ | -------- | --------------------------- |
| `reason`| String | Yes      | Ly do unban (toi da 1000 ky tu) |

**Response** (`ApiResponse<ChapterResponse>`): Xem cau truc ChapterResponse o tren

---

## CHAPTER PAGE MODULE

### GET `/api/chapters/{id}/pages`

**Mo ta**: Lay danh sach trang cua chuong (manga)

**Permission**: Public (co gioi han neu chapter bi khoa premium)
**Role**: Tat ca (co dieu kien)

**Logic xem trang**:
- **ADMIN**: Luon duoc doc
- **UPLOADER**: Doc neu la truyen cua minh
- **USER**: Doc neu trong gioi han free hoac co subscription active

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Response** (`ApiResponse<List<ChapterPageResponse>>`):

| Field        | Type    | Description          |
| ------------ | ------- | -------------------- |
| `id`         | String  | Page ID (UUID)       |
| `pageNumber` | Integer | So trang             |
| `imageUrl`   | String  | URL hinh trang       |
| `width`      | Integer | Chieu rong hinh      |
| `height`     | Integer | Chieu cao hinh       |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {"id": "page1", "pageNumber": 1, "imageUrl": "https://...", "width": 800, "height": 1200},
    {"id": "page2", "pageNumber": 2, "imageUrl": "https://...", "width": 800, "height": 1200}
  ]
}
```

---

### POST `/api/chapters/pages`

**Mo ta**: Tao trang cho chuong (upload hinh)

**Permission**: `chapter:create`
**Role**: ADMIN, UPLOADER

**Headers**:
- `Authorization: Bearer {token}` (Required)
- `Content-Type: multipart/form-data`

**Request Body** (multipart/form-data):

| Field                 | Type                     | Required | Description              |
| --------------------- | ------------------------ | -------- | ------------------------ |
| `chapterPageRequests` | List<ChapterPageRequest> | Yes      | Thong tin cac trang      |
| `files`               | List<MultipartFile>       | No       | File hinh upload         |
| `chapterId`           | String                    | Yes      | Chapter ID               |

**ChapterPageRequest structure**:

| Field        | Type    | Required | Description       |
| ------------ | ------- | -------- | ----------------- |
| `id`         | String  | No       | Page ID (neu sua) |
| `pageNumber` | Integer | Yes      | So trang          |
| `imageUrl`   | String  | No       | URL hinh          |
| `isNewPage`  | Boolean | No       | La trang moi      |

**Example Request** (multipart):
```
chapterId: "chapter-uuid-123"
chapterPageRequests: [{"pageNumber": 1, "isNewPage": true}, {"pageNumber": 2, "isNewPage": true}]
files: [file1.jpg, file2.jpg]
```

**Response** (`ApiResponse<List<ChapterPageResponse>>`): Xem cau truc o tren

---

### PATCH `/api/chapters/pages`

**Mo ta**: Cap nhat trang chuong

**Permission**: `chapter:update_own`
**Role**: ADMIN, UPLOADER

**Headers**:
- `Authorization: Bearer {token}` (Required)
- `Content-Type: multipart/form-data`

**Request Body** (multipart/form-data): Xem POST `/api/chapters/pages`

**Response** (`ApiResponse<List<ChapterPageResponse>>`): Xem cau truc o tren

---

### DELETE `/api/chapters/{id}/page`

**Mo ta**: Xoa tat ca trang cua chuong

**Permission**: `chapter:delete_own`
**Role**: ADMIN, UPLOADER (chi chuong thuoc truyen cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description   |
| ----- | ------ | ------------- |
| `id`  | String | Chapter ID    |

**Response** (`ApiResponse<String>`): Xem cau truc chung

---

## BOOKMARK MODULE

### GET `/api/bookmarks`

**Mo ta**: Lay bookmark theo story ID

**Permission**: Authenticated
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Query Params**:

| Field    | Type   | Required | Description  |
| -------- | ------ | -------- | ------------ |
| `storyId`| String | Yes     | Story ID     |

**Response** (`ApiResponse<BookmarkResponse>`):

| Field   | Type | Description          |
| ------- | ---- | -------------------- |
| `user`  | User | Entity nguoi dung    |
| `story` | Story| Entity truyen        |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "user": {"id": "...", "username": "johndoe"},
    "story": {"id": "...", "title": "The Great Story"}
  }
}
```

---

### POST `/api/bookmarks`

**Mo ta**: Tao bookmark moi

**Permission**: Authenticated
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Query Params**:

| Field    | Type   | Required | Description  |
| -------- | ------ | -------- | ------------ |
| `storyId`| String | Yes     | Story ID     |

**Response** (`ApiResponse<BookmarkResponse>`): Xem cau truc o tren

---

### DELETE `/api/bookmarks`

**Mo ta**: Xoa bookmark

**Permission**: Authenticated
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Query Params**:

| Field    | Type   | Required | Description  |
| -------- | ------ | -------- | ------------ |
| `storyId`| String | Yes     | Story ID     |

**Response** (`ApiResponse<String>`): Xem cau truc chung

---

## COMMENT MODULE

### GET `/api/comments/chapter/{id}`

**Mo ta**: Lay danh sach binh luan theo chuong

**Permission**: Public
**Role**: Tat ca

**Path Variables**:

| Field | Type   | Description        |
| ----- | ------ | ------------------ |
| `id`  | String | Chapter ID (UUID)  |

**Query Params**:

| Field | Type    | Required | Default | Description          |
| ----- | ------- | -------- | ------- | -------------------- |
| `page`| Integer | No       | 1       | So trang             |
| `size`| Integer | No       | 10      | So phan tu tren trang|

**Response** (`ApiResponse<PageResponse<CommentResponse>>`):

| Field     | Type                     | Description         |
| --------- | ------------------------ | ------------------- |
| `id`      | String                   | Comment ID (UUID)   |
| `type`    | CommentType              | Loai binh luan (STORY/CHAPTER)|
| `content` | String                   | Noi dung binh luan  |
| `author`  | UserResponse             | Thong tin tac gia   |
| `replies` | List<CommentResponse>    | Danh sach tra loi   |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 2,
    "data": [
      {
        "id": "comment-uuid",
        "type": "CHAPTER",
        "content": "Chuong nay hay qua!",
        "author": {"id": "...", "username": "reader1"},
        "replies": [
          {
            "id": "reply-uuid",
            "type": "CHAPTER",
            "content": "Dong y!",
            "author": {"id": "...", "username": "reader2"},
            "replies": []
          }
        ]
      }
    ]
  }
}
```

---

### GET `/api/comments/story/{id}`

**Mo ta**: Lay danh sach binh luan theo truyen (khong co chapterId - binh luan tong quan ve truyen)

**Permission**: Public
**Role**: Tat ca

**Path Variables**:

| Field | Type   | Description       |
| ----- | ------ | ----------------- |
| `id`  | String | Story ID (UUID)  |

**Query Params**:

| Field | Type    | Required | Default | Description          |
| ----- | ------- | -------- | ------- | -------------------- |
| `page`| Integer | No       | 1       | So trang             |
| `size`| Integer | No       | 10      | So phan tu tren trang|

**Response** (`ApiResponse<PageResponse<CommentResponse>>`): Xem cau truc o tren

---

### POST `/api/comments`

**Mo ta**: Tao binh luan moi (cho truyen hoac chuong)

**Permission**: `comment:create`
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Request Body** (`CommentCreateRequest`):

| Field      | Type        | Required | Description                          |
| ---------- | ----------- | -------- | ------------------------------------ |
| `type`     | CommentType | Yes     | Loai binh luan: `STORY` hoac `CHAPTER` |
| `storyId`  | String      | Yes*    | Story ID (UUID) - bat buoc neu type=STORY |
| `chapterId`| String      | Yes*    | Chapter ID (UUID) - bat buoc neu type=CHAPTER |
| `content`  | String      | Yes     | Noi dung binh luan (toi da 2000 ky tu) |
| `parentId` | String      | No      | Parent comment ID (neu la tra loi)   |

> **Luu y**: `storyId` hoac `chapterId` phu thuoc vao `type`. Neu `type=STORY` thi bat buoc `storyId`. Neu `type=CHAPTER` thi bat buoc `chapterId`.

**CommentType enum values**: `STORY`, `CHAPTER`

**Example Request** (binh luan chuong):
```json
{
  "type": "CHAPTER",
  "chapterId": "550e8400-e29b-41d4-a716-446655440001",
  "content": "Chuong nay vo cung hap dan, cam on tac gia!",
  "parentId": null
}
```

**Example Request** (tra loi binh luan):
```json
{
  "type": "CHAPTER",
  "chapterId": "550e8400-e29b-41d4-a716-446655440001",
  "content": "Dong y voi ban, chuong nay that su hay!",
  "parentId": "comment-uuid-goc"
}
```

**Example Request** (binh luan truyen - khong thuoc chuong cu the):
```json
{
  "type": "STORY",
  "storyId": "550e8400-e29b-41d4-a716-446655440000",
  "content": "Truyen nay that su tuyet voi, nen doc!",
  "parentId": null
}
```

**Response** (`ApiResponse<CommentResponse>`):

| Field     | Type                     | Description         |
| --------- | ------------------------ | ------------------- |
| `id`      | String                   | Comment ID (UUID)   |
| `type`    | CommentType              | Loai binh luan      |
| `content` | String                   | Noi dung binh luan  |
| `author`  | UserResponse             | Thong tin tac gia   |
| `replies` | List<CommentResponse>    | Danh sach tra loi   |

**Example Response**:
```json
{
  "code": 201,
  "message": "Success",
  "data": {
    "id": "new-comment-uuid",
    "type": "CHAPTER",
    "content": "Chuong nay vo cung hap dan, cam on tac gia!",
    "author": {
      "id": "user-uuid",
      "username": "reader1",
      "avatarUrl": "https://example.com/avatar.jpg"
    },
    "replies": []
  }
}
```

---

### PATCH `/api/comments/{id}`

**Mo ta**: Cap nhat binh luan cua minh

**Permission**: `comment:create` (chi binh luan cua minh)
**Role**: ADMIN, UPLOADER, USER (chi binh luan cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description      |
| ----- | ------ | ---------------- |
| `id`  | String | Comment ID (UUID) |

**Request Body** (`CommentUpdateRequest`):

| Field    | Type   | Required | Description                   |
| -------- | ------ | -------- | ----------------------------- |
| `content`| String | Yes     | Noi dung binh luan moi (toi da 2000 ky tu) |

**Response** (`ApiResponse<CommentResponse>`): Xem cau truc o tren

---

### DELETE `/api/comments/{id}`

**Mo ta**: Xoa binh luan

**Permission**: `comment:delete_own` / `comment:delete_any`
**Role**: ADMIN (delete_any), UPLOADER/USER (delete_own - chi binh luan cua minh)

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Path Variables**:

| Field | Type   | Description      |
| ----- | ------ | ---------------- |
| `id`  | String | Comment ID (UUID) |

**Response** (`ApiResponse<String>`): Xem cau truc chung

---

## READING HISTORY MODULE

### GET `/api/reading-histories/story/{storyId}`

**Mo ta**: Lay chuong doc gan nhat trong mot truyen (de quay tro lai doc tiep)

**Permission**: Public (guest + user)
**Role**: Tat ca

**Headers**:
- `Authorization: Bearer {token}` (Optional - cho authenticated user)
- `Session-Id` (Optional - cho guest, bat buoc neu khong co token)

**Path Variables**:

| Field    | Type   | Description      |
| -------- | ------ | ---------------- |
| `storyId`| String | Story ID (UUID) |

**Response** (`ApiResponse<ReadingHistoryResponse>`):

| Field       | Type             | Description                   |
| ----------- | ---------------- | ----------------------------- |
| `id`        | String           | History ID (UUID)             |
| `story`     | StoryResponse    | Entity truyen                 |
| `chapter`   | ChapterResponse  | Entity chuong doc gan nhat    |
| `lastReadAt`| LocalDateTime    | Thoi gian doc gan nhat        |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": "history-uuid",
    "story": {"id": "story-uuid", "title": "The Great Story", "slug": "the-great-story"},
    "chapter": {"id": "chapter-uuid", "chapterNumber": 5, "title": "Chapter 5: The Journey"},
    "lastReadAt": "2024-01-15T10:30:00"
  }
}
```

> **Luu y**: Neu nguoi dung chua tung doc truyen nay, tra ve `null` trong `data`.

---

### GET `/api/reading-histories`

**Mo ta**: Lay danh sach lich su doc cua nguoi dung/guest hien tai

**Permission**: Public (guest + user)
**Role**: Tat ca

**Headers**:
- `Authorization: Bearer {token}` (Optional - cho authenticated user)
- `Session-Id` (Optional - cho guest, bat buoc neu khong co token)

**Query Params**:

| Field      | Type                      | Required | Default | Description                      |
| ---------- | ------------------------- | -------- | ------- | -------------------------------- |
| `page`     | Integer                   | No       | 1       | So trang                         |
| `size`     | Integer                   | No       | 10      | So phan tu tren trang            |
| `type`     | ReadingHistoryFilterType  | No       | ALL     | Loc theo loai: ALL, STORY, CHAPTER |
| `fromDate` | LocalDate (yyyy-MM-dd)    | No       | -       | Loc tu ngay                      |
| `toDate`   | LocalDate (yyyy-MM-dd)    | No       | -       | Loc den ngay                     |

**ReadingHistoryFilterType values**:
- `ALL` - Tat ca lich su
- `STORY` - Chi lich su theo truyen (nhom theo story, lay chuong doc gan nhat moi nhat)
- `CHAPTER` - Chi lich su theo chuong (chi tiet tung chuong da doc)

**Example Request** (lay lich su doc theo truyen):
```
GET /api/reading-histories?type=STORY&page=1&size=10
Headers: Session-Id: guest-session-uuid
```

**Example Request** (lay lich su doc chi tiet theo chuong, co loc ngay):
```
GET /api/reading-histories?type=CHAPTER&fromDate=2024-01-01&toDate=2024-01-31
Headers: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response** (`ApiResponse<PageResponse<ReadingHistoryResponse>>`):

| Field       | Type             | Description                   |
| ----------- | ---------------- | ----------------------------- |
| `id`        | String           | History ID (UUID)             |
| `story`     | StoryResponse    | Entity truyen                 |
| `chapter`   | ChapterResponse  | Entity chuong                 |
| `lastReadAt`| LocalDateTime    | Thoi gian doc gan nhat        |

**Example Response** (type=STORY):
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 3,
    "data": [
      {
        "id": "history-uuid-1",
        "story": {"id": "story-1", "title": "The Great Story"},
        "chapter": {"id": "chapter-5", "chapterNumber": 5, "title": "Chapter 5"},
        "lastReadAt": "2024-01-15T10:30:00"
      },
      {
        "id": "history-uuid-2",
        "story": {"id": "story-2", "title": "Another Story"},
        "chapter": {"id": "chapter-10", "chapterNumber": 10, "title": "Chapter 10"},
        "lastReadAt": "2024-01-14T08:00:00"
      }
    ]
  }
}
```

**Example Response** (type=CHAPTER):
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 5,
    "data": [
      {
        "id": "history-uuid-1",
        "story": {"id": "story-1", "title": "The Great Story"},
        "chapter": {"id": "chapter-5", "chapterNumber": 5, "title": "Chapter 5"},
        "lastReadAt": "2024-01-15T10:30:00"
      },
      {
        "id": "history-uuid-2",
        "story": {"id": "story-1", "title": "The Great Story"},
        "chapter": {"id": "chapter-4", "chapterNumber": 4, "title": "Chapter 4"},
        "lastReadAt": "2024-01-14T09:00:00"
      }
    ]
  }
}
```

---

### POST `/api/reading-histories`

**Mo ta**: Tao/Cap nhat lich su doc (guest + user)

**Permission**: Public (guest + user)
**Role**: Tat ca

**Headers**:
- `Authorization: Bearer {token}` (Optional - cho authenticated user)
- `Session-Id` (Optional - cho guest, bat buoc neu khong co token)

**Request Body** (`ReadingHistoryRequest`):

| Field      | Type        | Required | Description                              |
| ---------- | ----------- | -------- | ---------------------------------------- |
| `chapterId`| String      | Yes     | Chapter ID (UUID)                        |
| `storyId`  | String      | Yes     | Story ID (UUID)                          |
| `type`     | HistoryType | Yes     | Loai: `STORY` (doc truyen) hoac `CHAPTER` (doc chuong) |

**HistoryType enum values**: `STORY`, `CHAPTER`

**Example Request** (doc truyen - quay tro lai tu dau):
```json
{
  "storyId": "550e8400-e29b-41d4-a716-446655440000",
  "chapterId": "550e8400-e29b-41d4-a716-446655440001",
  "type": "STORY"
}
```

**Example Request** (doc chuong cu the):
```json
{
  "storyId": "550e8400-e29b-41d4-a716-446655440000",
  "chapterId": "550e8400-e29b-41d4-a716-446655440002",
  "type": "CHAPTER"
}
```

**Response** (`ApiResponse<ReadingHistoryResponse>`):

| Field       | Type             | Description                   |
| ----------- | ---------------- | ----------------------------- |
| `id`        | String           | History ID (UUID)             |
| `story`     | StoryResponse    | Entity truyen                 |
| `chapter`   | ChapterResponse  | Entity chuong                 |
| `lastReadAt`| LocalDateTime    | Thoi gian doc                 |

**Example Response**:
```json
{
  "code": 201,
  "message": "Success",
  "data": {
    "id": "history-uuid",
    "story": {"id": "story-uuid", "title": "The Great Story"},
    "chapter": {"id": "chapter-uuid", "chapterNumber": 5, "title": "Chapter 5"},
    "lastReadAt": "2024-01-15T10:30:00"
  }
}
```

**Luu y**:
- Neu da co lich su doc cho `chapterId` nay, `lastReadAt` se duoc cap nhat.
- Neu chua co, se tao moi mot ban ghi lich su.
- Header `Session-Id` duoc su dung de nhan dien guest, neu khong co token.

---

### DELETE `/api/reading-histories`

**Mo ta**: Xoa toan bo lich su doc cua nguoi dung/guest hien tai

**Permission**: Public (guest + user)
**Role**: Tat ca

**Headers**:
- `Authorization: Bearer {token}` (Optional - cho authenticated user)
- `Session-Id` (Optional - cho guest, bat buoc neu khong co token)

**Response** (`ApiResponse<String>`): Xem cau truc chung

---

## AUDIT LOG MODULE

### GET `/api/audit-logs`

**Mo ta**: Lay danh sach audit log (lich su hanh dong)

**Permission**: Authenticated
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Request Body** (`AuditLogPageRequest`) - JSON body:

| Field        | Type            | Required | Default | Description              |
| ------------ | --------------- | -------- | ------- | ------------------------ |
| `page`       | Integer         | No       | 1       | So trang                 |
| `size`       | Integer         | No       | 10      | So phan tu tren trang   |
| `actorId`    | String          | No       | -       | Loc theo nguoi thuc hien|
| `action`     | AuditAction     | No       | -       | Loai hanh dong           |
| `objectType` | AuditObjectType | No       | -       | Loai doi tuong           |
| `objectId`   | String          | No       | -       | ID cua doi tuong         |
| `fromDate`   | LocalDateTime   | No       | -       | Tu ngay                  |
| `toDate`     | LocalDateTime   | No       | -       | Den ngay                 |

**AuditAction values**: `REGISTER`, `LOGIN`, `LOGOUT`, `CREATE`, `UPDATE`, `DELETE`, `PUBLISH`, `UNPUBLISH`, `APPROVE`, `REJECT`, `BAN`, `UNBAN`, `CHANGE_ROLE`, `CHANGE_PASSWORD`, `CHANGE_EMAIL`, `RESET_PASSWORD`

**AuditObjectType values**: `USER`, `STORY`, `CHAPTER`, `COMMENT`, `PUBLISH_REQUEST`, `GENRE`, `TAG`, `ROLE`, `SYSTEM`

**Example Request**:
```json
{
  "page": 1,
  "size": 20,
  "action": "BAN",
  "objectType": "STORY",
  "fromDate": "2024-01-01T00:00:00",
  "toDate": "2024-12-31T23:59:59"
}
```

**Response** (`ApiResponse<PageResponse<AuditLogResponse>>`):

| Field          | Type            | Description                |
| -------------- | --------------- | -------------------------- |
| `id`           | String          | Log ID (UUID)              |
| `actorId`      | String          | ID nguoi thuc hien         |
| `actorUsername`| String          | Username nguoi thuc hien    |
| `actorRole`    | String          | Role nguoi thuc hien       |
| `action`       | AuditAction     | Loai hanh dong             |
| `objectType`   | AuditObjectType | Loai doi tuong            |
| `objectId`     | String          | ID doi tuong               |
| `description`  | String          | Mo ta hanh dong            |
| `oldValue`     | String          | Gia tri cu (JSON)          |
| `newValue`     | String          | Gia tri moi (JSON)         |
| `ipAddress`    | String          | Dia chi IP                 |
| `userAgent`    | String          | User Agent                 |
| `createdAt`    | LocalDateTime   | Thoi gian tao              |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 5,
    "totalElements": 100,
    "data": [
      {
        "id": "log-uuid",
        "actorId": "admin-uuid",
        "actorUsername": "admin",
        "actorRole": "ADMIN",
        "action": "BAN",
        "objectType": "STORY",
        "objectId": "story-uuid",
        "description": "Ban truyen vi noi dung khong phu hop",
        "oldValue": "{\"isPublished\": true}",
        "newValue": "{\"isPublished\": false, \"reason\": \"...\"}",
        "ipAddress": "192.168.1.1",
        "userAgent": "Mozilla/5.0...",
        "createdAt": "2024-06-15T14:30:00"
      }
    ]
  }
}
```

---

## MODERATION ACTION MODULE

### GET `/api/moderation-actions`

**Mo ta**: Lay danh sach hanh dong kiem duyet (lich su ban/unban)

**Permission**: Authenticated
**Role**: ADMIN, UPLOADER, USER

**Headers**:
- `Authorization: Bearer {token}` (Required)

**Request Body** (`ModerationActionPageRequest`) - JSON body:

| Field          | Type                  | Required | Default | Description             |
| -------------- | --------------------- | -------- | ------- | ----------------------- |
| `page`         | Integer               | No       | 1       | So trang                |
| `size`         | Integer               | No       | 10      | So phan tu tren trang   |
| `objectId`     | String                | No       | -       | ID doi tuong            |
| `objectType`   | ModerationObjectType  | No       | -       | Loai doi tuong          |
| `actionType`   | ModerationActionType  | No       | -       | Loai hanh dong          |
| `violationType`| ViolationType        | No       | -       | Loai vi pham            |

**ModerationObjectType values**: `STORY`, `CHAPTER`, `COMMENT`, `USER`

**ModerationActionType values**: `BAN`, `UNBAN`

**Example Request**:
```json
{
  "page": 1,
  "size": 20,
  "objectType": "STORY",
  "actionType": "BAN"
}
```

**Response** (`ApiResponse<PageResponse<ModerationActionResponse>>`):

| Field          | Type                  | Description              |
| -------------- | --------------------- | ------------------------ |
| `id`           | String                | Action ID (UUID)         |
| `objectId`     | String                | ID doi tuong bi kiem duyet |
| `objectType`   | ModerationObjectType  | Loai doi tuong           |
| `actionType`   | ModerationActionType  | Loai hanh dong (BAN/UNBAN)|
| `violationType`| ViolationType        | Loai vi pham            |
| `reason`       | String                | Ly do                    |
| `adminId`      | String                | ID admin thuc hien       |
| `adminUsername`| String                | Username admin thuc hien |
| `createdAt`     | LocalDateTime         | Thoi gian tao            |

**Example Response**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 2,
    "totalElements": 30,
    "data": [
      {
        "id": "mod-uuid",
        "objectId": "story-uuid",
        "objectType": "STORY",
        "actionType": "BAN",
        "violationType": "COPYRIGHT",
        "reason": "Noi dung truyen chap quyen tac gia",
        "adminId": "admin-uuid",
        "adminUsername": "admin",
        "createdAt": "2024-06-15T14:30:00"
      }
    ]
  }
}
```

---

# ENUM VALUES

## StoryType
- `NOVEL` - Truyen van
- `MANGA` - Truyen tranh (bao gom COMICS, MANHWA, MANHUA)

## StoryStatus
- `ONGOING` - Dang tien hanh
- `COMPLETED` - Hoan thanh
- `HIATUS` - Tam ngung
- `DROPPED` - Da bo

## StoryPublishRequestStatus
- `PENDING` - Dang cho duyet
- `APPROVED` - Da duyet
- `REJECTED` - Tu choi

## CommentType
- `STORY` - Binh luan ve truyen (khong thuoc chuong cu the)
- `CHAPTER` - Binh luan ve chuong cu the

## HistoryType
- `STORY` - Ghi nhan viec doc truyen (quay tro lai doc tu dau)
- `CHAPTER` - Ghi nhan viec doc chuong cu the

## ReadingHistoryFilterType
- `ALL` - Tat ca lich su doc
- `STORY` - Chi lich su theo truyen (nhom theo story)
- `CHAPTER` - Chi tiet lich su theo chuong da doc

## SortType
- `NEWEST` - Moi nhat (theo createdAt giam dan)
- `OLDEST` - Cu nhat (theo createdAt tang dan)
- `UPDATED` - Moi cap nhat (theo updatedAt giam dan)
- `VIEW` - Nhieu luot xem nhat (theo viewCount giam dan)
- `FOLLOW` - Nhieu nguoi theo doi nhat (theo so bookmark giam dan)
- `ALPHABET_ASC` - Theo bang chu cai A -> Z
- `ALPHABET_DESC` - Theo bang chu cai Z -> A

## ViolationType
- `COPYRIGHT` - Vi pham ban quyen
- `PORNOGRAPHY` - Noi dung khiem nham
- `VIOLENCE` - Noi dung buc dac
- `SPAM` - Spam
- `HARASSMENT` - Quấy rối
- `OTHER` - Khac

## Roles
- `1` = USER - Nguoi dung thong thuong
- `2` = UPLOADER - Nguoi dang noi dung
- `3` = ADMIN - Quan tri vien

---

# GHI CHU

## Dieu kien ◐ (Co dieu kien)

| Endpoint | UPLOADER | USER |
| -------- | -------- | ---- |
| `PATCH /api/stories/{id}` | Chi sua truyen do minh upload | - |
| `DELETE /api/stories/{id}` | Chi xoa truyen do minh upload | - |
| `GET /api/chapters/{id}` | Doc premium neu la bo cua minh; free neu trong gioi han | Doc neu trong gioi han free hoac co subscription active |
| `GET /api/chapters/{id}/pages` | Nhu tren | Nhu tren |
| `PATCH /api/chapters/{id}` | Chi sua chuong thuoc bo cua minh | - |
| `DELETE /api/chapter/{id}` | Chi xoa chuong thuoc bo cua minh | - |
| `PATCH /api/comments/{id}` | Chi sua comment do minh viet | Chi sua comment do minh viet |
| `DELETE /api/comments/{id}` | Chi xoa comment do minh viet | Chi xoa comment do minh viet |

---

_TruyenOnline API Documentation | v5.1 | Updated: July 2026_

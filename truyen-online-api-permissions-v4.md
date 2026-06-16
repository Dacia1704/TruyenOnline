# 📡 TruyệnOnline — API Endpoints & Permission Matrix v4

> **3 Role:** `ADMIN` | `UPLOADER` | `USER`
>
> **Ký hiệu:**
> - ✅ Có quyền truy cập
> - ◐ Có điều kiện (xem bảng ghi chú bên dưới)
> - ❌ Không có quyền
> - ○ Public — không cần token

---

## 🔐 Auth

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `POST` | `/api/auth/register` | public | ○ | ○ | ○ |
| `POST` | `/api/auth/login` | public | ○ | ○ | ○ |
| `POST` | `/api/auth/google` | public | ○ | ○ | ○ |
| `POST` | `/api/auth/refresh` | public | ○ | ○ | ○ |
| `POST` | `/api/auth/logout` | authenticated | ✅ | ✅ | ✅ |

---

## 👤 Users

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/users/me` | authenticated | ✅ | ✅ | ✅ |
| `PATCH` | `/api/users/me` | authenticated | ✅ | ✅ | ✅ |
| `GET` | `/api/users` | `user:read` | ✅ | ❌ | ❌ |
| `GET` | `/api/users/{id}` | `user:read` | ✅ | ❌ | ❌ |
| `PATCH` | `/api/users/{id}/ban` | `user:ban` | ✅ | ❌ | ❌ |
| `POST` | `/api/users/{id}/roles` | `user:manage_roles` | ✅ | ❌ | ❌ |
---

## 📚 Stories

| Method   | Endpoint                                     | Permission                              | ADMIN | UPLOADER | USER |
|----------|----------------------------------------------|-----------------------------------------|:-----:|:--------:|:----:|
| `GET`    | `/api/stories`                               | public                                  | ○ | ○ | ○ |
| `GET`    | `/api/stories/{slug}`                        | public                                  | ○ | ○ | ○ |
| `POST`   | `/api/stories`                               | `story:create`                          | ✅ | ✅ | ❌ |
| `PATCH`  | `/api/stories/{id}`                          | `story:update_own` / `story:update_any` | ✅ | ◐ | ❌ |
| `DELETE` | `/api/stories/{id}`                          | `story:delete_own` / `story:delete_any` | ✅ | ◐ | ❌ |
| `GET`    | `/api/stories/publish-requests`              | `admin`                                 | ✅ | ◐ | ❌ |
| `POST`   | `/api/stories/publish-requests/me`           | `story:update_own`                      | ✅ | ◐ | ❌ |
| `POST`   | `/api/stories/{id}/request-publish`          | `story:update_own`                      | ✅ | ◐ | ❌ |
| `PATCH`  | `/api/stories/publish-requests/approve/{id}` | `admin`                                 | ✅ | ❌ | ❌ |
| `PATCH`  | `/api/stories/publish-requests/reject/{id}`  | `admin`                                 | ✅ | ❌ | ❌ |
---

## 📖 Chapters

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/stories/{slug}/chapters` | public | ○ | ○ | ○ |
| `GET` | `/api/chapters/{id}` | `chapter:read_premium` (nếu locked) | ✅ | ◐ | ◐ |
| `POST` | `/api/stories/{id}/chapters` | `chapter:create` | ✅ | ✅ | ❌ |
| `PATCH` | `/api/chapters/{id}` | `chapter:update_own` | ✅ | ◐ | ❌ |
| `DELETE` | `/api/chapters/{id}` | `chapter:delete_own` | ✅ | ◐ | ❌ |

---

## 🖼️ Chapter Pages (Manga)

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/chapters/{id}/pages` | `chapter:read_premium` (nếu locked) | ✅ | ◐ | ◐ |
| `POST` | `/api/chapters/{id}/pages` | `chapter:create` | ✅ | ✅ | ❌ |
| `DELETE` | `/api/chapters/{id}/pages/{pageId}` | `chapter:delete_own` | ✅ | ◐ | ❌ |

---

## 🏷️ Genres

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/genres` | public | ○ | ○ | ○ |
| `POST` | `/api/genres` | `story:update_any` | ✅ | ❌ | ❌ |
| `DELETE` | `/api/genres/{id}` | `story:update_any` | ✅ | ❌ | ❌ |

---

## 📜 Reading History

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/reading-history` | authenticated | ✅ | ✅ | ✅ |
| `POST` | `/api/reading-history` | public (guest + user) | ○ | ○ | ○ |
| `DELETE` | `/api/reading-history/{id}` | authenticated | ✅ | ✅ | ✅ |

---

## 🔖 Bookmarks

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/bookmarks` | authenticated | ✅ | ✅ | ✅ |
| `POST` | `/api/bookmarks/{storyId}` | authenticated | ✅ | ✅ | ✅ |
| `DELETE` | `/api/bookmarks/{storyId}` | authenticated | ✅ | ✅ | ✅ |

---

## 💬 Comments

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/stories/{id}/comments` | public | ○ | ○ | ○ |
| `POST` | `/api/comments` | `comment:create` | ✅ | ✅ | ✅ |
| `DELETE` | `/api/comments/{id}` | `comment:delete_own` / `comment:delete_any` | ✅ | ◐ | ◐ |

---

## 💳 Payment & Subscription

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `POST` | `/api/payment/create` | `subscription:buy` | ✅ | ✅ | ✅ |
| `GET` | `/api/payment/return` | public (VNPay redirect) | ○ | ○ | ○ |
| `GET` | `/api/payment/vnpay-ipn` | public (VNPay server) | ○ | ○ | ○ |
| `GET` | `/api/payment/transactions` | `payment:manage` | ✅ | ❌ | ❌ |
| `GET` | `/api/payment/transactions/me` | authenticated | ✅ | ✅ | ✅ |
| `GET` | `/api/subscriptions/me` | authenticated | ✅ | ✅ | ✅ |

---

## 🛠️ Admin

| Method | Endpoint | Permission | ADMIN | UPLOADER | USER |
|--------|----------|------------|:-----:|:--------:|:----:|
| `GET` | `/api/admin/roles` | `user:manage_roles` | ✅ | ❌ | ❌ |
| `GET` | `/api/admin/permissions` | `user:manage_roles` | ✅ | ❌ | ❌ |
| `GET` | `/api/admin/dashboard` | `user:read` | ✅ | ❌ | ❌ |
| `GET` | `/api/admin/stories/pending` | `story:approve` | ✅ | ❌ | ❌ |

---

## 📝 Ghi chú — Điều kiện ◐

| Endpoint | UPLOADER | USER |
|----------|----------|------|
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

| Permission | ADMIN | UPLOADER | USER |
|---|:---:|:---:|:---:|
| `user:read` | ✅ | ❌ | ❌ |
| `user:ban` | ✅ | ❌ | ❌ |
| `user:manage_roles` | ✅ | ❌ | ❌ |
| `story:create` | ✅ | ✅ | ❌ |
| `story:update_own` | ✅ | ✅ | ❌ |
| `story:update_any` | ✅ | ❌ | ❌ |
| `story:delete_own` | ✅ | ✅ | ❌ |
| `story:delete_any` | ✅ | ❌ | ❌ |
| `story:approve` | ✅ | ❌ | ❌ |
| `chapter:create` | ✅ | ✅ | ❌ |
| `chapter:update_own` | ✅ | ✅ | ❌ |
| `chapter:delete_own` | ✅ | ✅ | ❌ |
| `chapter:read_premium` | ✅ | ✅* | ✅* |
| `comment:create` | ✅ | ✅ | ✅ |
| `comment:delete_own` | ✅ | ✅ | ✅ |
| `comment:delete_any` | ✅ | ❌ | ❌ |
| `payment:manage` | ✅ | ❌ | ❌ |
| `subscription:buy` | ✅ | ✅ | ✅ |

> \* `chapter:read_premium`: UPLOADER chỉ áp dụng cho bộ của mình · USER cần subscription active

---

*TruyệnOnline API Permission Matrix | v4.0 | 3 roles | Tháng 6/2025*

# TruyenOnline — Backend API

A Spring Boot backend API powering an online story/comic reading platform, with authentication, content management, payments, and moderation built in.

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Roles & Permissions](#roles--permissions)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Security](#security)
- [Database](#database)
- [API Documentation](#api-documentation)
- [Contact](#contact)

---

## Overview

**TruyenOnline** is a backend API written in Java Spring Boot that supports:

- 📖 **Online reading** — stories organized into chapters and pages
- 🔐 **Authentication** — login, registration, and Google OAuth2
- 👥 **User management** — role-based access for `USER`, `UPLOADER`, and `ADMIN`
- 💳 **Payments** — VNPay integration for subscription plans
- 💬 **Interactions** — comments, bookmarks, and reading history
- 🛡️ **Administration** — banners, content moderation, and violation reports

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.14 |
| Database | MySQL |
| Cache | Redis |
| ORM | Spring Data JPA + QueryDSL |
| Security | Spring Security + JWT |
| OAuth2 | Google Login |
| Payments | VNPay |
| Cloud Storage | Cloudinary |
| Email | Spring Mail + Thymeleaf |
| Migrations | Flyway |
| API Docs | Postman Collection |

---

## Project Structure

```
src/main/java/com/dacia1704/truyenonline/
├── config/                     # Application configuration
│   ├── SecurityConfig.java     # Spring Security & JWT configuration
│   └── GoogleConfig.java       # Google OAuth2 configuration
├── module/                     # Feature modules
│   ├── authentication/         # Login, registration, JWT
│   ├── user/                   # User management
│   ├── story/                  # Stories, genres, authors
│   ├── chapter/                # Story chapters
│   ├── interaction/            # Comments, bookmarks, reading history
│   ├── banner/                 # Banner management
│   ├── payment/                # VNPay payments
│   ├── media/                  # File/media management
│   └── administration/         # Moderation, audit logs
├── shared/                     # Shared code
│   ├── entity/                 # BaseEntity
│   ├── service/                # Email service
│   ├── exception/              # Global exception handling
│   └── response/                # ApiResponse, PageResponse
└── scheduler/                   # Scheduled tasks (cleanup)
```

---

## Roles & Permissions

### Roles

| Role | Description |
|---|---|
| `USER` | Regular user — reads stories, posts comments |
| `UPLOADER` | Content uploader — creates and edits stories |
| `ADMIN` | Administrator — moderates content, manages users |

### Permissions

| Permission | Description |
|---|---|
| `story:create` | Create a new story |
| `story:update_own` | Edit own story |
| `story:update_any` | Edit any story |
| `story:delete_own` | Delete own story |
| `story:delete_any` | Delete any story |
| `user:read` | View the user list |
| `user:manage_roles` | Manage user roles |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Redis (optional, for caching)

### Setup

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd truyenonline
   ```

2. **Configure the database** in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/truyenonline
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Configure JWT** (set your own secret key):

   ```properties
   jwt.signer-key=your-secret-key-min-32-characters
   jwt.access-token-validity-in-seconds=86400
   jwt.refresh-token-validity-in-seconds=604800
   ```

4. **Configure Cloudinary** (for image uploads):

   ```properties
   cloudinary.cloud-name=your-cloud-name
   cloudinary.api-key=your-api-key
   cloudinary.api-secret=your-api-secret
   ```

5. **Configure Google OAuth2**:

   ```properties
   spring.security.oauth2.client.registration.google.client-id=your-client-id
   spring.security.oauth2.client.registration.google.client-secret=your-client-secret
   ```

6. **Run the application**:

   ```bash
   mvn spring-boot:run
   ```

7. The API will be available at: `http://localhost:8080`

---

## API Reference

### Authentication — `/api/auth`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/login` | Log in with email and password |
| POST | `/register` | Register a new account |
| POST | `/google` | Log in with Google |
| POST | `/refresh` | Refresh the access token |
| POST | `/logout` | Log out |
| POST | `/logout/all` | Log out of all devices |
| POST | `/forgot-password` | Request a password reset (email) |
| POST | `/reset-password` | Reset password |
| POST | `/link/google` | Link a Google account |
| DELETE | `/link/google` | Unlink a Google account |
| GET | `/providers` | List linked accounts |
| POST | `/introspect` | Validate a token |

### Stories — `/api/stories`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/list` | Public | List stories |
| POST | `/list/admin` | ADMIN, UPLOADER | List stories (admin view) |
| GET | `/slug/{slug}` | Public | Get story by slug |
| GET | `/id/{id}` | Public | Get story by ID |
| POST | `` | UPLOADER | Create a new story |
| PATCH | `/{id}` | Owner / ADMIN | Update a story |
| DELETE | `/{id}` | Owner / ADMIN | Delete a story |
| POST | `/{id}/publish-requests` | UPLOADER | Submit a publish request |
| GET | `/publish-requests` | ADMIN | List publish requests |
| PATCH | `/publish-requests/approve/{id}` | ADMIN | Approve a publish request |
| PATCH | `/publish-requests/reject/{id}` | ADMIN | Reject a publish request |
| PATCH | `/{id}/ban` | ADMIN | Ban a story |
| PATCH | `/{id}/unban` | ADMIN | Unban a story |

### Chapters — `/api/chapters`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/{id}` | Public | Get chapter details |
| GET | `/{id}/pages` | Public | Get pages in a chapter |
| POST | `` | UPLOADER | Create a new chapter |
| PUT | `/{id}` | Owner / ADMIN | Update a chapter |
| DELETE | `/{id}` | Owner / ADMIN | Delete a chapter |

### Genres — `/api/genres`

| Method | Endpoint | Description |
|---|---|---|
| GET | `` | List genres |
| GET | `/{id}` | Get genre details |

### Authors — `/api/authors`

| Method | Endpoint | Description |
|---|---|---|
| GET | `` | List authors |

### Banners — `/api/banners`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/active` | Public | Get active banners |
| POST | `` | ADMIN | Create a banner |
| GET | `` | ADMIN | List banners |
| DELETE | `/{id}` | ADMIN | Delete a banner |

### Interactions — `/api`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/comments/chapter/{id}` | Comment on a chapter |
| GET | `/comments/chapter/{id}` | Get chapter comments |
| POST | `/bookmarks` | Bookmark a story |
| GET | `/bookmarks` | List bookmarks |
| DELETE | `/bookmarks/{id}` | Remove a bookmark |
| POST | `/reading-histories` | Record reading history |
| GET | `/reading-histories` | Get my reading history |
| GET | `/reading-histories/story/{storyId}` | Get reading history for a story |

### Payments — `/api/payment`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/create` | Create a VNPay payment |
| GET | `/vnpay-return` | VNPay return redirect |
| GET | `/vnpay-ipn` | VNPay IPN callback |
| GET | `/transactions/me` | Get my transaction history |
| GET | `/transactions` | ADMIN: get all transactions |
| GET | `/subscription-plan` | List subscription plans |
| GET | `/subscription-plan/{code}` | Get subscription plan details |

### Users — `/api/users`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/myinfo` | Get my profile |
| POST | `/{id}` | Update profile information |
| POST | `/upgrade-to-uploader` | Request an upgrade to UPLOADER |

### Administration — `/api/admin`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/users` | ADMIN | List users |
| POST | `/users` | ADMIN | Create a user |
| DELETE | `/users/soft/{id}` | ADMIN | Soft-delete a user |
| DELETE | `/users/{id}` | ADMIN | Delete a user |
| PATCH | `/users/{id}/role` | ADMIN | Update a user's role |
| GET | `/audit-logs` | ADMIN | View audit logs |
| GET | `/moderation-actions` | ADMIN | List moderation actions |
| GET | `/ban-appeals` | ADMIN | List ban appeals |
| PATCH | `/ban-appeals/{id}/approve` | ADMIN | Approve a ban appeal |
| PATCH | `/ban-appeals/{id}/reject` | ADMIN | Reject a ban appeal |

---

## Security

### JWT Tokens

- **Access token**: valid for 24 hours
- **Refresh token**: valid for 7 days
- **Device tracking**: Device-ID and Session-ID are recorded per session

### Public Endpoints

The following endpoints do not require authentication:

- `/api/auth/login`, `/api/auth/register`, `/api/auth/refresh`
- `/api/stories/list`, `/api/stories/slug/{slug}`, `/api/stories/id/{id}`
- `/api/chapters/{id}`, `/api/chapters/{id}/pages`
- `/api/banners/active`
- `/api/genres`, `/api/authors`
- `/api/payment/vnpay-return`, `/api/payment/vnpay-ipn`

### CORS

Allowed origins:

- `http://localhost:3000`
- `https://daciaxx.site`
- `https://www.daciaxx.site`

---

## Database

### Migrations (Flyway)

| File | Description |
|---|---|
| `V1__init.sql` | `users`, `roles`, `permissions` tables |
| `V2__story_module.sql` | `stories`, `chapters`, `genres` tables |
| `V3__banner_and_ban_appeal.sql` | Banner and ban appeal tables |

### Core Tables

- `users` — application users
- `roles`, `permissions` — role-based access control
- `stories` — stories
- `chapters` — story chapters
- `chapter_pages` — pages within a chapter
- `genres` — story genres
- `authors` — authors
- `comments` — user comments
- `bookmarks` — saved stories
- `reading_histories` — reading progress
- `transactions` — payment transactions
- `subscriptions` — active subscriptions
- `banners` — promotional banners
- `moderation_actions` — content moderation records
- `ban_appeals` — appeals against bans
- `audit_logs` — action history
- `refresh_tokens` — JWT refresh tokens

---

## API Documentation

Import the Postman collection:

```
doc/TruyenOnline-API.postman_collection.json
```

**How to use it:**

1. Import the collection into Postman.
2. Log in via `/api/auth/login` to obtain an `accessToken`.
3. The token is automatically stored in the `accessToken` environment variable.
4. Authenticated requests will use the Bearer token automatically.

---

## Contact

- **Email**: support@daciaxx.site
- **Website**: https://daciaxx.site

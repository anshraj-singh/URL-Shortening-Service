<div align="center">

# 🔗 URL Shortening Service

### A production-ready URL Shortener REST API built with Spring Boot & MongoDB

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.18-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green?style=for-the-badge&logo=mongodb)](https://www.mongodb.com/atlas)
[![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io)
[![Spring Security](https://img.shields.io/badge/Spring_Security-✓-brightgreen?style=for-the-badge&logo=springsecurity)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

> Shorten URLs, track clicks, manage with admin panel — all secured with JWT Authentication.

</div>
 
---

## Overview
This is a backend service for shortening long URLs built using Spring Boot and MongoDB. It provides functionalities for creating, retrieving, updating, and deleting short URLs, along with tracking access statistics.

## Features
- Shorten long URLs
- Retrieve original URLs using short codes
- Update and delete short URLs
- Access statistics for each short URL
- MongoDB storage for URL data

## API Endpoints

### **URL Management**

| HTTP Method | Endpoint               | Description                       |
|-------------|------------------------|-----------------------------------|
| `POST`      | `/shorten`             | Create a new short URL            |
| `GET`       | `/{shortCode}`         | Retrieve original URL by short code|
| `PUT`       | `/{shortCode}`         | Update the original URL           |
| `DELETE`    | `/{shortCode}`         | Delete a short URL                |
| `GET`       | `/{shortCode}/stats`   | Get access statistics for a short URL |

## URL Shortener Model

### UrlShortener Entity
The `UrlShortener` entity represents the shortened URL details, including:
- `id`: Unique identifier for the URL.
- `originalUrl`: The original long URL.
- `shortCode`: The generated short code.
- `createdAt`: Timestamp for when the URL was created.
- `updatedAt`: Timestamp for when the URL was last updated.
- `accessCount`: Number of times the short URL has been accessed.

## Changes update

### 1. Error Handling
- Added error handling in the `UrlShortenerController` class to manage exceptions and provide meaningful HTTP responses.
- Each endpoint now logs errors and returns a 500 Internal Server Error response if an exception occurs.

### 2. Logging
- Enhanced logging to capture error messages for better debugging.

### Register
**POST** `/api/v1/auth/register`

Request:
```json
{
  "name": "Ansh Singh",
  "email": "ansh@gmail.com",
  "password": "secret123"
}
```

Response `201 Created`:
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "id": "6657abc123def456",
    "name": "Ansh Singh",
    "email": "ansh@gmail.com",
    "role": "ROLE_USER",
    "createdAt": "2025-01-15T10:30:00",
    "message": "Account created successfully. Please login to get your token."
  }
}
```
 
---

### Login
**POST** `/api/v1/auth/login`

Request:
```json
{
  "user": "Ansh Singh",
  "password": "secret123"
}
```

Response `200 OK`:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBbnNoIFNpbmdoIn0.xyzand"
  }
}
```

> **Copy this token** — use it in every protected request:
> `Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...`
 
---

## 🔗 URL API Endpoints

### Create Short URL
**POST** `/api/v1/urls` 🔒 *Requires token*

Request:
```json
{
  "url": "https://www.github.com/karan-singh"
}
```

Response `201 Created`:
```json
{
  "success": true,
  "message": "Short URL created successfully",
  "data": {
    "id": "6657abc123def456",
    "originalUrl": "https://www.github.com/karan-singh",
    "shortUrl": "http://localhost:8080/r/xK9mQz",
    "shortCode": "xK9mQz",
    "accessCount": 0,
    "active": true,
    "banned": false,
    "createdAt": "2025-01-15T10:30:00"
  }
}
```

> **Duplicate check:** Submit the same URL again → same short code returned, no duplicate created.
 
---

### Redirect to Original URL
**GET** `/r/{code}` 🌐 *Public*

```
GET http://localhost:8080/r/xK9mQz
→ 302 Found
→ Location: https://www.github.com/karan-singh
```
Access count increments on every redirect.
 
---

### Preview URL (No Click Counted)
**GET** `/api/v1/urls/{code}/preview` 🌐 *Public*

Response `200 OK`:
```json
{
  "success": true,
  "message": "Preview fetched — no click counted",
  "data": {
    "originalUrl": "https://www.github.com/anshraj-singh",
    "shortUrl": "http://localhost:8080/r/xK9mQz",
    "accessCount": 5,
    "active": true
  }
}
```
 
---

### Get URL Info
**GET** `/api/v1/urls/{code}` 🔒 *Requires token*
 
---

### Get URL Stats
**GET** `/api/v1/urls/{code}/stats` 🔒 *Requires token*

Response `200 OK`:
```json
{
  "success": true,
  "message": "Stats fetched successfully",
  "data": {
    "shortCode": "xK9mQz",
    "originalUrl": "https://www.github.com/karan-singh",
    "shortUrl": "http://localhost:8080/r/xK9mQz",
    "accessCount": 42,
    "active": true,
    "banned": false,
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T14:22:10"
  }
}
```
 
---

### Update URL
**PUT** `/api/v1/urls/{code}` 🔒 *Requires token*

Request:
```json
{
  "newUrl": "https://www.github.com/karan-singh/new-repo"
}
```
 
---

### Delete URL (Soft Delete)
**DELETE** `/api/v1/urls/{code}` 🔒 *Requires token*

Response `200 OK`:
```json
{
  "success": true,
  "message": "URL deactivated successfully"
}
```
 
---

## 👑 Admin API Endpoints

> Admin endpoints require a token with `ROLE_ADMIN`.
> To make a user admin, manually update `role` field in MongoDB to `ROLE_ADMIN`.
 
---

### Get All URLs (Paginated)
**GET** `/api/v1/admin/urls?page=0&size=20` 🔒 *Admin only*

Response `200 OK`:
```json
{
  "success": true,
  "message": "Page 0 — total 150 URL(s)",
  "data": {
    "content": [ { "shortCode": "xK9mQz", "accessCount": 42 }, "..." ],
    "totalElements": 150,
    "totalPages": 8,
    "currentPage": 0,
    "last": false
  }
}
```
 
---

### Get Active URLs
**GET** `/api/v1/admin/urls/active` 🔒 *Admin only*
 
---

### Get Top 10 Clicked URLs
**GET** `/api/v1/admin/urls/top` 🔒 *Admin only*

Response `200 OK`:
```json
{
  "success": true,
  "message": "Top URLs fetched",
  "data": [
    { "shortCode": "xK9mQz", "accessCount": 821, "originalUrl": "https://github.com" },
    { "shortCode": "aB3xY9", "accessCount": 430, "originalUrl": "https://youtube.com" }
  ]
}
```
 
---

### Get Flagged (Suspicious) URLs
**GET** `/api/v1/admin/urls/flagged` 🔒 *Admin only*

> Returns URLs with 10+ clicks in the last 1 minute — spam/bot detection.

Response `200 OK`:
```json
{
  "success": true,
  "message": "3 suspicious URL(s) found",
  "data": [
    { "shortCode": "xK9mQz", "accessCount": 500, "banned": false }
  ]
}
```
 
---

### Ban a URL
**POST** `/api/v1/admin/urls/{code}/ban` 🔒 *Admin only*

> Sets `active=false` and `banned=true`.
> After ban, `GET /r/{code}` returns error — redirect permanently blocked.

Response `200 OK`:
```json
{
  "success": true,
  "message": "URL banned successfully",
  "data": {
    "shortCode": "xK9mQz",
    "active": false,
    "banned": true
  }
}
```
 
---

### Restore a URL
**PATCH** `/api/v1/admin/urls/{code}/restore` 🔒 *Admin only*
 
---

### Soft Delete a URL
**DELETE** `/api/v1/admin/urls/{code}` 🔒 *Admin only*
 
---

### Hard Delete a URL
**DELETE** `/api/v1/admin/urls/{code}/hard` 🔒 *Admin only*

> ⚠️ Permanent — cannot be undone.
 
---

### Bulk Delete URLs
**DELETE** `/api/v1/admin/urls/bulk` 🔒 *Admin only*

Request:
```json
{
  "shortCodes": ["abc123", "xK9mQz", "pQ7rTu"]
}
```

Response `200 OK`:
```json
{
  "success": true,
  "message": "Bulk delete successful",
  "data": "3 URL(s) deactivated"
}
```
 
---

## 🔐 Security Overview

| Endpoint Pattern | Access |
|---|---|
| `POST /api/v1/auth/**` | 🌐 Public |
| `GET /r/**` | 🌐 Public |
| `GET /api/v1/urls/*/preview` | 🌐 Public |
| `GET,POST,PUT,DELETE /api/v1/urls/**` | 🔒 Authenticated (any valid token) |
| `/api/v1/admin/**` | 👑 Admin only (ROLE_ADMIN) |
 
---

## 🗄️ MongoDB Collections

### `users` collection
```json
{
  "_id": "6657abc123def456",
  "name": "Ansh Singh",
  "email": "ansh@gmail.com",
  "password": "$2a$10$hashed...",
  "role": "ROLE_USER",
  "createdAt": "2025-01-15T10:30:00"
}
```

### `urls` collection
```json
{
  "_id": "6657xyz789abc123",
  "originalUrl": "https://www.github.com/xxxxxxxxxxxxx",
  "shortCode": "xK9mQz",
  "userId": "6657abc123def456",
  "accessCount": 42,
  "active": true,
  "banned": false,
  "lastAccessedAt": "2025-01-15T14:22:10",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T14:22:10"
}
```

### MongoDB Indexes
| Index | Fields | Type | Purpose |
|---|---|---|---|
| `shortCode` | shortCode | Unique | Fast redirect lookup |
| `idx_originalUrl_active` | originalUrl + active | Compound | Duplicate check |
| `idx_active_accessCount` | active + accessCount | Compound | Top URLs query |
 
---

## 📋 Complete API Reference

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/auth/register` | 🌐 | Register new account |
| POST | `/api/v1/auth/login` | 🌐 | Login, get JWT token |
| POST | `/api/v1/urls` | 🔒 | Create short URL |
| GET | `/r/{code}` | 🌐 | Redirect to original URL |
| GET | `/api/v1/urls/{code}/preview` | 🌐 | Preview URL destination |
| GET | `/api/v1/urls/{code}` | 🔒 | Get URL info |
| GET | `/api/v1/urls/{code}/stats` | 🔒 | Get click statistics |
| PUT | `/api/v1/urls/{code}` | 🔒 | Update destination URL |
| DELETE | `/api/v1/urls/{code}` | 🔒 | Soft delete URL |
| GET | `/api/v1/admin/urls` | 👑 | All URLs paginated |
| GET | `/api/v1/admin/urls/active` | 👑 | Active URLs |
| GET | `/api/v1/admin/urls/top` | 👑 | Top 10 clicked |
| GET | `/api/v1/admin/urls/flagged` | 👑 | Suspicious URLs |
| GET | `/api/v1/admin/urls/{code}` | 👑 | Any URL detail |
| GET | `/api/v1/admin/urls/{code}/stats` | 👑 | Any URL stats |
| POST | `/api/v1/admin/urls/{code}/ban` | 👑 | Ban a URL |
| PATCH | `/api/v1/admin/urls/{code}/restore` | 👑 | Restore URL |
| DELETE | `/api/v1/admin/urls/{code}` | 👑 | Soft delete any URL |
| DELETE | `/api/v1/admin/urls/{code}/hard` | 👑 | Permanent delete |
| DELETE | `/api/v1/admin/urls/bulk` | 👑 | Bulk delete URLs |
 
---

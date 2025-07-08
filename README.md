
# URL Shortening Service

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


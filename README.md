# 🚀 Notification Retry Service
### A modern microservice for reliable notification delivery, intelligent retries, full auditing, and production-grade observability.

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen)
![Build](https://img.shields.io/badge/Build-Maven-blue)

---

## 📌 Overview

**Notification Retry Service** is a modern, fault-tolerant microservice designed to handle notification delivery with high reliability and complete traceability.

It provides:

- Asynchronous notification processing
- Intelligent retry mechanism with exponential backoff
- Full auditing of every notification attempt
- Pagination, filtering, and admin endpoints
- Production-grade observability (Actuator + Prometheus)
- Clean, extensible architecture with DTOs, DAOs, and MapStruct
- Automatic API documentation via OpenAPI/Swagger
- Developed following enterprise-grade best practices

---

## 🏗️ Tech Stack

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring Data JPA**
- **H2 / PostgreSQL**
- **Spring Scheduler**
- **MapStruct (DTO mapping)**
- **Micrometer + Actuator + Prometheus**
- **Springdoc OpenAPI (Swagger UI)**
- **Lombok**
- **Maven**

---

## 🔥 Key Features

### ✔ Intelligent Retry Engine
- Exponential backoff (1m → 2m → 4m → 8m → …)
- Distinguishes transient failures from permanent ones
- Manual “retry-now” endpoint for debugging

### ✔ Enterprise-grade Auditing
- Stores every attempt in `NotificationAttempt`
- Success/failure tracking with timestamps
- Error messages stored per attempt

### ✔ Observability (metrics)
Exported through Actuator + Prometheus:

- `notifications.sent`
- `notifications.failed`
- `notifications.attempts`
- `notifications.retries.scheduled`

### ✔ Clean API Design
- DTO-based request/response
- Pagination + filters
- Fully documented via Swagger/OpenAPI

---

## ⚙️ Run the Application

```bash
mvn clean install
java -jar target/notification-retry-service-1.0.0.jar
<div align="center">

# 🏋️ Gym Lorza API

**REST API para la gestión integral de un gimnasio**

[![Java](https://img.shields.io/badge/Java-25_LTS-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-22c55e?style=for-the-badge)](./LICENSE)
[![REST API](https://img.shields.io/badge/REST-API-FF6C37?style=for-the-badge&logo=postman&logoColor=white)]()
[![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white)]()
[![Lombok](https://img.shields.io/badge/Lombok-enabled-pink?style=for-the-badge)]()

[🇬🇧 English version](#english)

[Descripción](#-descripción) · [Características](#-características) · [Stack Tecnológico](#️-stack-tecnológico) · [Requisitos](#-requisitos-previos) · [Instalación](#-instalación) · [Estructura](#️-estructura-del-proyecto) · [Entidades](#️-entidades-y-modelo-de-datos) · [Endpoints](#-endpoints) · [Reglas de Negocio](#-reglas-de-negocio) · [Ejemplos](#-ejemplos-de-uso) · [Contribuir](#-contribuir)

</div>

---

## 📋 Descripción

**Gym Lorza API** es una API RESTful construida con **Spring Boot 4** que permite gestionar de forma completa las operaciones de un gimnasio. Proporciona un backend robusto para la administración de usuarios, entrenadores y actividades, con sistema de inscripciones, relaciones entre entidades, validación de datos, reglas de negocio y una arquitectura en capas limpia y mantenible.

Diseñada para integrarse con un frontend React, expone endpoints REST estándar con respuestas HTTP semánticas y soporte CORS preconfigurado.

---

## ✨ Características

- CRUD completo para **usuarios**, **entrenadores** y **actividades**
- Relación **OneToMany** entre entrenadores y actividades
- Relación **ManyToMany** entre actividades y usuarios (sistema de inscripciones)
- **4 reglas de negocio** para inscripciones con excepciones específicas
- **6 endpoints de consulta** para vistas del negocio (cursos futuros, usuarios activos, etc.)
- Validación de datos con **Jakarta Bean Validation**
- DTOs + Mappers para desacoplar la capa de presentación del modelo de dominio
- Gestión centralizada de excepciones con **@RestControllerAdvice**
- Transacciones declarativas con **@Transactional**
- Reducción de boilerplate con **Lombok**
- CORS habilitado para frontend en `http://localhost:3000`
- Autoconfiguración del esquema de base de datos con `ddl-auto`

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Rol en el proyecto |
|---|---|---|
| Java | 25 LTS | Lenguaje principal |
| Spring Boot | 4.0.5 | Framework base y autoconfiguración |
| Spring Web MVC | — | Capa REST y manejo de peticiones HTTP |
| Spring Data JPA | — | Abstracción de acceso a datos |
| Hibernate | — | Implementación ORM |
| MySQL | 8+ | Motor de base de datos relacional |
| Lombok | 1.18.44 | Reducción de boilerplate |
| Jakarta Bean Validation | — | Validación declarativa de DTOs |
| Maven | 3.9+ | Gestión de dependencias y ciclo de vida |

---

## 📦 Requisitos Previos

| Herramienta | Versión mínima | Enlace |
|---|---|---|
| Java (JDK) | 25 | [Adoptium Temurin](https://adoptium.net/) |
| Maven | 3.9 | [Apache Maven](https://maven.apache.org/download.cgi) |
| MySQL Server | 8.0 | [MySQL Community](https://dev.mysql.com/downloads/mysql/) |

> 💡 **Tip:** Recomendamos usar [SDKMAN](https://sdkman.io/) para gestionar versiones de Java:
> ```bash
> sdk install java 25-tem
> ```

---

## 🚀 Instalación

### 1. Clona el repositorio

```bash
git clone https://github.com/Grupo-1-Gimnasio/Backend.git
cd Backend
```

### 2. Configura la base de datos

```sql
CREATE DATABASE gym_lorza CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configura las credenciales

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_lorza
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
spring.jpa.hibernate.ddl-auto=update
```

### 4. Compila y ejecuta

```bash
./mvnw clean install
./mvnw spring-boot:run
```

La API estará disponible en: **`http://localhost:8080`**

---

## 🗂️ Estructura del Proyecto

```
src/main/java/com/inditex/gym_lorza/
├── controller/
│   ├── ActivityController.java
│   ├── TrainerController.java
│   └── UserController.java
├── service/
│   ├── ActivityService.java
│   ├── TrainerService.java
│   ├── UserService.java
│   └── EnrollmentService.java
├── repository/
│   ├── ActivityRepository.java
│   ├── TrainerRepository.java
│   └── UserRepository.java
├── model/
│   ├── Activity.java
│   ├── Trainer.java
│   └── User.java
├── dto/
│   ├── ActivityRequestDTO.java
│   ├── ActivityResponseDTO.java
│   ├── TrainerRequestDTO.java
│   ├── TrainerResponseDTO.java
│   ├── UserRequestDTO.java
│   └── UserResponseDTO.java
├── mapper/
│   ├── ActivityMapper.java
│   ├── TrainerMapper.java
│   └── UserMapper.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ObjectNotFoundException.java
│   ├── PaymentRequiredException.java
│   ├── UserAlreadyEnrolledException.java
│   ├── MaxActivitiesReachedException.java
│   └── TrainerNotActiveException.java
└── GymLorzaApplication.java
```

> **Arquitectura:** `Controller → Service → Repository → DB`, con DTOs en la frontera API y Mappers para la conversión.

---

## 🗃️ Entidades y Modelo de Datos

### 👤 User

| Campo | Tipo | Descripción | Requerido |
|---|---|---|---|
| `id` | `Long` | Identificador único (autoincremental) | — |
| `name` | `String` | Nombre del usuario | ✅ |
| `surname` | `String` | Apellido del usuario | ✅ |
| `dni` | `String` | DNI del usuario | ✅ |
| `startYear` | `Integer` | Año de alta en el gimnasio | ✅ |
| `isActive` | `Boolean` | Estado activo / inactivo | ✅ |
| `annualFeePaid` | `Boolean` | Si ha pagado la cuota anual | ✅ |
| `image` | `String` | URL de la imagen | — |

### 🧑‍🏫 Trainer

| Campo | Tipo | Descripción | Requerido |
|---|---|---|---|
| `id` | `Long` | Identificador único (autoincremental) | — |
| `name` | `String` | Nombre del entrenador | ✅ |
| `dni` | `String` | DNI del entrenador | ✅ |
| `hiringYear` | `Integer` | Año de contratación | ✅ |
| `isHired` | `Boolean` | Si está contratado actualmente | ✅ |
| `image` | `String` | URL de la imagen | — |

### 🏃 Activity

| Campo | Tipo | Descripción | Requerido |
|---|---|---|---|
| `id` | `Long` | Identificador único (autoincremental) | — |
| `title` | `String` | Nombre de la actividad | ✅ |
| `description` | `String` | Descripción de la actividad | ✅ |
| `price` | `BigDecimal` | Precio de la actividad | ✅ |
| `date` | `LocalDate` | Fecha de la actividad | ✅ |
| `startHour` | `LocalTime` | Hora de inicio | ✅ |
| `endHour` | `LocalTime` | Hora de fin | ✅ |
| `image` | `String` | URL de la imagen | — |
| `trainer` | `Trainer` | Entrenador asignado (FK) | ✅ |

### 🔗 Diagrama de Relaciones

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   Trainer   │         │     Activity     │         │  activity_users │
│─────────────│         │──────────────────│         │─────────────────│
│ id (PK)     │◄────────│ id (PK)          │────────►│ activity_id(FK) │
│ name        │  1 : N  │ title            │         │ user_id (FK)    │
│ dni         │         │ description      │         └────────┬────────┘
│ hiringYear  │         │ price            │                  │
│ isHired     │         │ date             │                  │
│ image       │         │ startHour        │                  │
└─────────────┘         │ endHour          │         ┌────────┴────────┐
                        │ image            │         │      User      │
                        │ trainer_id (FK)  │         │────────────────│
                        └──────────────────┘         │ id (PK)        │
                                              N : M  │ name           │
                                                     │ surname        │
                                                     │ dni            │
                                                     │ startYear      │
                                                     │ isActive       │
                                                     │ annualFeePaid  │
                                                     │ image          │
                                                     └────────────────┘
```

---

## 📡 Endpoints

**Base URL:** `http://localhost:8080`

### 👤 Usuarios — `/users`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/users` | Obtener todos los usuarios |
| `GET` | `/users/active` | Obtener usuarios activos |
| `GET` | `/users/{id}` | Obtener usuario por ID |
| `GET` | `/users/{id}/activities` | Actividades donde está inscrito |
| `POST` | `/users` | Crear nuevo usuario |
| `PUT` | `/users/{id}` | Actualizar usuario |
| `DELETE` | `/users/{id}` | Eliminar usuario |

### 🧑‍🏫 Entrenadores — `/trainers`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/trainers` | Obtener todos los entrenadores |
| `GET` | `/trainers/active` | Obtener entrenadores activos |
| `GET` | `/trainers/{id}` | Obtener entrenador por ID |
| `GET` | `/trainers/{id}/activities` | Actividades que imparte |
| `POST` | `/trainers` | Crear nuevo entrenador |
| `PUT` | `/trainers/{id}` | Actualizar entrenador |
| `DELETE` | `/trainers/{id}` | Eliminar entrenador |

### 🏃 Actividades — `/activities`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/activities` | Obtener todas las actividades |
| `GET` | `/activities/future` | Obtener actividades futuras |
| `GET` | `/activities/{id}` | Obtener actividad por ID |
| `POST` | `/activities` | Crear nueva actividad |
| `PUT` | `/activities/{id}` | Actualizar actividad |
| `DELETE` | `/activities/{id}` | Eliminar actividad |

### 📋 Inscripciones — `/activities/{id}/users`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/activities/{id}/users` | Alumnos inscritos en una actividad |
| `POST` | `/activities/{activityId}/users/{userId}` | Inscribir usuario en actividad |
| `DELETE` | `/activities/{activityId}/users/{userId}` | Desinscribir usuario |

---

## 🛡️ Reglas de Negocio

Las inscripciones están protegidas por 4 criterios de aceptación:

| Regla | Excepción | HTTP |
|---|---|---|
| Un usuario no puede inscribirse si no ha pagado su cuota anual | `PaymentRequiredException` | `402` |
| Un usuario no puede inscribirse dos veces en la misma actividad | `UserAlreadyEnrolledException` | `409` |
| Un usuario no puede estar inscrito en más de 3 actividades futuras | `MaxActivitiesReachedException` | `403` |
| Un profesor no puede asignarse a una actividad si no está dado de alta | `TrainerNotActiveException` | `400` |

---

## 💡 Ejemplos de Uso

### Crear un entrenador

```http
POST /trainers
Content-Type: application/json

{
  "name": "Ana",
  "dni": "87654321B",
  "hiringYear": 2022,
  "isHired": true,
  "image": "https://example.com/ana.jpg"
}
```

### Crear una actividad

```http
POST /activities
Content-Type: application/json

{
  "title": "Pilates",
  "description": "Clase de pilates suelo nivel inicial",
  "price": 12.50,
  "date": "2026-05-10",
  "startHour": "10:00:00",
  "endHour": "11:00:00",
  "image": null,
  "trainerId": 1
}
```

### Inscribir un usuario en una actividad

```http
POST /activities/1/users/3
```

**Respuesta exitosa:** `201 Created`

**Respuesta con error de negocio:**

```json
HTTP 402 Payment Required
"El usuario con id 3 no ha realizado el pago"
```

---

## 📊 Códigos de Respuesta HTTP

| Código | Estado | Cuándo ocurre |
|---|---|---|
| `200` | OK | GET o PUT completados |
| `201` | Created | POST completado |
| `204` | No Content | DELETE completado |
| `400` | Bad Request | Validación fallida o entrenador no activo |
| `402` | Payment Required | Cuota anual no pagada |
| `403` | Forbidden | Máximo de actividades alcanzado |
| `404` | Not Found | Recurso no encontrado |
| `409` | Conflict | Inscripción duplicada |

---

## 🤝 Contribuir

1. Haz un **fork** del repositorio
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit con [Conventional Commits](https://www.conventionalcommits.org/): `git commit -m 'feat: descripción'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un **Pull Request**

---

## 📄 Licencia

Este proyecto está bajo la licencia descrita en el archivo [LICENSE](./LICENSE).

---

<div align="center">

Hecho con ❤️ por **Grupo 1 — Inditex Gym Bootcamp**

</div>

---
---

<div align="center">

<a name="english"></a>

# 🏋️ Gym Lorza API

**REST API for comprehensive gym management**

[![Java](https://img.shields.io/badge/Java-25_LTS-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)

[🇪🇸 Versión en español](#spanish)

[Description](#-description-1) · [Features](#-features) · [Tech Stack](#️-tech-stack) · [Prerequisites](#-prerequisites) · [Installation](#-installation-1) · [Project Structure](#️-project-structure) · [Entities](#️-entities-and-data-model) · [Endpoints](#-endpoints-1) · [Business Rules](#️-business-rules) · [Examples](#-usage-examples) · [Contributing](#-contributing)

</div>

---

## 📋 Description

**Gym Lorza API** is a RESTful API built with **Spring Boot 4** for managing gym operations. It provides a robust backend for user, trainer, and activity management, with an enrollment system, entity relationships, data validation, business rules, and a clean layered architecture.

Designed to integrate with a React frontend, it exposes standard REST endpoints with semantic HTTP responses and pre-configured CORS support.

---

## ✨ Features

- Full CRUD for **users**, **trainers**, and **activities**
- **OneToMany** relationship between trainers and activities
- **ManyToMany** relationship between activities and users (enrollment system)
- **4 business rules** for enrollment with specific exceptions
- **6 query endpoints** for business views (future courses, active users, etc.)
- Data validation with **Jakarta Bean Validation**
- DTOs + Mappers to decouple the presentation layer from the domain model
- Centralized exception handling with **@RestControllerAdvice**
- Declarative transactions with **@Transactional**
- Boilerplate reduction with **Lombok**
- CORS enabled for frontend at `http://localhost:3000`
- Auto-configured database schema with `ddl-auto`

---

## 🛠️ Tech Stack

| Technology | Version | Role |
|---|---|---|
| Java | 25 LTS | Main language |
| Spring Boot | 4.0.5 | Base framework and autoconfiguration |
| Spring Web MVC | — | REST layer and HTTP request handling |
| Spring Data JPA | — | Data access abstraction |
| Hibernate | — | ORM implementation |
| MySQL | 8+ | Relational database engine |
| Lombok | 1.18.44 | Boilerplate reduction |
| Jakarta Bean Validation | — | Declarative DTO validation |
| Maven | 3.9+ | Dependency management and build lifecycle |

---

## 📦 Prerequisites

| Tool | Minimum Version | Link |
|---|---|---|
| Java (JDK) | 25 | [Adoptium Temurin](https://adoptium.net/) |
| Maven | 3.9 | [Apache Maven](https://maven.apache.org/download.cgi) |
| MySQL Server | 8.0 | [MySQL Community](https://dev.mysql.com/downloads/mysql/) |

> 💡 **Tip:** We recommend using [SDKMAN](https://sdkman.io/) for Java version management:
> ```bash
> sdk install java 25-tem
> ```

---

## 🚀 Installation

### 1. Clone the repository

```bash
git clone https://github.com/Grupo-1-Gimnasio/Backend.git
cd Backend
```

### 2. Set up the database

```sql
CREATE DATABASE gym_lorza CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_lorza
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

### 4. Build and run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The API will be available at: **`http://localhost:8080`**

---

## 🗂️ Project Structure

```
src/main/java/com/inditex/gym_lorza/
├── controller/          # REST controllers — handle HTTP requests and responses
├── service/             # Business logic — orchestrate repositories and rules
├── repository/          # Data access — JPA interfaces with custom queries
├── model/               # JPA entities — mapped to MySQL tables
├── dto/                 # Data Transfer Objects — decouple API from domain model
├── mapper/              # Static mappers — convert between entities and DTOs
├── exception/           # Custom exceptions and centralized handler
└── GymLorzaApplication.java
```

> **Architecture:** Request flow follows `Controller → Service → Repository → DB`, with DTOs at the API boundary and Mappers for conversion.

---

## 🗃️ Entities and Data Model

### 👤 User

| Field | Type | Description | Required |
|---|---|---|---|
| `id` | `Long` | Auto-incremental unique identifier | — |
| `name` | `String` | User's first name | ✅ |
| `surname` | `String` | User's last name | ✅ |
| `dni` | `String` | National ID number | ✅ |
| `startYear` | `Integer` | Year of gym membership | ✅ |
| `isActive` | `Boolean` | Active / inactive status | ✅ |
| `annualFeePaid` | `Boolean` | Whether annual fee has been paid | ✅ |
| `image` | `String` | Image URL | — |

### 🧑‍🏫 Trainer

| Field | Type | Description | Required |
|---|---|---|---|
| `id` | `Long` | Auto-incremental unique identifier | — |
| `name` | `String` | Trainer's name | ✅ |
| `dni` | `String` | National ID number | ✅ |
| `hiringYear` | `Integer` | Year of hire | ✅ |
| `isHired` | `Boolean` | Currently employed | ✅ |
| `image` | `String` | Image URL | — |

### 🏃 Activity

| Field | Type | Description | Required |
|---|---|---|---|
| `id` | `Long` | Auto-incremental unique identifier | — |
| `title` | `String` | Activity name | ✅ |
| `description` | `String` | Activity description | ✅ |
| `price` | `BigDecimal` | Activity price | ✅ |
| `date` | `LocalDate` | Activity date | ✅ |
| `startHour` | `LocalTime` | Start time | ✅ |
| `endHour` | `LocalTime` | End time | ✅ |
| `image` | `String` | Image URL | — |
| `trainer` | `Trainer` | Assigned trainer (FK) | ✅ |

### 🔗 Relationships Diagram

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   Trainer   │         │     Activity     │         │  activity_users │
│─────────────│         │──────────────────│         │─────────────────│
│ id (PK)     │◄────────│ id (PK)          │────────►│ activity_id(FK) │
│ name        │  1 : N  │ title            │         │ user_id (FK)    │
│ dni         │         │ description      │         └────────┬────────┘
│ hiringYear  │         │ price            │                  │
│ isHired     │         │ date             │                  │
│ image       │         │ startHour        │                  │
└─────────────┘         │ endHour          │         ┌────────┴────────┐
                        │ image            │         │      User      │
                        │ trainer_id (FK)  │         │────────────────│
                        └──────────────────┘         │ id (PK)        │
                                              N : M  │ name           │
                                                     │ surname        │
                                                     │ dni            │
                                                     │ startYear      │
                                                     │ isActive       │
                                                     │ annualFeePaid  │
                                                     │ image          │
                                                     └────────────────┘
```

---

## 📡 Endpoints

**Base URL:** `http://localhost:8080`

### 👤 Users — `/users`

| Method | Route | Description |
|---|---|---|
| `GET` | `/users` | Get all users |
| `GET` | `/users/active` | Get active users |
| `GET` | `/users/{id}` | Get user by ID |
| `GET` | `/users/{id}/activities` | Activities user is enrolled in |
| `POST` | `/users` | Create new user |
| `PUT` | `/users/{id}` | Update user |
| `DELETE` | `/users/{id}` | Delete user |

### 🧑‍🏫 Trainers — `/trainers`

| Method | Route | Description |
|---|---|---|
| `GET` | `/trainers` | Get all trainers |
| `GET` | `/trainers/active` | Get active trainers |
| `GET` | `/trainers/{id}` | Get trainer by ID |
| `GET` | `/trainers/{id}/activities` | Activities taught by trainer |
| `POST` | `/trainers` | Create new trainer |
| `PUT` | `/trainers/{id}` | Update trainer |
| `DELETE` | `/trainers/{id}` | Delete trainer |

### 🏃 Activities — `/activities`

| Method | Route | Description |
|---|---|---|
| `GET` | `/activities` | Get all activities |
| `GET` | `/activities/future` | Get upcoming activities |
| `GET` | `/activities/{id}` | Get activity by ID |
| `POST` | `/activities` | Create new activity |
| `PUT` | `/activities/{id}` | Update activity |
| `DELETE` | `/activities/{id}` | Delete activity |

### 📋 Enrollment — `/activities/{id}/users`

| Method | Route | Description |
|---|---|---|
| `GET` | `/activities/{id}/users` | Students enrolled in an activity |
| `POST` | `/activities/{activityId}/users/{userId}` | Enroll user in activity |
| `DELETE` | `/activities/{activityId}/users/{userId}` | Unenroll user |

---

## 🛡️ Business Rules

Enrollments are protected by 4 acceptance criteria:

| Rule | Exception | HTTP |
|---|---|---|
| A user cannot enroll without paying their annual fee | `PaymentRequiredException` | `402` |
| A user cannot enroll twice in the same activity | `UserAlreadyEnrolledException` | `409` |
| A user cannot be enrolled in more than 3 future activities | `MaxActivitiesReachedException` | `403` |
| An inactive trainer cannot be assigned to an activity | `TrainerNotActiveException` | `400` |

---

## 💡 Usage Examples

### Create a trainer

```http
POST /trainers
Content-Type: application/json

{
  "name": "Ana",
  "dni": "87654321B",
  "hiringYear": 2022,
  "isHired": true,
  "image": "https://example.com/ana.jpg"
}
```

### Create an activity

```http
POST /activities
Content-Type: application/json

{
  "title": "Pilates",
  "description": "Beginner floor pilates class",
  "price": 12.50,
  "date": "2026-05-10",
  "startHour": "10:00:00",
  "endHour": "11:00:00",
  "image": null,
  "trainerId": 1
}
```

### Enroll a user in an activity

```http
POST /activities/1/users/3
```

**Success response:** `201 Created`

**Business rule error response:**

```json
HTTP 402 Payment Required
"El usuario con id 3 no ha realizado el pago"
```

---

## 📊 HTTP Response Codes

| Code | Status | When it occurs |
|---|---|---|
| `200` | OK | Successful GET or PUT |
| `201` | Created | Successful POST |
| `204` | No Content | Successful DELETE |
| `400` | Bad Request | Validation failure or inactive trainer |
| `402` | Payment Required | Annual fee not paid |
| `403` | Forbidden | Maximum activities reached |
| `404` | Not Found | Resource not found |
| `409` | Conflict | Duplicate enrollment |

---

## 🤝 Contributing

1. **Fork** the repository
2. Create a branch: `git checkout -b feature/new-feature`
3. Commit using [Conventional Commits](https://www.conventionalcommits.org/): `git commit -m 'feat: description'`
4. Push: `git push origin feature/new-feature`
5. Open a **Pull Request**

---

## 📄 License

This project is under the license described in the [LICENSE](./LICENSE) file.

---

<div align="center">

Made with ❤️ by **Grupo 1 — Inditex Gym Bootcamp**

</div>

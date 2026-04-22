
<div align="center">

# 🏋️ Gym Lorza API

**REST API para la gestión integral de un gimnasio**

[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-22c55e?style=for-the-badge)](./LICENSE)
[![REST API](https://img.shields.io/badge/REST-API-FF6C37?style=for-the-badge&logo=postman&logoColor=white)]()
[![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white)]()
[![Lombok](https://img.shields.io/badge/Lombok-enabled-pink?style=for-the-badge)]()

[Descripción](#-descripción) · [Características](#-características) · [Requisitos](#-requisitos-previos) · [Instalación](#-instalación) · [Configuración](#️-configuración-avanzada) · [Estructura](#️-estructura-del-proyecto) · [Entidades](#️-entidades-y-modelo-de-datos) · [Endpoints](#-endpoints) · [Ejemplos](#-ejemplos-de-uso) · [Códigos HTTP](#-códigos-de-respuesta-http) · [Tests](#-tests) · [Docker](#-docker-opcional) · [Contribuir](#-contribuir)

</div>

---

## 📋 Descripción

**Gym Lorza API** es una API RESTful construida con **Spring Boot 3** que permite gestionar de forma completa las operaciones de un gimnasio. Proporciona un backend robusto para la administración de usuarios, entrenadores y actividades, con relaciones entre entidades, validación de datos integrada y una arquitectura en capas limpia y mantenible.

Diseñada para integrarse fácilmente con cualquier frontend moderno (React, Angular, Vue, etc.), expone endpoints REST estándar con respuestas HTTP semánticas y soporte CORS preconfigurado.

---

## ✨ Características

- ✅ CRUD completo para **usuarios**, **entrenadores** y **actividades**
- ✅ Relación entre entrenadores y actividades (1:N)
- ✅ Validación de datos con **Jakarta Bean Validation**
- ✅ Mapeo objeto-relacional con **Spring Data JPA / Hibernate**
- ✅ CORS habilitado para frontend en `http://localhost:3000`
- ✅ Respuestas HTTP semánticas (`200`, `201`, `204`, `404`, `409`)
- ✅ Arquitectura en capas (Controller → Service → Repository → Model)
- ✅ DTOs para desacoplar la capa de presentación del modelo de dominio
- ✅ Reducción de boilerplate con **Lombok** (`@Getter`, `@Setter`, `@Builder`, etc.)
- ✅ Autoconfiguración del esquema de base de datos con `ddl-auto=update`
- ✅ Logging de SQL activado para facilitar el desarrollo y depuración

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Rol en el proyecto |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.0 | Framework base y autoconfiguración |
| Spring Web MVC | — | Capa REST y manejo de peticiones HTTP |
| Spring Data JPA | — | Abstracción de acceso a datos |
| Hibernate | — | Implementación ORM |
| MySQL | 8+ | Motor de base de datos relacional |
| Lombok | — | Reducción de boilerplate (getters, setters, builders) |
| Jakarta Bean Validation | — | Validación declarativa de entidades |
| Maven | 3.8+ | Gestión de dependencias y ciclo de vida |

---

## 📦 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado y configurado:

| Herramienta | Versión mínima | Enlace |
|---|---|---|
| Java (JDK) | 17 | [Adoptium Temurin](https://adoptium.net/) |
| Maven | 3.8 | [Apache Maven](https://maven.apache.org/download.cgi) |
| MySQL Server | 8.0 | [MySQL Community](https://dev.mysql.com/downloads/mysql/) |

> 💡 **Tip:** Puedes verificar tus versiones instaladas con:
> ```bash
> java -version
> mvn -version
> mysql --version
> ```

---

## 🚀 Instalación

### 1. Clona el repositorio

```bash
git clone https://github.com/tu-usuario/gym-lorza-api.git
cd gym-lorza-api
```

### 2. Configura la base de datos

Conéctate a MySQL y crea la base de datos:

```sql
CREATE DATABASE gym_lorza CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configura las credenciales

Edita el archivo `src/main/resources/application.properties`:

```properties
spring.application.name=gym_lorza

spring.datasource.url=jdbc:mysql://localhost:3306/gym_lorza
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 4. Instala las dependencias y compila

```bash
mvn clean install
```

### 5. Ejecuta la aplicación

```bash
mvn spring-boot:run
```

La API estará disponible en: **`http://localhost:8080`**

---

## ⚙️ Configuración Avanzada

### Variables de entorno (recomendado para producción)

En lugar de hardcodear las credenciales en `application.properties`, puedes usar variables de entorno:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/gym_lorza}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

Y exportarlas antes de lanzar la aplicación:

```bash
export DB_USERNAME=mi_usuario
export DB_PASSWORD=mi_contraseña
mvn spring-boot:run
```

### Perfiles de Spring

Puedes crear perfiles separados para desarrollo y producción:

- `application-dev.properties` → configuración local con logs activos
- `application-prod.properties` → configuración de producción sin logs SQL

Activa un perfil con:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Configuración de CORS

CORS está habilitado por defecto para `http://localhost:3000`. Si necesitas cambiar el origen permitido, localiza la configuración CORS en el controlador o clase de configuración y ajusta el valor de `allowedOrigins`.

---

## 🗂️ Estructura del Proyecto

```
gym-lorza-api/
├── src/
│   ├── main/
│   │   ├── java/com/inditex/gym_lorza/
│   │   │   ├── controller/         # Controladores REST — reciben y responden peticiones HTTP
│   │   │   │   ├── UserController.java
│   │   │   │   ├── TrainerController.java
│   │   │   │   └── ActivityController.java
│   │   │   ├── service/            # Lógica de negocio — orquesta repositorios y reglas
│   │   │   │   ├── UserService.java
│   │   │   │   ├── TrainerService.java
│   │   │   │   └── ActivityService.java
│   │   │   ├── repository/         # Acceso a datos — interfaces JPA
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── TrainerRepository.java
│   │   │   │   └── ActivityRepository.java
│   │   │   ├── model/              # Entidades JPA — mapeadas a tablas MySQL
│   │   │   │   ├── User.java
│   │   │   │   ├── Trainer.java
│   │   │   │   └── Activity.java
│   │   │   └── dto/                # Data Transfer Objects — desacoplan la API del modelo
│   │   │       ├── UserDTO.java
│   │   │       ├── TrainerDTO.java
│   │   │       └── ActivityDTO.java
│   │   └── resources/
│   │       └── application.properties   # Configuración principal
│   └── test/
│       └── java/com/inditex/gym_lorza/ # Tests unitarios e integración
├── pom.xml                              # Dependencias y configuración Maven
└── README.md
```

> **Arquitectura:** El flujo de una petición sigue el patrón `Controller → Service → Repository → DB`, garantizando separación de responsabilidades y facilidad de testing.

---

## 🗃️ Entidades y Modelo de Datos

### 👤 User

Representa a un miembro del gimnasio.

| Campo | Tipo | Descripción | Requerido |
|---|---|---|---|
| `id` | `Long` | Identificador único (autoincremental) | — |
| `name` | `String` | Nombre del usuario | ✅ |
| `surname` | `String` | Apellido del usuario | ✅ |
| `dni` | `String` | DNI (único) | ✅ |
| `startYear` | `Integer` | Año de alta en el gimnasio | ✅ |
| `isActive` | `Boolean` | Estado activo / inactivo | ✅ |
| `image` | `String` | URL de imagen de perfil | ❌ |

### 🧑‍🏫 Trainer

Representa a un entrenador del gimnasio.

| Campo | Tipo | Descripción | Requerido |
|---|---|---|---|
| `id` | `Long` | Identificador único (autoincremental) | — |
| `name` | `String` | Nombre del entrenador | ✅ |
| `dni` | `String` | DNI (único) | ✅ |
| `hiringYear` | `Integer` | Año de contratación | ✅ |
| `isHired` | `Boolean` | Si está contratado actualmente | ✅ |
| `image` | `String` | URL de imagen de perfil | ❌ |
| `activities` | `List<Activity>` | Actividades que imparte | — |

### 🏃 Activity

Representa una clase o actividad del gimnasio.

| Campo | Tipo | Descripción | Requerido |
|---|---|---|---|
| `id` | `Long` | Identificador único (autoincremental) | — |
| `title` | `String` | Nombre de la actividad | ✅ |
| `price` | `String` | Precio de la actividad | ✅ |
| `weekDay` | `Integer` | Día de la semana (`0`=Lun · `1`=Mar · ... · `6`=Dom) | ✅ |
| `startHour` | `LocalTime` | Hora de inicio (formato `HH:mm`) | ✅ |
| `endHour` | `LocalTime` | Hora de fin (formato `HH:mm`) | ✅ |
| `image` | `String` | URL de imagen de la actividad | ❌ |
| `trainer` | `Trainer` | Entrenador asignado (FK) | ✅ |

### 🔗 Diagrama de Relaciones

```
┌─────────────┐         ┌──────────────────┐
│   Trainer   │         │     Activity     │
│─────────────│         │──────────────────│
│ id (PK)     │◄────────│ id (PK)          │
│ name        │  1 : N  │ title            │
│ dni         │         │ price            │
│ hiringYear  │         │ weekDay          │
│ isHired     │         │ startHour        │
│ image       │         │ endHour          │
└─────────────┘         │ image            │
                        │ trainer_id (FK)  │
                        └──────────────────┘

┌─────────────┐
│    User     │
│─────────────│
│ id (PK)     │
│ name        │
│ surname     │
│ dni         │
│ startYear   │
│ isActive    │
│ image       │
└─────────────┘
```

Un **Trainer** puede tener múltiples **Activities** asignadas. Cada **Activity** pertenece a un único entrenador. **User** es una entidad independiente que representa a los miembros del gimnasio.

---

## 📡 Endpoints

**Base URL:** `http://localhost:8080`
**CORS habilitado para:** `http://localhost:3000`

### 👤 Usuarios — `/users`

| Método | Ruta | Descripción | Body | Respuesta |
|---|---|---|---|---|
| `GET` | `/users` | Obtener todos los usuarios | — | `200 OK` |
| `GET` | `/users/{id}` | Obtener usuario por ID | — | `200 OK` / `404` |
| `POST` | `/users` | Crear nuevo usuario | JSON User | `201 Created` |
| `PUT` | `/users/{id}` | Actualizar usuario existente | JSON User | `200 OK` |
| `DELETE` | `/users/{id}` | Eliminar usuario | — | `204 No Content` / `404` / `409` |

### 🧑‍🏫 Entrenadores — `/trainers`

| Método | Ruta | Descripción | Body | Respuesta |
|---|---|---|---|---|
| `GET` | `/trainers` | Obtener todos los entrenadores | — | `200 OK` |
| `GET` | `/trainers/{id}` | Obtener entrenador por ID | — | `200 OK` / `404` |
| `POST` | `/trainers` | Crear nuevo entrenador | JSON Trainer | `201 Created` |
| `PUT` | `/trainers/{id}` | Actualizar entrenador existente | JSON Trainer | `200 OK` |
| `DELETE` | `/trainers/{id}` | Eliminar entrenador | — | `204 No Content` / `404` / `409` |

### 🏃 Actividades — `/activities`

| Método | Ruta | Descripción | Body | Respuesta |
|---|---|---|---|---|
| `GET` | `/activities` | Obtener todas las actividades | — | `200 OK` |
| `GET` | `/activities/{id}` | Obtener actividad por ID | — | `200 OK` / `404` |
| `POST` | `/activities` | Crear nueva actividad | JSON Activity | `201 Created` |
| `PUT` | `/activities/{id}` | Actualizar actividad existente | JSON Activity | `200 OK` |
| `DELETE` | `/activities/{id}` | Eliminar actividad | — | `204 No Content` / `404` / `409` |

---

## 💡 Ejemplos de Uso

> Puedes probar los endpoints con [Postman](https://www.postman.com/), [Insomnia](https://insomnia.rest/) o `curl`.

### Crear un usuario

```http
POST /users
Content-Type: application/json

{
  "name": "Carlos",
  "surname": "García López",
  "dni": "12345678A",
  "startYear": 2024,
  "isActive": true,
  "image": "https://example.com/foto.jpg"
}
```

**Respuesta exitosa `201 Created`:**

```json
{
  "id": 1,
  "name": "Carlos",
  "surname": "García López",
  "dni": "12345678A",
  "startYear": 2024,
  "isActive": true,
  "image": "https://example.com/foto.jpg"
}
```

---

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

---

### Crear una actividad y asignarla a un entrenador

```http
POST /activities
Content-Type: application/json

{
  "title": "Yoga Matutino",
  "price": "25.00",
  "weekDay": 1,
  "startHour": "08:00",
  "endHour": "09:00",
  "image": "https://example.com/yoga.jpg",
  "trainer": {
    "id": 1
  }
}
```

---

### Obtener todos los entrenadores

```http
GET /trainers
```

**Respuesta `200 OK`:**

```json
[
  {
    "id": 1,
    "name": "Ana",
    "dni": "87654321B",
    "hiringYear": 2022,
    "isHired": true,
    "image": "https://example.com/ana.jpg",
    "activities": [
      {
        "id": 1,
        "title": "Yoga Matutino",
        "price": "25.00",
        "weekDay": 1,
        "startHour": "08:00",
        "endHour": "09:00"
      }
    ]
  }
]
```

---

### Actualizar el estado de un usuario

```http
PUT /users/1
Content-Type: application/json

{
  "name": "Carlos",
  "surname": "García López",
  "dni": "12345678A",
  "startYear": 2024,
  "isActive": false
}
```

---

### Eliminar una actividad

```http
DELETE /activities/1
```

**Respuesta exitosa:** `204 No Content`

---

## 📊 Códigos de Respuesta HTTP

| Código | Estado | Cuándo ocurre |
|---|---|---|
| `200 OK` | Éxito | GET o PUT completados correctamente |
| `201 Created` | Creado | POST completado — recurso creado |
| `204 No Content` | Sin contenido | DELETE completado correctamente |
| `400 Bad Request` | Petición inválida | El body no supera la validación de Jakarta |
| `404 Not Found` | No encontrado | El recurso con el ID indicado no existe |
| `409 Conflict` | Conflicto | Intento de eliminar un recurso con dependencias activas |
| `500 Internal Server Error` | Error servidor | Error inesperado en la aplicación |

---

## 🧪 Tests

Para ejecutar los tests del proyecto:

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte detallado
mvn test -Dsurefire.useFile=false
```

Los tests se encuentran en `src/test/java/com/inditex/gym_lorza/`.

---

## 🐳 Docker (Opcional)

Si prefieres levantar el entorno con Docker, puedes crear un `docker-compose.yml` en la raíz del proyecto:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: gym_lorza
      MYSQL_ROOT_PASSWORD: rootpassword
    ports:
      - "3306:3306"

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      DB_URL: jdbc:mysql://mysql:3306/gym_lorza
      DB_USERNAME: root
      DB_PASSWORD: rootpassword
```

```bash
docker-compose up --build
```

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Para cambios importantes, abre primero un **issue** para discutir qué te gustaría modificar.

### Flujo de trabajo

1. Haz un **fork** del repositorio
2. Crea una rama para tu funcionalidad:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. Haz commit de tus cambios siguiendo [Conventional Commits](https://www.conventionalcommits.org/):
   ```bash
   git commit -m 'feat: añade endpoint de búsqueda por DNI'
   ```
4. Haz push a tu rama:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
5. Abre un **Pull Request** describiendo los cambios realizados

### Convención de commits

| Prefijo | Uso |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `docs:` | Cambios en documentación |
| `refactor:` | Refactorización sin cambio de comportamiento |
| `test:` | Añadir o corregir tests |
| `chore:` | Tareas de mantenimiento (deps, config) |

---

## 📄 Licencia

Este proyecto está bajo la licencia descrita en el archivo [LICENSE](./LICENSE).

---

## 📬 Contacto

Si tienes dudas, sugerencias o encuentras algún bug, abre un [issue](https://github.com/tu-usuario/gym-lorza-api/issues) en el repositorio.

---

<div align="center">

Hecho con ❤️ por <strong>Lorza</strong>

⭐ Si este proyecto te ha sido útil, considera darle una estrella en GitHub ⭐

</div>

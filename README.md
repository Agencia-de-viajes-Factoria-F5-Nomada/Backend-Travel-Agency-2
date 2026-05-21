# Backend - Agencia de Viajes

API REST desarrollada con **Spring Boot 4.0.6** y **Java 25** para la gestión integral de una agencia de viajes. Administra usuarios, hoteles, autobuses, conductores, empleados, ofertas, viajes, trayectos y reservas con lógica de negocio avanzada: tarifas por edad, descuentos de grupo, validación de disponibilidad, control de solapamiento de conductores, dashboard directivo y email transaccional asíncrono.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | Hibernate |
| Spring Validation | Hibernate Validator |
| MySQL | 8+ |
| H2 (tests) | — |
| Lombok | Última |
| MapStruct | 1.6.3 |
| Maven | 3.9.14 (wrapper) |
| Cloudinary | 1.39.0 |
| Auth0 java-jwt | 4.4.0 |
| jBCrypt | 0.4 |
| SpringDoc OpenAPI | 2.8.5 |
| Spring Mail + Thymeleaf | via parent |
| Spring Retry | 2.0.12 |
| AspectJ Weaver | — |
| JUnit 5 + Mockito + H2 | Tests |

---

## Requisitos previos

- Java 25 instalado
- MySQL 8+ en ejecución
- Maven instalado (o usar `mvnw.cmd`)
- Cuenta en Cloudinary (subida de imágenes)
- Variable de entorno `JWT_SECRET` con una clave de mínimo 32 caracteres
- Variables de entorno para email SMTP (opcional para desarrollo)

---

## Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/Agencia-de-viajes-Factoria-F5-Nomada/Backend-Travel-Agency-2.git
cd Backend-Travel-Agency-2
```

### 2. Crear la base de datos

```sql
CREATE DATABASE travel_agency;
```

### 3. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```properties
DB_URL=jdbc:mysql://localhost:3306/travel_agency
DB_USER=root
DB_PASSWORD=root
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
JWT_SECRET=tu_clave_secreta_de_minimo_32_caracteres
MAIL_HOST=tu_servidor_smtp
MAIL_PORT=587
MAIL_USERNAME=tu_email
MAIL_PASSWORD=tu_password
```

O edita directamente `src/main/resources/application.properties`.

### 4. Arrancar el proyecto

```bash
./mvnw.cmd spring-boot:run
```

La API estará disponible en `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

### 5. Datos iniciales

En el primer arranque, `DataInitializer` crea automáticamente 4 empleados semilla (password `123456`):

| Email | Rol |
|---|---|
| carlos@nomada.es | ADMIN |
| ana@nomada.es | EMPLOYEE |
| sofia@nomada.es | EMPLOYEE |
| david@nomada.es | EMPLOYEE |

Además, el script de datos para pruebas está en `src/main/resources/db/migration/database.sql` y se ejecuta al iniciar la aplicación.

---

## Estructura del proyecto

```
src/main/java/com/inditex/g1_agencia_viajes/
├── G1AgenciaViajesApplication.java      # Entry point (@EnableAsync, @EnableRetry)
├── config/                              # CloudinaryConfig, CorsConfig, AsyncConfig, DataInitializer
├── controller/                          # 12 controladores REST
├── dto/                                 # 24 DTOs de request/response
├── event/                               # BookingCreatedEvent
├── exception/                           # 11 excepciones + GlobalExceptionHandler
├── mapper/                              # 9 mappers (MapStruct)
├── model/                               # 9 entidades + 3 enums
├── repository/                          # 9 repositorios JPA
├── security/                            # JwtUtil, JwtFilter, SecurityConfig, LoginRateLimiter
└── service/                             # 14 servicios (incl. BookingPricingService, EmailService)

src/test/java/com/inditex/g1_agencia_viajes/
├── service/                             # Tests de servicio
├── controller/                          # Tests de controlador
├── repository/                          # Tests de repositorio
└── G1AgenciaViajesApplicationTests      # Test de contexto

docs/                                    # Documentación adicional
├── AUDITORIA_PROYECTO.md / .html        # Auditoría completa del proyecto
├── plan-transporte-y-segmentos.md       # Plan futuro de transporte y segmentos
├── seed-completo-historico.sql          # Seed histórico completo
└── seed-nacional-expandido.sql          # Seed de viajes nacionales
```

---

## Entidades

### User (Clientes)
| Campo | Tipo | Descripción |
|---|---|---|
| id | Long | Identificador único |
| name | String | Nombre |
| surname | String | Apellido |
| email | String | Email único |
| dni | String | DNI |
| passport | String | Pasaporte |
| age | Integer | Edad |
| tutorId | User (FK) | Tutor para menores |
| active | Boolean | Soft delete |

### Employee
| Campo | Tipo | Descripción |
|---|---|---|
| employeeId | Long | Identificador único |
| name | String | Nombre |
| surname | String | Apellido |
| email | String | Email único (dominio @nomada.es) |
| gender | Enum | MALE / FEMALE / NON_BINARY |
| workHour | Integer | Horas semanales |
| hired | Boolean | Contratado |
| role | Enum | EMPLOYEE / ADMIN |
| password | String | Hash BCrypt |
| active | Boolean | Soft delete |

### Hotel
| Campo | Tipo | Descripción |
|---|---|---|
| id | Long | Identificador único |
| name | String | Nombre |
| address | String | Dirección |
| city | String | Ciudad |
| country | String | País |
| stars | Integer | Estrellas (1-5) |
| capacity | Integer | Capacidad total |
| availablePlaces | Integer | Plazas disponibles |
| halfBoardPrice | Double | Precio media pensión |
| fullBoardPrice | Double | Precio pensión completa |
| imageUrl | String | URL imagen (Cloudinary) |
| active | Boolean | Soft delete |

### Bus
| Campo | Tipo | Descripción |
|---|---|---|
| id | Long | Identificador único |
| licensePlate | String | Matrícula (única, formato 0000-AAA) |
| capacity | Integer | Plazas totales |
| bath | Boolean | Servicio |
| wifi | Boolean | Servicio |
| AC | Boolean | Servicio |
| USB | Boolean | Servicio |
| active | Boolean | Soft delete |

### Driver
| Campo | Tipo | Descripción |
|---|---|---|
| id | Long | Identificador único |
| name | String | Nombre |
| phone | String | Teléfono |
| licenceActive | Boolean | Licencia en vigor |
| imageUrl | String | URL imagen (Cloudinary) |
| active | Boolean | Soft delete |

### Travel
| Campo | Tipo | Descripción |
|---|---|---|
| id | Long | Identificador único |
| destiny | String | Destino |
| startDate | LocalDate | Fecha inicio |
| endDate | LocalDate | Fecha fin |
| sale | Boolean | En oferta |
| availablePlaces | Integer | Plazas disponibles |
| active | Boolean | Soft delete |
| hotel | Hotel (FK) | Hotel asociado |
| offer | Offer (FK) | Oferta aplicada |

### Offer
| Campo | Tipo | Descripción |
|---|---|---|
| offerId | Long | Identificador único |
| discountPercentage | Double | Porcentaje descuento |
| startDate | LocalDate | Inicio oferta |
| endDate | LocalDate | Fin oferta |

### Booking
| Campo | Tipo | Descripción |
|---|---|---|
| bookingId | Long | Identificador único |
| boughtDate | LocalDateTime | Fecha compra |
| typeBoard | Enum | HALF / FULL |
| isGroup | Boolean | Descuento grupo |
| totalPrice | Double | Precio total (calculado) |
| travel | Travel (FK) | Viaje reservado |
| employee | Employee (FK) | Empleado que gestiona |
| customers | List\<User\> | Pasajeros (N:M) |

### TripSegment
| Campo | Tipo | Descripción |
|---|---|---|
| id | Long | Identificador único |
| origin | String | Origen |
| destination | String | Destino |
| startTime | LocalDateTime | Salida |
| endTime | LocalDateTime | Llegada |
| travel | Travel (FK) | Viaje asociado |
| bus | Bus (FK) | Autobús asignado |
| driver | Driver (FK) | Conductor asignado |

### Enums
| Enum | Valores |
|---|---|
| Gender | MALE, FEMALE, NON_BINARY |
| Role | EMPLOYEE, ADMIN |
| TypeBoard | HALF, FULL |

---

## Endpoints de la API

> Todos los GET de listas aceptan paginación: `?page=0&size=10&sort=campo,asc`

### Autenticación — `/api/authentication`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/api/authentication/login` | Público | Iniciar sesión (devuelve JWT) |

### Usuarios — `/api/users`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/users` | JWT | Listar todos (paginado) |
| GET | `/api/users/{id}` | JWT | Obtener por ID |
| GET | `/api/users/active` | JWT | Listar solo activos |
| POST | `/api/users` | JWT | Crear usuario |
| PUT | `/api/users/{id}` | JWT | Actualizar |
| DELETE | `/api/users/{id}` | JWT | Eliminar (soft delete) |

### Empleados — `/api/employees`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/employees` | JWT | Listar todos (paginado) |
| GET | `/api/employees/{id}` | JWT | Obtener por ID |
| POST | `/api/employees` | ADMIN | Crear empleado |
| PUT | `/api/employees/{id}` | ADMIN | Actualizar |
| DELETE | `/api/employees/{id}` | ADMIN | Eliminar |

### Hoteles — `/api/hotels`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/hotels` | Público | Listar todos (paginado) |
| GET | `/api/hotels/{id}` | Público | Obtener por ID |
| GET | `/api/hotels/active` | Público | Listar solo activos |
| GET | `/api/hotels/available` | Público | Con plazas disponibles |
| POST | `/api/hotels` | JWT | Crear hotel |
| PUT | `/api/hotels/{id}` | JWT | Actualizar |
| DELETE | `/api/hotels/{id}` | JWT | Eliminar (soft delete) |

### Autobuses — `/api/buses`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/buses` | JWT | Listar todos (paginado) |
| GET | `/api/buses/{id}` | JWT | Obtener por ID |
| POST | `/api/buses` | JWT | Crear autobús |
| PUT | `/api/buses/{id}` | JWT | Actualizar |
| DELETE | `/api/buses/{id}` | JWT | Eliminar (soft delete) |

### Conductores — `/api/drivers`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/drivers` | JWT | Listar todos (paginado) |
| GET | `/api/drivers/{id}` | JWT | Obtener por ID |
| GET | `/api/drivers/active` | JWT | Listar solo activos |
| POST | `/api/drivers` | JWT | Crear conductor |
| PUT | `/api/drivers/{id}` | JWT | Actualizar |
| DELETE | `/api/drivers/{id}` | JWT | Eliminar (soft delete) |

### Viajes — `/api/travels`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/travels` | Público | Listar todos los activos (paginado) |
| GET | `/api/travels/{id}` | Público | Obtener por ID |
| GET | `/api/travels/available` | Público | Viajes futuros con plazas |
| GET | `/api/travels/sale` | Público | Viajes en oferta |
| POST | `/api/travels` | JWT | Crear viaje |
| PUT | `/api/travels/{id}` | JWT | Actualizar |
| DELETE | `/api/travels/{id}` | JWT | Eliminar (soft delete) |

### Ofertas — `/api/offers`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/offers` | Público | Listar todas (paginado) |
| GET | `/api/offers/{id}` | Público | Obtener por ID |
| POST | `/api/offers` | JWT | Crear oferta |
| PUT | `/api/offers/{id}` | JWT | Actualizar |
| DELETE | `/api/offers/{id}` | JWT | Eliminar |

### Reservas — `/api/bookings`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/bookings` | JWT | Listar todas (paginado) |
| GET | `/api/bookings/{id}` | JWT | Obtener por ID |
| POST | `/api/bookings` | JWT | Crear reserva |
| POST | `/api/bookings/quote` | JWT | Cotizar precio sin reservar |
| PUT | `/api/bookings/{id}` | JWT | Actualizar reserva |
| POST | `/api/bookings/{bookingId}/customers` | JWT | Agregar pasajeros |
| DELETE | `/api/bookings/{id}` | JWT | Cancelar reserva |

### Trayectos — `/api/trip-segments`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/trip-segments` | JWT | Listar todos (paginado) |
| GET | `/api/trip-segments/{id}` | JWT | Obtener por ID |
| POST | `/api/trip-segments` | JWT | Crear trayecto |
| PUT | `/api/trip-segments/{id}` | JWT | Actualizar |
| DELETE | `/api/trip-segments/{id}` | JWT | Eliminar |

### Dashboard — `/api/dashboard`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/api/dashboard` | JWT | Indicadores para la dirección |

### Imágenes — `/api/images`
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/api/images/upload` | JWT | Subir imagen a Cloudinary |
| DELETE | `/api/images/delete/{publicId}` | JWT | Eliminar imagen de Cloudinary |

---

## Seguridad

- **JWT (Auth0 java-jwt):** tokens HMAC256 con 24h de expiración. Claims: `subject` (email), `id` (employeeId), `role`, `issuer` ("agencia-viajes").
- **Roles de acceso:**
  - `EMPLOYEE` — solo lectura en `/api/employees`; acceso completo al resto de recursos según JWT.
  - `ADMIN` — acceso completo a toda la API.
- **Rate limiting:** `LoginRateLimiter` bloquea IPs tras 5 intentos fallidos de login en 15 minutos (HTTP 429).
- **CORS:** permitido para `localhost:5173`–`5177`.
- **Endpoints públicos** (sin JWT): `POST /api/authentication/login`, GET de `/api/travels`, `/api/hotels`, `/api/offers`, Swagger UI y OpenAPI docs.

---

## Reglas de negocio

- **Tarifas por edad:**
  | Rango | Edad | Tarifa |
  |---|---|---|
  | Baby | 0–2 | 5% del precio base |
  | Child | 3–11 | 60% del precio base |
  | Adult | 12–64 | 100% del precio base |
  | Pensioner | 65+ | 90% del precio base |
- **Descuento por grupo:** 5% adicional si ≥ 10 pasajeros y `isGroup == true`.
- **Descuento por oferta:** porcentaje configurable si el viaje tiene `sale == true` y una oferta asociada.
- Un menor (< 18) no puede viajar sin tutor adulto.
- No se puede vender un viaje si el autobús o el hotel están completos (con bloqueo pesimista).
- No se pueden vender viajes pasados.
- Un conductor no puede conducir 2 autobuses en el mismo horario.
- Los empleados deben usar email con dominio `@nomada.es`.
- Los viajes internacionales requieren pasaporte para todos los pasajeros.
- **Soft delete:** todas las entidades usan `active = true` como borrado lógico.
- **Confirmación por email:** se envía un email detallado tras cada reserva con lista de pasajeros, precios y descuentos aplicados (asíncrono vía `@TransactionalEventListener`).

---

## Funcionalidades destacadas

| Funcionalidad | Descripción |
|---|---|
| **Motor de precios** | Calcula precios con descuentos por edad (baby/child/adult/pensioner), grupo y ofertas |
| **Email transaccional** | Envío asíncrono de confirmación con template Thymeleaf (event-driven: `BookingCreatedEvent` → `@TransactionalEventListener`) |
| **Dashboard directivo** | Viajes por año, ganancias del año actual, top 3 viajes por facturación |
| **Cloudinary** | Subida y eliminación de imágenes para hoteles y conductores |
| **Protección fuerza bruta** | Rate limiter por IP en login (5 intentos / 15 min) |
| **Paginación** | Todos los listados soportan paginación y ordenación vía `Pageable` |
| **Seed automático** | 4 empleados iniciales creados al primer arranque |
| **Roles JWT** | EMPLOYEE (solo lectura en empleados) y ADMIN (acceso total) |
| **MapStruct** | Mapeo eficiente entre entidades y DTOs |
| **Spring Retry** | Reintentos ante fallos transitorios en operaciones |

---

## Testing

El proyecto cuenta con **~219 tests** distribuidos en **30 archivos**:

```bash
# Ejecutar todos los tests
./mvnw.cmd clean test

# Ejecutar solo tests de servicio
./mvnw.cmd test -Dtest="*ServiceTest"

# Ejecutar solo tests de repositorio
./mvnw.cmd test -Dtest="*RepositoryTest"
```

| Tipo | Archivos |
|---|---|
| Servicio | 10 |
| Controlador | 11 |
| Repositorio | 8 |
| Contexto | 1 |

Los tests de repositorio usan H2 en memoria (`@DataJpaTest` + `@ActiveProfiles("test")`).

**Librerías de testing:** JUnit 5, Mockito, Spring Boot Test, `@WebMvcTest`, `@DataJpaTest`.

---

## Documentación adicional (`docs/`)

| Archivo | Descripción |
|---|---|
| `AUDITORIA_PROYECTO.md` / `.html` | Auditoría completa del proyecto (backend + frontend) |
| `plan-transporte-y-segmentos.md` | Plan futuro: entidad `Transport`, enums `TravelType` y `TravelSegment` |
| `seed-completo-historico.sql` | Seed histórico con 14 ofertas, hoteles, buses, viajes y reservas |
| `seed-nacional-expandido.sql` | Datos semilla de viajes nacionales adicionales |

---

## Ramas del repositorio

| Rama | Descripción |
|---|---|
| `main` | Código en producción |
| `develop` | Integración de features |
| `feature/*` | Features en desarrollo |
| `fix/*` | Correcciones |
| `test/*` | Testing |

---

## Equipo

Proyecto desarrollado por el **Grupo 1** de **Factoria F5** — Proyecto Final 2026.

# Backend - Agencia de Viajes

API REST desarrollada con **Spring Boot 4.0.6** para la gestión de una agencia de viajes. Permite administrar usuarios, hoteles, autobuses, conductores, empleados, ofertas, viajes y reservas, con lógica de negocio avanzada: tarifas por edad, descuentos de grupo, validación de disponibilidad, control de solapamiento de conductores, dashboard directivo y email transaccional.

---

## Tecnologias

| Tecnologia | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | Hibernate |
| Spring Validation | Hibernate Validator |
| MySQL | 8+ |
| Lombok | Ultima |
| Maven | 3.9.14 (wrapper) |
| Cloudinary | 1.39.0 |
| Auth0 java-jwt | 4.4.0 |
| jBCrypt | 0.4 |
| SpringDoc OpenAPI | 2.8.5 |
| Spring Mail + Thymeleaf | via parent |
| JUnit 5 + Mockito + H2 | Tests |

---

## Requisitos previos

- Java 25 instalado
- MySQL 8+ en ejecucion
- Maven instalado (o usar `mvnw.cmd`)
- Cuenta en Cloudinary (subida de imagenes)
- Variable de entorno `JWT_SECRET` con una clave de minimo 32 caracteres
- Variables de entorno para email SMTP (opcional para desarrollo)

---

## Instalacion y configuracion

### 1. Clonar el repositorio

```bash
git clone https://github.com/Agencia-de-viajes-PF-Factoria-F5/Backend-Travel-Agency.git
cd Backend-Travel-Agency
```

### 2. Crear la base de datos

```sql
CREATE DATABASE travel_agency;
```

### 3. Configurar variables de entorno

Crea un archivo `.env` en la raiz del proyecto:

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

La API estara disponible en `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Estructura del proyecto

```
src/main/java/com/inditex/g1_agencia_viajes/
├── G1AgenciaViajesApplication.java      # Entry point
├── config/                              # CloudinaryConfig, CorsConfig, AsyncConfig
├── controller/                          # 12 controladores REST
│   ├── AuthenticationController.java    # POST /api/authentication/login
│   ├── BookingController.java           # /api/bookings
│   ├── BusController.java               # /api/buses
│   ├── CloudinaryController.java        # /api/images
│   ├── DashboardController.java         # /api/dashboard
│   ├── DriverController.java            # /api/drivers
│   ├── EmployeeController.java          # /api/employees
│   ├── HotelController.java             # /api/hotels
│   ├── OfferController.java             # /api/offers
│   ├── TravelController.java            # /api/travels
│   ├── TripSegmentController.java       # /api/trip-segments
│   └── UserController.java              # /api/users
├── dto/                                 # 24 DTOs de request/response
├── exception/                           # 10 excepciones + GlobalExceptionHandler
├── mapper/                              # 9 mappers
├── model/                               # 9 entidades + 3 enums
├── repository/                          # 9 repositorios JPA
├── security/                            # JwtUtil, JwtFilter, SecurityConfig
└── service/                             # 13 servicios

src/test/java/com/inditex/g1_agencia_viajes/
├── service/                             # 10 tests de servicio
├── controller/                          # 11 tests de controlador
├── repository/                          # 8 tests de repositorio
└── G1AgenciaViajesApplicationTests      # Test de contexto
```

---

## Entidades

### User
| Campo | Tipo | Descripcion |
|---|---|---|
| id | Long | Identificador unico |
| name | String | Nombre |
| surname | String | Apellido |
| email | String | Email unico |
| dni | String | DNI |
| passport | String | Pasaporte |
| age | Integer | Edad |
| tutorId | User (FK) | Tutor para menores |
| active | Boolean | Soft delete |

### Employee
| Campo | Tipo | Descripcion |
|---|---|---|
| employeeId | Long | Identificador unico |
| name | String | Nombre |
| surname | String | Apellido |
| email | String | Email unico (dominio @nomada.es) |
| gender | Enum | MALE / FEMALE / NON_BINARY |
| workHour | Integer | Horas semanales |
| hired | Boolean | Contratado |
| role | Enum | VIEWER / EDITOR / ADMIN |
| password | String | Hash BCrypt |

### Hotel
| Campo | Tipo | Descripcion |
|---|---|---|
| id | Long | Identificador unico |
| name | String | Nombre |
| address | String | Direccion |
| city | String | Ciudad |
| country | String | Pais |
| stars | Integer | Estrellas (1-5) |
| capacity | Integer | Capacidad total |
| availablePlaces | Integer | Plazas disponibles |
| halfBoardPrice | Double | Precio media pension |
| fullBoardPrice | Double | Precio pension completa |
| imageUrl | String | URL imagen (Cloudinary) |
| active | Boolean | Soft delete |

### Bus
| Campo | Tipo | Descripcion |
|---|---|---|
| id | Long | Identificador unico |
| licensePlate | String | Matricula (unica, formato 0000-AAA) |
| capacity | Integer | Plazas totales |
| bath | Boolean | Servicio |
| wifi | Boolean | Servicio |
| AC | Boolean | Servicio |
| USB | Boolean | Servicio |
| active | Boolean | Soft delete |

### Driver
| Campo | Tipo | Descripcion |
|---|---|---|
| id | Long | Identificador unico |
| name | String | Nombre |
| phone | String | Telefono |
| licenceActive | Boolean | Licencia en vigor |
| imageUrl | String | URL imagen (Cloudinary) |
| active | Boolean | Soft delete |

### Travel
| Campo | Tipo | Descripcion |
|---|---|---|
| id | Long | Identificador unico |
| destiny | String | Destino |
| startDate | LocalDate | Fecha inicio |
| endDate | LocalDate | Fecha fin |
| sale | Boolean | En oferta |
| availablePlaces | Integer | Plazas disponibles |
| active | Boolean | Soft delete |
| hotel | Hotel (FK) | Hotel asociado |
| offer | Offer (FK) | Oferta aplicada |

### Offer
| Campo | Tipo | Descripcion |
|---|---|---|
| offerId | Long | Identificador unico |
| discountPercentage | Double | Porcentaje descuento |
| startDate | LocalDate | Inicio oferta |
| endDate | LocalDate | Fin oferta |

### Booking
| Campo | Tipo | Descripcion |
|---|---|---|
| bookingId | Long | Identificador unico |
| boughtDate | LocalDateTime | Fecha compra |
| typeBoard | Enum | HALF / FULL |
| isGroup | Boolean | Descuento grupo |
| totalPrice | Double | Precio total (calculado) |
| travel | Travel (FK) | Viaje reservado |
| employee | Employee (FK) | Empleado que gestiona |
| customers | List\<User\> | Pasajeros (N:M) |

### TripSegment
| Campo | Tipo | Descripcion |
|---|---|---|
| id | Long | Identificador unico |
| origin | String | Origen |
| destination | String | Destino |
| startTime | LocalDateTime | Salida |
| endTime | LocalDateTime | Llegada |
| travel | Travel (FK) | Viaje asociado |
| bus | Bus (FK) | Autobus asignado |
| driver | Driver (FK) | Conductor asignado |

### Enums
| Enum | Valores |
|---|---|
| Gender | MALE, FEMALE, NON_BINARY |
| Role | VIEWER, EDITOR, ADMIN |
| TypeBoard | HALF, FULL |

---

## Endpoints de la API

### Autenticacion - `/api/authentication`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/authentication/login` | Iniciar sesion (devuelve JWT) |

### Usuarios - `/api/users`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/users` | Listar todos (paginado) |
| GET | `/api/users/{id}` | Obtener por ID |
| GET | `/api/users/active` | Listar solo activos |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar |
| DELETE | `/api/users/{id}` | Eliminar (soft delete) |

### Empleados - `/api/employees`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/employees` | Listar todos (paginado) |
| GET | `/api/employees/{id}` | Obtener por ID |
| POST | `/api/employees` | Crear empleado |
| PUT | `/api/employees/{id}` | Actualizar |
| DELETE | `/api/employees/{id}` | Eliminar |

### Hoteles - `/api/hotels`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/hotels` | Listar todos (paginado) |
| GET | `/api/hotels/{id}` | Obtener por ID |
| GET | `/api/hotels/active` | Listar solo activos |
| GET | `/api/hotels/available` | Con plazas disponibles |
| POST | `/api/hotels` | Crear hotel |
| PUT | `/api/hotels/{id}` | Actualizar |
| DELETE | `/api/hotels/{id}` | Eliminar (soft delete) |

### Autobuses - `/api/buses`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/buses` | Listar todos (paginado) |
| GET | `/api/buses/{id}` | Obtener por ID |
| POST | `/api/buses` | Crear autobus |
| PUT | `/api/buses/{id}` | Actualizar |
| DELETE | `/api/buses/{id}` | Eliminar (soft delete) |

### Conductores - `/api/drivers`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/drivers` | Listar todos (paginado) |
| GET | `/api/drivers/{id}` | Obtener por ID |
| GET | `/api/drivers/active` | Listar solo activos |
| POST | `/api/drivers` | Crear conductor |
| PUT | `/api/drivers/{id}` | Actualizar |
| DELETE | `/api/drivers/{id}` | Eliminar (soft delete) |

### Viajes - `/api/travels`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/travels` | Listar todos los activos (paginado) |
| GET | `/api/travels/{id}` | Obtener por ID |
| GET | `/api/travels/available` | Viajes futuros con plazas |
| GET | `/api/travels/sale` | Viajes en oferta |
| POST | `/api/travels` | Crear viaje |
| PUT | `/api/travels/{id}` | Actualizar |
| DELETE | `/api/travels/{id}` | Eliminar (soft delete) |

### Ofertas - `/api/offers`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/offers` | Listar todas (paginado) |
| GET | `/api/offers/{id}` | Obtener por ID |
| POST | `/api/offers` | Crear oferta |
| PUT | `/api/offers/{id}` | Actualizar |
| DELETE | `/api/offers/{id}` | Eliminar |

### Reservas - `/api/bookings`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/bookings` | Listar todas (paginado) |
| GET | `/api/bookings/{id}` | Obtener por ID |
| POST | `/api/bookings` | Crear reserva |
| POST | `/api/bookings/quote` | Cotizar precio sin reservar |
| PUT | `/api/bookings/{id}` | Actualizar reserva |
| DELETE | `/api/bookings/{id}` | Cancelar reserva |

### Trayectos - `/api/trip-segments`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/trip-segments` | Listar todos (paginado) |
| GET | `/api/trip-segments/{id}` | Obtener por ID |
| POST | `/api/trip-segments` | Crear trayecto |
| PUT | `/api/trip-segments/{id}` | Actualizar |
| DELETE | `/api/trip-segments/{id}` | Eliminar |

### Dashboard - `/api/dashboard`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/dashboard` | Indicadores para la direccion |

### Imagenes - `/api/images`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/images/upload` | Subir imagen a Cloudinary |
| DELETE | `/api/images/delete/{publicId}` | Eliminar imagen de Cloudinary |

> **Nota:** Todos los endpoints GET que devuelven listas aceptan parametros de paginacion: `?page=0&size=10&sort=campo,asc`

---

## Reglas de negocio

- Un menor (< 18) no puede viajar sin tutor adulto
- No se puede vender un viaje si el autobus esta completo
- No se puede vender un viaje si el hotel esta completo
- No se pueden vender viajes pasados
- Un conductor no puede conducir 2 autobuses en el mismo horario
- **Tarifas:** niño (<= 17, 15% descuento), adulto (precio completo), pensionista (>= 65, 10% descuento)
- **Descuento por grupo:** 5% si >= 10 pasajeros y `isGroup == true`
- **Descuento por oferta:** porcentaje configurable si el viaje esta en oferta
- **Confirmacion por email:** se envia un email detallado tras cada reserva con lista de pasajeros, precios y descuentos aplicados

---

## Funcionalidades destacadas

| Funcionalidad | Descripcion |
|---|---|
| **Motor de precios** | Calcula precios con descuentos por edad (niño/pensionista), grupo y ofertas |
| **Email transaccional** | Envio asincrono de confirmacion con template Thymeleaf |
| **Dashboard directivo** | Viajes por año, ganancias del año actual, top 3 viajes por facturacion |
| **Cloudinary** | Subida y eliminacion de imagenes para hoteles y conductores |
| **Paginacion** | Todos los listados soportan paginacion y ordenacion via `Pageable` |
| **Roles JWT** | VIEWER (solo lectura), EDITOR (sin empleados), ADMIN (todo) |

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

| Tipo | Tests |
|---|---|
| Servicio | 10 archivos (~103 tests) |
| Controlador | 11 archivos (~59 tests) |
| Repositorio | 8 archivos (~56 tests) |
| Contexto | 1 archivo (1 test) |

Los tests de repositorio usan H2 en memoria (`@DataJpaTest` + `@ActiveProfiles("test")`).

---

## Ramas del repositorio

| Rama | Descripcion |
|---|---|
| `main` | Codigo en produccion |
| `develop` | Integracion de features |
| `feature/*` | Features en desarrollo |
| `fix/*` | Correcciones |
| `test/*` | Testing |

---

## Equipo

Proyecto desarrollado por el **Grupo 1** de **Factoria F5** - Proyecto Final 2026.

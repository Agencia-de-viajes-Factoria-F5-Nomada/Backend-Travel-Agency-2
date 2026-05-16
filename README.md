# Backend - Agencia de Viajes

API REST desarrollada con **Spring Boot 4.0.6** para la gestión de una agencia de viajes. Permite administrar usuarios, hoteles, autobuses, conductores, empleados, ofertas, viajes y reservas, con lógica de negocio avanzada: tarifas por edad, descuentos de grupo, validación de disponibilidad y control de solapamiento de conductores.

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
| JUnit 5 + Mockito | Tests |

---

## Requisitos previos

- Java 25 instalado
- MySQL 8+ en ejecucion
- Maven instalado (o usar `mvnw.cmd`)
- Cuenta en Cloudinary (subida de imagenes)
- Variable de entorno `JWT_SECRET` con una clave de minimo 32 caracteres

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
├── config/                              # CloudinaryConfig, CorsConfig
├── controller/                          # 11 controladores REST
│   ├── AuthenticationController.java    # POST /api/authentication/login
│   ├── BookingController.java           # /api/bookings
│   ├── BusController.java               # /api/buses
│   ├── CloudinaryController.java        # /api/images
│   ├── DriverController.java            # /api/drivers
│   ├── EmployeeController.java          # /api/employees
│   ├── HotelController.java             # /api/hotels
│   ├── OfferController.java             # /api/offers
│   ├── TravelController.java            # /api/travels
│   ├── TripSegmentController.java       # /api/trip-segments
│   └── UserController.java              # /api/users
├── dto/                                 # 24 DTOs de request/response
├── exception/                           # 9 excepciones + GlobalExceptionHandler
├── mapper/                              # 8 mappers
├── model/                               # 9 entidades + 3 enums
├── repository/                          # 9 repositorios JPA
├── security/                            # JwtUtil, JwtFilter, SecurityConfig
└── service/                             # 12 servicios
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
| licensePlate | String | Matricula (unica) |
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
| imageUrl | String | URL imagen |
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
| totalPrice | Double | Precio total |
| travel | Travel (FK) | Viaje reservado |
| employee | Employee (FK) | Empleado que gestiona |
| customers | List<User> | Pasajeros (N:M) |

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
| POST | `/api/users` | Crear usuario |
| GET | `/api/users` | Listar todos |
| GET | `/api/users/{id}` | Obtener por ID |
| GET | `/api/users/activos` | Listar activos |
| PUT | `/api/users/{id}` | Actualizar |
| DELETE | `/api/users/{id}` | Eliminar |

### Empleados - `/api/employees`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/employees` | Crear empleado |
| GET | `/api/employees` | Listar todos |
| GET | `/api/employees/{id}` | Obtener por ID |
| PUT | `/api/employees/{id}` | Actualizar |
| DELETE | `/api/employees/{id}` | Eliminar |

### Hoteles - `/api/hotels`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/hotels` | Crear hotel |
| GET | `/api/hotels` | Listar todos |
| GET | `/api/hotels/{id}` | Obtener por ID |
| GET | `/api/hotels/activos` | Listar activos |
| GET | `/api/hotels/disponibles` | Con plazas disponibles |
| PUT | `/api/hotels/{id}` | Actualizar |
| DELETE | `/api/hotels/{id}` | Eliminar |

### Autobuses - `/api/buses`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/buses` | Crear autobus |
| GET | `/api/buses` | Listar todos |
| GET | `/api/buses/{id}` | Obtener por ID |
| PUT | `/api/buses/{id}` | Actualizar |
| DELETE | `/api/buses/{id}` | Eliminar |

### Conductores - `/api/drivers`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/drivers` | Crear conductor |
| GET | `/api/drivers` | Listar todos |
| GET | `/api/drivers/{id}` | Obtener por ID |
| GET | `/api/drivers/activos` | Listar activos |
| PUT | `/api/drivers/{id}` | Actualizar |
| DELETE | `/api/drivers/{id}` | Eliminar |

### Viajes - `/api/travels`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/travels` | Crear viaje |
| GET | `/api/travels` | Listar todos |
| GET | `/api/travels/{id}` | Obtener por ID |
| GET | `/api/travels/available` | Viajes futuros con plazas |
| GET | `/api/travels/sale` | Viajes en oferta |
| PUT | `/api/travels/{id}` | Actualizar |
| DELETE | `/api/travels/{id}` | Eliminar |

### Ofertas - `/api/offers`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/offers` | Crear oferta |
| GET | `/api/offers` | Listar todas |
| GET | `/api/offers/{id}` | Obtener por ID |
| PUT | `/api/offers/{id}` | Actualizar |
| DELETE | `/api/offers/{id}` | Eliminar |

### Reservas - `/api/bookings`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/bookings` | Crear reserva |
| GET | `/api/bookings` | Listar todas |
| GET | `/api/bookings/{id}` | Obtener por ID |
| POST | `/api/bookings/quote` | Cotizar precio |
| PUT | `/api/bookings/{id}` | Actualizar |
| DELETE | `/api/bookings/{id}` | Cancelar |

### Trayectos - `/api/trip-segments`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/trip-segments` | Crear trayecto |
| GET | `/api/trip-segments` | Listar todos |
| GET | `/api/trip-segments/{id}` | Obtener por ID |
| PUT | `/api/trip-segments/{id}` | Actualizar |
| DELETE | `/api/trip-segments/{id}` | Eliminar |

### Imagenes - `/api/images`
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/images/upload` | Subir imagen a Cloudinary |
| DELETE | `/api/images/delete/{publicId}` | Eliminar imagen |

---

## Reglas de negocio

- Un menor (< 18) no puede viajar sin tutor adulto
- No se puede vender un viaje si el autobus esta completo
- No se puede vender un viaje si el hotel esta completo
- No se pueden vender viajes pasados
- Un conductor no puede conducir 2 autobuses en el mismo horario
- Tarifas: nino (<= 17, 15% descuento), adulto (precio completo), pensionista (>= 65, 10% descuento)
- Descuento por grupo: 5% si >= 10 pasajeros y `isGroup == true`
- Descuento por oferta: porcentaje configurable si el viaje esta en oferta

---

## Testing

```bash
./mvnw.cmd clean test
```

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

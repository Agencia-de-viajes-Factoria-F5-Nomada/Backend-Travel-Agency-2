# Auditoría de Código — Backend-Travel-Agency-2

**Fecha:** 15 de mayo de 2026
**Proyecto:** Backend-Travel-Agency-2 (Spring Boot + Maven + MySQL)

---

## 1. Estructura del proyecto

```
Backend-Travel-Agency-2/
├── pom.xml
├── README.md
├── .gitignore
├── mvnw / mvnw.cmd
├── src/
│   ├── main/java/com/inditex/g1_agencia_viajes/
│   │   ├── G1AgenciaViajesApplication.java
│   │   ├── config/ (CloudinaryConfig.java, CorsConfig.java)
│   │   ├── security/ (SecurityConfig.java, JwtUtil.java, JwtFilter.java)
│   │   ├── controller/ (11 controllers)
│   │   ├── dto/ (22 DTOs)
│   │   ├── exception/ (8 excepciones + GlobalExceptionHandler)
│   │   ├── mapper/ (8 mappers)
│   │   ├── model/ (12 modelos/entidades/enums)
│   │   ├── repository/ (9 repos)
│   │   └── service/ (12 servicios)
│   ├── main/resources/ (application.properties, data.sql)
│   └── test/java/.../service/ (10 tests)
```

---

## 2. Escala de valoración

- **🔴 Crítico:** Impide el funcionamiento o es un riesgo de seguridad grave. Arreglar inmediatamente.
- **🟡 Alto:** Impacta negativamente en calidad, mantenibilidad o funcionalidad. Arreglar pronto.
- **🟠 Medio:** Incumple buenas prácticas o convenios del proyecto. Mejorar cuando se pueda.
- **🟢 Punto fuerte:** Aspecto positivo del proyecto.

---

## 3. Criterios de evaluación

### 3.1 Organización por dominio — 🟠 3/10

**Problema:** El proyecto está organizado por capas técnicas (controller/, service/, model/, dto/), no por dominio de negocio. Aunque es aceptable para el tamaño actual, no sigue la recomendación de estructura por dominio.

**Recomendación:** Para proyectos pequeños como este es aceptable, pero si crece, refactorizar a paquetes como `user/`, `hotel/`, `booking/`, etc.

---

### 3.2 Uso de DTOs — 🟡 6/10

**🔴 EmployeeController expone la entidad directamente** (`EmployeeController.java:36,41`)
```java
public ResponseEntity<Employee> create(@RequestBody Employee employee) { ... }
public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) { ... }
```
Acepta `Employee` directamente sin DTO. Esto:
- Bypassa validaciones (`@Valid` no está presente)
- Expone el modelo interno (incluyendo `password`, aunque es `WRITE_ONLY`)
- No separa la representación interna de la externa

**Crear `EmployeeRequestDTO` y `EmployeeResponseDTO`** y usarlos en estos métodos.

**🟠 Anotaciones redundantes en `UserResponseDTO.java:7-9`**
```java
@Data
@Getter
@Setter
```
`@Data` ya incluye `@Getter`, `@Setter`, `@ToString`, `@EqualsAndHashCode` y `@RequiredArgsConstructor`. Las anotaciones extra son redundantes.

**🟠 Campos duplicados `userId`/`id` en `UserResponseDTO.java:11,14`**
```java
private Long userId;
private Long id;
```
Ambos parecen representar lo mismo. `userId` solo lo usa `BookingUserMapper`. Unificar.

---

### 3.3 Manejo global de excepciones — 🟡 6/10

**🟡 Falta un handler genérico para `Exception`** (`GlobalExceptionHandler.java`)
Actualmente maneja:
- `MethodArgumentNotValidException` → 400
- `ResourceNotFoundException` → 404
- `HotelNotAvailableException`, `TravelNotAvailableException`, `EmailAlreadyExistsException`, `DuplicateLicensePlateException`, `DriverOverlapException` → 409
- `IllegalArgumentException`, `MinorWithoutTutorException` → 400

**No maneja:**
- `Exception` (catch-all) — cualquier excepción no contemplada devuelve stack trace 500 al cliente
- `HttpMessageNotReadableException` — JSON malformado
- `MethodArgumentTypeMismatchException` — tipo de variable de ruta incorrecto
- `ConstraintViolationException` — validaciones JPA
- `DataIntegrityViolationException` — violaciones de constraint en BD
- `MissingServletRequestPartException` — subida de archivos
- `AccessDeniedException` — seguridad

**Añadir un handler para `Exception` que devuelva un 500 genérico sin trazas internas.**

---

### 3.4 Validaciones — 🟡 7/10

**🔴 Falta `@Valid` en `AuthenticationController.java:27`**
```java
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request)
```
Sin `@Valid`, las validaciones de `LoginRequest` nunca se ejecutan.

**🔴 Falta `@Valid` en `EmployeeController.java:36,41`**
Los métodos `create()` y `update()` de EmployeeController no tienen `@Valid`.

**🟡 Falta `@Max(5)` en campo `stars`** (`Hotel.java:29`, `HotelRequestDTO.java:23`)
```java
@Min(value = 1)
private int stars;
```
Sin `@Max(5)` se puede crear un hotel de 100 estrellas.

**🟡 Falta `@NotEmpty` en `BookingRequestDTO.java:25`**
```java
private List<Long> customerIds;
```
Sin validación de tamaño mínimo, se podría crear una reserva sin clientes.

**🟡 Validaciones de formato ausentes:**
- `BusRequestDTO.java:12` — `licensePlate` sin patrón (ej. `^[0-9]{4}[A-Z]{3}$`)
- `DriverRequestDTO.java:13` — número de teléfono sin `@Pattern`
- `UserRequestDTO.java:25` — `passport` sin validación de formato

---

### 3.5 Cloudinary — 🟢 9/10

**Bien implementado:**
- `CloudinaryConfig.java` — credenciales externalizadas vía `@Value` desde `application.properties`
- `CloudinaryService.java` — `uploadImage()` y `deleteImage()` funcionales
- `CloudinaryController.java` — endpoints `/api/images/upload` (POST) y `/api/images/delete/{publicId}` (DELETE)
- Anotaciones Swagger (`@Tag`, `@Operation`) presentes en este controller

**Único pero:** No hay manejo de errores específico para cuando Cloudinary no responde o la imagen es demasiado grande.

---

### 3.6 Tests — 🟡 7/10

**10 archivos de test (~2100 líneas):**

| Test | Líneas | Cobertura |
|------|--------|-----------|
| `BookingPricingServiceTest` | 297 | Quote con child/pensioner/group/offer discounts + errores |
| `BookingServiceTest` | 368 | CRUD, addCustomer, quote, minor validation, capacity |
| `BusServiceImplTest` | 146 | CRUD, duplicate license, errores |
| `DriverServiceTest` | 160 | CRUD, errores |
| `EmployeeServiceTest` | 96 | Save con BCrypt, getById, delete |
| `HotelServiceTest` | 240 | CRUD, reducirPlazas/liberarPlazas, errores |
| `OfferServiceTest` | 133 | CRUD, errores |
| `TravelServiceTest` | 252 | CRUD, date validation, available/onSale queries |
| `TripSegmentServiceTest` | 180 | CRUD, errores |
| `UserServiceTest` | 229 | CRUD, getActive, update, delete, errores |

**🔴 Tests con assertions rotas:**
- `TravelServiceTest.java:142-148, 226-231, 244-250` — esperan `"Viaje no encontrado"` pero el mensaje real es `"No hemos podido encontrar la información de el viaje, con el id: 99"`. **FALLAN.**
- `OfferServiceTest.java:114` — espera `"Oferta no encontrada"` pero el mensaje real incluye el ID. **FALLA.**

**🟡 Tests faltantes:**
- Controller tests (`@WebMvcTest`)
- Repository tests (`@DataJpaTest`)
- Tests de seguridad/autenticación
- Tests de CloudinaryService

---

### 3.7 Swagger — 🟠 3/10

- Dependencia `springdoc-openapi-starter-webmvc-ui` presente en `pom.xml:66-68`
- Configurado en `application.properties` líneas 8-11
- **Solo `CloudinaryController.java`** usa `@Tag` y `@Operation`
- El resto de controllers (10) no tienen anotaciones Swagger/OpenAPI

**Añadir `@Operation`, `@ApiResponses` y `@Schema` a todos los endpoints.**

---

### 3.8 Convenciones REST — 🟠 7/10

**Bien:**
- Sustantivos en plural: `/api/users`, `/api/hotels`, `/api/buses`
- Métodos HTTP correctos (GET/POST/PUT/DELETE)
- Códigos de estado HTTP apropiados (201, 200, 204)

**Inconsistencias:**
- Mezcla de idiomas: `/activos`, `/disponibles` (español) vs `/available`, `/sale`, `/quote` (inglés)
- Nombres de métodos en controllers inconsistentes: `getAllBookings` vs `getAll`
- Sin versionado de API (`/api/v1/`)
- `/api/images/upload` — estándar sería `POST /api/images`

---

### 3.9 Reglas de negocio — 🟢 8/10

| Regla | Estado | Detalle |
|-------|--------|---------|
| No se pueden vender viajes pasados | 🟡 Parcial | `TravelService.getAvailable()` filtra por fecha futura, pero `BookingService.save()` no comprueba si el viaje ya ha pasado |
| No vender si bus/hotel completo | 🟢 OK | `BookingService.save()` checks `availablePlaces` y llama a `hotelService.reducirPlazas()` |
| Menor acompañado de adulto | 🟢 OK | `BookingService.validateMinorHasTutor()` chequea edad < 18 y `tutorId != null` |
| Conductor no puede conducir 2 buses | 🟢 OK | `TripSegmentService` consulta `findOverlappingByDriver()` |
| Tarifa niño/adulto/pensionista | 🟢 OK | `BookingPricingService` con descuentos 15% niño, 10% pensionista |
| Descuento por grupo | 🟢 OK | 5% si >= 10 pasajeros y `isGroup == true` |

---

### 3.10 Seguridad — 🔴 2/10

**🔴 CRÍTICO: JWT secret hardcodeado** (`JwtUtil.java:11`, `JwtFilter.java:53`)
```java
private static final String SECRET_KEY = "your_secret_password";
Algorithm.HMAC256("your_secret_password");
```
Cualquiera con acceso al código puede forjar tokens. Mover a `application.properties` y usar `@Value`.

**🔴 CRÍTICO: JwtFilter no protege ningún endpoint** (`JwtFilter.java:28-38`)
```java
if (path.startsWith("/api/users") || path.startsWith("/api/hotels") || ...) {
    chain.doFilter(request, response);
    return;
}
```
TODAS las rutas `/api/...` están whitelisted. El filter llama a `chain.doFilter()` sin verificar token para **cada endpoint de la API**. La autenticación JWT nunca se ejecuta.

**🔴 CRÍTICO: Contraseñas en texto plano en seed data** (`data.sql:18-21`)
```sql
INSERT INTO employees (...) VALUES (1, 'Admin', 'admin@email.com', '123456', 'ADMIN', ...);
```
`EmployeeService` hashea con BCrypt pero `data.sql` bypassa el servicio. El login por BCrypt fallará para estos usuarios seed.

**🟡 EmployeeController expone entidad** — acepta `Employee` directamente, `password` aunque es `WRITE_ONLY` podría exponerse.

**🟡 Inyección por campo** (`AuthenticationController.java:20-24`, `EmployeeService.java:15-16`) — usar constructor injection.

**🟡 Falta `@Transactional` en `EmployeeService.deleteEmployee()`**

---

## 4. Problemas adicionales

| # | Problema | Archivo | Detalle |
|---|----------|---------|---------|
| 1 | Queries ineficientes | `TravelService.java:43-57` | `getAvailable()` y `getOnSale()` hacen `findAll()` y filtran en memoria. El repo tiene queries nativas sin usar |
| 2 | Sin paginación | Todos los listados | Devuelven todos los registros sin paginar |
| 3 | Sin BusMapper | `BusServiceImpl.java:71-96` | Define `toResponseDTO()` y `toEntity()` internos; los demás domains tienen mapper dedicado |
| 4 | Typos en mensajes de error | Varios | "l bus", "l hotel", "l cliente", "l viaje", "l empleado", "l tutor", "l trayecto" — falta "e" de "el" |
| 5 | Operador `+` sobrante | `BookingPricingService.java:41` | `"l viaje", + request.getTravelId()` — el unario `+` no hace nada |
| 6 | `@Repository` ausente | `DriverRepository.java:8` | Falta la anotación |
| 7 | data.sql con IDs fijos | `data.sql` | Asume que la secuencia de BD empieza en 1 |
| 8 | update() no reconcilia capacidad | `BookingService.java:88-114` | Al cambiar de viaje/hotel no restaura plazas del anterior |

---

## 5. Resumen por archivo

| Archivo | Líneas | Estado |
|---------|--------|--------|
| `application.properties` | 21 | OK, placeholders para credenciales |
| `pom.xml` | 137 | Spring Boot 4.0.6, Java 25, todas las deps |
| `data.sql` | 87 | 🔴 Passwords texto plano |
| `JwtUtil.java` | 27 | 🔴 Secret hardcodeado |
| `JwtFilter.java` | 79 | 🔴 No autentica |
| `GlobalExceptionHandler.java` | 88 | 🟡 Falta catch-all |
| `EmployeeController.java` | 54 | 🟡 Sin DTO, sin @Valid |
| `AuthenticationController.java` | 37 | 🟡 Sin @Valid, field injection |
| `Hotel.java` / `HotelRequestDTO.java` | — | 🟡 Falta @Max(5) |
| `BusRequestDTO.java` | 23 | 🟡 Sin pattern en licensePlate |
| `DriverRepository.java` | 8 | 🟠 Sin @Repository |
| `UserResponseDTO.java` | 25 | 🟠 Anotaciones redundantes |
| `CloudinaryController.java` | 41 | 🟢 Swagger presente |
| `CloudinaryService.java` | 39 | 🟢 Bien implementado |
| Tests (10) | ~2100 | 🟢 Buenos en general, 🔴 2 assertions rotas |

---

## 6. Prioridad de acciones

### 🔴 Inmediato (día 1)
1. Mover JWT secret a `application.properties`
2. Arreglar JwtFilter para que autentique realmente
3. Hashear passwords en seed data o usar inicializador Java
4. Arreglar assertions rotas en tests

### 🟡 Corto plazo (día 2-3)
5. Crear Employee DTOs
6. Añadir `@Valid` donde falta
7. Añadir handler genérico de Exception
8. Añadir `@Max(5)` a stars
9. Arreglar `BookingService.update()` para reconciliar capacidad
10. Usar repository queries en TravelService

### 🟠 Medio plazo (sprint)
11. Añadir Swagger a todos los controllers
12. Crear BusMapper
13. Arreglar typos en mensajes de error
14. Migrar a constructor injection
15. Añadir paginación

# Auditoría de Código — Backend-Travel-Agency-2

**Fecha:** 16 de mayo de 2026 (actualización: análisis TravelService + estado real)
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

**✅ `@Valid` en `AuthenticationController.java:27`** — Corregido desde la auditoría anterior. Ya tiene `@Valid`.

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

**22 archivos de test (11 service + 10 controller + 1 app):**

| Test | Líneas | Cobertura |
|------|--------|-----------|
| `BookingPricingServiceTest` | 297 | Quote con child/pensioner/group/offer discounts + errores |
| `BookingServiceTest` | 386 | CRUD, addCustomer, quote, minor validation, capacity |
| `BusServiceImplTest` | 146 | CRUD, duplicate license, errores |
| `DriverServiceTest` | 160 | CRUD, errores |
| `EmployeeServiceTest` | 118 | Save con BCrypt, getById, delete |
| `HotelServiceTest` | 240 | CRUD, reducirPlazas/liberarPlazas, errores |
| `OfferServiceTest` | 133 | CRUD, errores |
| `TravelServiceTest` | 253 | CRUD, date validation, available/onSale queries |
| `TripSegmentServiceTest` | 180 | CRUD, overlapping driver (sin test real) |
| `UserServiceTest` | 229 | CRUD, getActive, update, delete, errores |
| `*ControllerTest` (10) | ~134-164 c/u | Controller tests con `MockMvc` + `GlobalExceptionHandler` |

**✅ Assertions de mensajes corregidos** — Tras commit `7b112c1`, las excepciones usan `ResourceNotFoundException` con mensajes consistentes. Los tests usan `hasMessageContaining()` con subcadenas válidas.

**🟢 Controller tests presentes** — 10 tests con `MockMvcBuilders.standaloneSetup`. No usan `@WebMvcTest` pero cubren los endpoints.

**⚠️ TravelServiceTest.getById** (`TravelServiceTest.java:147`)
Usa `RuntimeException.class` genérico → debería usar `ResourceNotFoundException.class`.

**🔴 TripSegmentServiceTest — mock faltante** (`TripSegmentServiceTest.java`)
`tripSegmentRepository.findOverlappingByDriver()` no mockeado en `create()`/`update()`. La lógica de solapamiento NO se testea.

**🔴 G1AgenciaViajesApplicationTests sin test DB** (`G1AgenciaViajesApplicationTests.java`)
`@SpringBootTest` sin H2 ni perfil test. Intenta conectar a MySQL real.

**🟡 Tests faltantes:**
- Repository tests (`@DataJpaTest`)
- Tests de CloudinaryService
- Tests de JwtFilter / SecurityConfig

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

### 3.9 Reglas de negocio — 🟡 6/10

| Regla | Estado | Detalle |
|-------|--------|---------|
| No se pueden vender viajes pasados | 🟡 Parcial | `TravelService.getAvailable()` filtra por fecha futura, pero `BookingService.save()` no comprueba si el viaje ya ha pasado. Además `getAll()` ahora NO filtra por activos — devuelve viajes pasados e inactivos. |
| No vender si bus/hotel completo | 🟢 OK | `BookingService.save()` checks `availablePlaces` y llama a `hotelService.reducirPlazas()` |
| Menor acompañado de adulto | 🟢 OK | `BookingService.validateMinorHasTutor()` chequea edad < 18 y `tutorId != null` |
| Conductor no puede conducir 2 buses | 🟢 OK | `TripSegmentService` consulta `findOverlappingByDriver()` |
| Tarifa niño/adulto/pensionista | 🟢 OK | `BookingPricingService` con descuentos 15% niño, 10% pensionista |
| Descuento por grupo | 🟢 OK | 5% si >= 10 pasajeros y `isGroup == true` |

**🟡 REGRESIÓN: `getAll()` dejó de filtrar por activos** (`TravelService.java:35-41`)
Commit `69fc944` reemplazó `findByActiveTrue()` por `findAll()`. Ahora devuelve viajes inactivos y pasados. En la versión inicial (commit `929a41d`) sí se filtraba correctamente.

**🟡 REGRESIÓN: `getOnSale()` perdió filtro de fecha** (`TravelService.java:57-66`)
Commit `69fc944` reemplazó `findBySaleTrueAndActiveTrueAndStartDateAfter(LocalDate.now())` por `findAll()` + `filter(sale == true)`. Ahora muestra ofertas de viajes ya pasados.

**🟡 `Collections.shuffle()` en listados** (`TravelService.java:37,50,62`)
Añadido en commit `7651cef`. Baraja aleatoriamente los resultados de `getAll()`, `getAvailable()` y `getOnSale()`. Esto:
- Hace impredecible el orden de respuesta (cada petición devuelve orden distinto)
- Inhabilita cualquier intento futuro de paginación
- Es un efecto secundario sobre la lista original (no una copia)

---

### 3.10 Seguridad — 🟡 5/10 (mejorado desde auditoría anterior)

**Problemas corregidos desde la auditoría del 15/05:**
- ✅ **JWT secret**: Ahora se inyecta vía `@Value("${jwt.secret}")` desde `application.properties`. Ya no está hardcodeado en código.
- ✅ **JwtFilter autentica**: Ya no whitelistea todas las rutas. Solo permite `/api/authentication/login`, `/api-docs`, `/swagger-ui`. Verifica token Bearer y aplica roles (VIEWER=read-only, EDITOR=no employees).
- ✅ **Passwords en seed**: Ahora son hashes BCrypt válidos en `data.sql`.
- ✅ **Inyección por campo**: No existe `@Autowired` en campos en todo el código. Todo es constructor injection.

**🔴 CRÍTICO: JWT secret con fallback hardcodeado** (`application.properties:21`)
```properties
jwt.secret=${JWT_SECRET:default_secret_change_me}
```
Si la variable de entorno `JWT_SECRET` no está definida, se usa `default_secret_change_me`. Cualquiera puede forjar tokens. **Quitar el `:default_secret_change_me`**.

**🟡 Posible mismatch en ruta Swagger del filtro** (`JwtFilter.java:33`)
```java
path.startsWith("/swagger-ui")
```
Comprueba `/swagger-ui` pero la ruta configurada en `application.properties` es `/swagger-ui.html`. Las rutas reales de Swagger son `/swagger-ui.html` y `/swagger-ui/*`. Podría no matchear.

**🟡 EmployeeController expone entidad** — acepta `Employee` directamente, sin `@Valid`. `password` tiene `WRITE_ONLY` pero es mejor usar DTO.

**🟡 Falta `@Transactional` en `EmployeeService`** — todos sus métodos carecen de `@Transactional`.

**🟠 Naming español en JwtUtil** (`JwtUtil.java`)
```java
private final Algorithm algoritmo;       // → algorithm
public String crearToken(...)             // → createToken
public Algorithm getAlgoritmo()           // → getAlgorithm
```
Violación de AGENTS.md sección 2 (nombres en inglés).

---

## 4. Problemas adicionales

| # | Problema | Archivo | Detalle |
|---|----------|---------|---------|
| 1 | Queries ineficientes + regresión | `TravelService.java:35-66` | `getAll()`, `getAvailable()` y `getOnSale()` usan `findAll()` + filtro en memoria. El repositorio tiene 3 queries derivadas útiles (`findByActiveTrue()`, `findByActiveTrueAndStartDateAfter()`, `findBySaleTrueAndActiveTrueAndStartDateAfter()`) que **no se usan**. La versión inicial (commit `929a41d`) sí las usaba. |
| 2 | `Collections.shuffle()` en listados | `TravelService.java:37,50,62` | Añadido en commit `7651cef`. Baraja resultados aleatoriamente. Hace impredecible el orden, impide paginación. |
| 3 | `getAll()` sin filtrar activos | `TravelService.java:35-41` | Regresión desde commit `69fc944`. Antes usaba `findByActiveTrue()`. |
| 4 | `getOnSale()` sin filtrar por fecha | `TravelService.java:57-66` | Regresión desde commit `69fc944`. Antes usaba `findBySaleTrueAndActiveTrueAndStartDateAfter()`. |
| 5 | Sin paginación | Todos los listados | Devuelven todos los registros sin paginar |
| 6 | Sin BusMapper | `BusServiceImpl.java:71-96` | Define `toResponseDTO()` y `toEntity()` internos; los demás domains tienen mapper dedicado |
| 7 | Typos en mensajes de error | Varios | "l bus", "l hotel", "l cliente", "l viaje", "l empleado", "l tutor", "l trayecto" — falta "e" de "el" |
| 8 | Operador `+` sobrante | `BookingPricingService.java:41` | `"l viaje", + request.getTravelId()` — el unario `+` no hace nada |
| 9 | `@Repository` ausente | Varios repos | `BookingRepository`, `DriverRepository`, `EmployeeRepository`, `OfferRepository`, `TripSegmentRepository` |
| 10 | data.sql con IDs fijos | `data.sql` | Asume que la secuencia de BD empieza en 1 |
| 11 | update() no reconcilia capacidad | `BookingService.java:88-114` | Al cambiar de viaje/hotel no restaura plazas del anterior |
| 12 | Naming español en servicio | `HotelService.java` | `reducirPlazas()`, `liberarPlazas()` — violan AGENTS.md (deben ser inglés) |
| 13 | Hard delete vs soft delete | `UserService.java` | `deleteById()` (hard delete) mientras Hotel/Bus/Driver/Travel usan soft delete (`active=false`) |
| 14 | N+1 queries en bucle | `BookingService.java:233`, `BookingPricingService.java:66` | `findById()` en loop en lugar de `findAllById()` |

---

## 5. Resumen por archivo

| Archivo | Líneas | Estado |
|---------|--------|--------|
| `application.properties` | 21 | 🔴 Fallback JWT secret hardcodeado |
| `pom.xml` | 142 | Spring Boot 4.0.6, Java 25, todas las deps |
| `data.sql` | 87 | ✅ Passwords BCrypt (corregido) |
| `JwtUtil.java` | 30 | ✅ Secret vía @Value, 🟠 naming español |
| `JwtFilter.java` | 75 | ✅ Autentica, 🟡 posible swagger path mismatch |
| `GlobalExceptionHandler.java` | 104 | 🟡 Falta catch-all Exception |
| `EmployeeController.java` | 54 | 🔴 Sin DTO, sin @Valid |
| `AuthenticationController.java` | 37 | ✅ @Valid presente, constructor injection |
| `TravelService.java` | 113 | 🟡 Queries ineficientes (findAll+stream), shuffle(), regresiones |
| `HotelRequestDTO.java` | — | 🟡 Falta @Max(5) en stars |
| `BookingRequestDTO.java` | — | 🟡 Falta @NotEmpty en customerIds, @NotNull en employeeId |
| `BusRequestDTO.java` | 23 | 🟡 Sin pattern en licensePlate |
| `DriverRequestDTO.java` | — | 🟡 Sin @Pattern en phone |
| `UserResponseDTO.java` | 25 | 🟠 Anotaciones redundantes, userId/id duplicados |
| `CloudinaryController.java` | 41 | 🟢 Swagger presente |
| `CloudinaryService.java` | 39 | 🟢 Bien implementado |
| Tests (22) | ~3500 | ✅ Asserts corregidos, 🔴 mock faltante TripSegment, 🔴 sin DB test |

---

## 6. Prioridad de acciones

### 🔴 Inmediato (día 1)
1. Quitar fallback hardcodeado del JWT secret en `application.properties` (`:default_secret_change_me`)
2. Crear Employee DTOs + refactor EmployeeController (entity → DTOs, añadir `@Valid`)
3. Añadir handler genérico `@ExceptionHandler(Exception.class)` en `GlobalExceptionHandler`
4. Arreglar mock faltante `findOverlappingByDriver()` en `TripSegmentServiceTest`
5. Agregar H2/test DB para `G1AgenciaViajesApplicationTests`

### 🟡 Corto plazo (día 2-3)
6. Refactorizar `TravelService` para usar queries derivadas del repositorio (`findByActiveTrue()`, `findByActiveTrueAndStartDateAfter()`, `findBySaleTrueAndActiveTrueAndStartDateAfter()`)
7. Eliminar `Collections.shuffle()` de los listados (o moverlo a una capa de presentación si es requisito UI)
8. Arreglar `getAll()` para filtrar solo viajes activos
9. Arreglar `getOnSale()` para filtrar también por fecha futura
10. Añadir `@Max(5)` a stars en `HotelRequestDTO`
11. Añadir `@NotEmpty` en customerIds + `@NotNull` en employeeId de `BookingRequestDTO`
12. Añadir `@Transactional` a todos los métodos de `EmployeeService`
13. Arreglar `BookingService.update()` para reconciliar capacidad
14. Añadir `@Pattern` para teléfono en `DriverRequestDTO`

### 🟠 Medio plazo (sprint)
15. Añadir Swagger (`@Operation`, `@ApiResponses`) a todos los controllers
16. Crear BusMapper y eliminar lógica manual de `BusServiceImpl`
17. Arreglar N+1 queries (usar `findAllById()` en vez de loop `findById()`)
18. Arreglar typos "l viaje" → "el viaje" en todas las excepciones
19. Limpiar `UserResponseDTO` (redundancias, userId/id duplicados)
20. Renombrar métodos/nombres en español a inglés (`algoritmo`→`algorithm`, `reducirPlazas`→`reduceCapacity`, etc.)
21. Arreglar posible mismatch de ruta Swagger en JwtFilter
22. Unificar hard delete → soft delete en UserService
23. Añadir paginación a endpoints GET
24. Añadir `@Repository` en repositorios que faltan

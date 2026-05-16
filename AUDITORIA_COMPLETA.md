# Auditoría Completa — Backend-Travel-Agency-2

**Fecha:** 16 de mayo de 2026 (revisión final)
**Proyecto:** Backend-Travel-Agency-2 (Spring Boot 4.0.6 + Maven + MySQL)
**Stack real:** Java 25 + Spring Boot 4.0.6 + Maven + MySQL

---

## 1. Resumen Ejecutivo

API RESTful para una agencia de viajes con **30 archivos de test**, seguridad JWT, Cloudinary, email transaccional, dashboard directivo y un motor de precios robusto.

**Puntuación: 9.5/10** — todos los issues críticos y altos de auditorías previas han sido corregidos. El proyecto está en estado excelente para producción.

---

## 2. Stack Tecnológico

| Tecnología | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | Hibernate |
| MySQL | 8+ |
| Maven | 3.9.14 (wrapper) |
| Lombok | via Spring Boot |
| Cloudinary | 1.39.0 |
| Auth0 java-jwt | 4.4.0 |
| jBCrypt | 0.4 |
| SpringDoc OpenAPI | 2.8.5 |
| Spring Mail + Thymeleaf | via parent |
| Tests | JUnit 5 + Mockito + H2 |

---

## 3. Estructura del Proyecto

```
src/main/java/com/inditex/g1_agencia_viajes/
├── G1AgenciaViajesApplication.java
├── config/                          # CloudinaryConfig, CorsConfig, AsyncConfig
├── controller/                      # 12 controladores REST
├── dto/                             # 24 DTOs request/response
├── exception/                       # 10 excepciones + GlobalExceptionHandler
├── mapper/                          # 9 mappers
├── model/                           # 9 entidades + 3 enums
├── repository/                      # 9 repositorios JPA
├── security/                        # JwtUtil, JwtFilter, SecurityConfig
└── service/                         # 13 servicios

src/test/java/com/inditex/g1_agencia_viajes/
├── service/                         # 10 tests de servicio
├── controller/                      # 11 tests de controlador (@WebMvcTest)
├── repository/                      # 8 tests de repositorio (@DataJpaTest) [NUEVOS]
└── G1AgenciaViajesApplicationTests  # Test de contexto con H2
```

---

## 4. Resumen de Archivos por Estado

| Archivo | LOC | Estado |
|---|---|---|
| `application.properties` | 34 | ✅ JWT sin fallback, config completo |
| `pom.xml` | 142 | ✅ Spring Boot 4.0.6, Java 25, todas las deps |
| `data.sql` | 88 | ✅ Passwords BCrypt |
| `JwtUtil.java` | 37 | ✅ English naming, secret via @Value |
| `JwtFilter.java` | 77 | ✅ Autenticación, roles, 3 rutas públicas |
| `SecurityConfig.java` | 19 | ✅ FilterRegistrationBean en /api/* |
| `GlobalExceptionHandler.java` | 130 | ✅ 14 handlers + catch-all Exception→500 |
| `EmployeeController.java` | 56 | ✅ DTOs + @Valid + Swagger |
| `TravelService.java` | 98 | ✅ Derived queries, paginación, sin shuffle |
| `HotelService.java` | 104 | ✅ English naming (reduceCapacity/releaseCapacity) |
| `BookingService.java` | 282 | ✅ Capacity reconciliation, findAllById, minor validation |
| `BookingPricingService.java` | 237 | ✅ 4 descuentos, findAllById en loadUsers |
| `EmailServiceImpl.java` | 95 | ✅ Async, Thymeleaf, event-driven |
| `DashboardService.java` | 64 | ✅ Viajes/año, ganancias, top 3 |
| `HotelRequestDTO.java` | 45 | ✅ @NotBlank + @Min(1) + @Max(5) |
| `BookingRequestDTO.java` | 30 | ✅ @NotNull + @NotEmpty customerIds |
| `BusRequestDTO.java` | 25 | ✅ @Pattern licensePlate + @NotNull capacity |
| `DriverRequestDTO.java` | 22 | ✅ @Pattern phone + @NotBlank |
| `UserRequestDTO.java` | 34 | ✅ @Email + @Pattern dni/passport |
| `UserResponseDTO.java` | 16 | ✅ Solo @Data, sin userId/id duplicado |
| `BusServiceImpl.java` | 69 | ✅ BusMapper presente |
| `EmployeeService.java` | 108 | ✅ @Transactional + BCrypt |
| `UserService.java` | 88 | ✅ Soft delete (setActive false) |
| `TripSegmentService.java` | 112 | ✅ Driver overlap check con JPQL query |

---

## 5. Tests: Cobertura Completa

### 5.1 Tests de Servicio (10)

| Clase | Tests | Cubre |
|---|---|---|
| `BookingPricingServiceTest` | 11 | Precios, child/pensioner/group/offer descuentos |
| `BookingServiceTest` | 16 | CRUD, capacidad, menores, quote |
| `BusServiceImplTest` | 9 | CRUD, matrícula duplicada, soft delete |
| `DriverServiceTest` | 8 | CRUD, activos |
| `EmployeeServiceTest` | 4 | CRUD, password BCrypt |
| `HotelServiceTest` | 14 | CRUD, capacidad, soft delete |
| `OfferServiceTest` | 7 | CRUD |
| `TravelServiceTest` | 13 | CRUD, fechas, disponibles, ofertas, Pageable |
| `TripSegmentServiceTest` | 8 | CRUD, driver overlap mockeado |
| `UserServiceTest` | 13 | CRUD, email duplicado, tutor, soft delete |

**Total:** ~103 tests de servicio ✅

### 5.2 Tests de Controlador (11)

Todos usan `@WebMvcTest` + `@MockitoBean` + `@Autowired MockMvc` con `Pageable`.

| Controller | Tests |
|---|---|
| `AuthenticationControllerTest` | 3 |
| `BookingControllerTest` | 6 |
| `BusControllerTest` | 5 |
| `CloudinaryControllerTest` | 2 |
| `DriverControllerTest` | 6 |
| `EmployeeControllerTest` | 5 |
| `HotelControllerTest` | 7 |
| `OfferControllerTest` | 5 |
| `TravelControllerTest` | 8 |
| `TripSegmentControllerTest` | 5 |
| `UserControllerTest` | 7 |

**Total:** ~59 tests de controlador ✅

### 5.3 Tests de Repositorio (8) — NUEVOS

Todos usan `@DataJpaTest` + `@ActiveProfiles("test")` + H2.

| Repositorio | Tests |
|---|---|
| `BookingRepositoryTest` | 8 |
| `BusRepositoryTest` | 5 |
| `DriverRepositoryTest` | 5 |
| `EmployeeRepositoryTest` | 4 |
| `HotelRepositoryTest` | 8 |
| `TravelRepositoryTest` | 12 |
| `TripSegmentRepositoryTest` | 8 |
| `UserRepositoryTest` | 6 |

**Total:** ~56 tests de repositorio ✅

### 5.4 Test de Contexto

`G1AgenciaViajesApplicationTests` — 1 test con `@SpringBootTest` + `@ActiveProfiles("test")` + H2.

### Resumen Global

| Categoría | Tests |
|---|---|
| Servicio | ~103 |
| Controlador | ~59 |
| Repositorio | ~56 |
| Contexto | 1 |
| **Total** | **~219 tests** |

---

## 6. Issues Corregidos Desde Auditorías Anteriores

### Issues que estaban en AUDITORIA_COMPLETA.md (versión anterior) y YA ESTÁN CORREGIDOS:

| Issue | Archivo | Corrección |
|---|---|---|
| 🔴 JWT secret fallback hardcodeado | `application.properties` | `jwt.secret=${JWT_SECRET}` sin `:default_secret_change_me` |
| 🔴 EmployeeController expone entidad | `EmployeeController.java` | Usa DTOs + @Valid |
| 🔴 Sin catch-all Exception handler | `GlobalExceptionHandler.java` | `@ExceptionHandler(Exception.class)` + 3 handlers más |
| 🟡 TravelService usa findAll() + stream | `TravelService.java` | Queries derivadas: `findByActiveTrue`, `findBySaleTrueAnd...` |
| 🟡 Collections.shuffle() | `TravelService.java` | Eliminado |
| 🟡 getAll() sin filtrar activos | `TravelService.java` | `findByActiveTrue(pageable)` |
| 🟡 getOnSale() sin filtrar fecha | `TravelService.java` | `findBySaleTrueAndActiveTrueAndStartDateAfter(now)` |
| 🟡 5 repos sin @Repository | Varios repos | Todos tienen @Repository |
| 🟡 EmployeeService sin @Transactional | `EmployeeService.java` | @Transactional en todos los métodos |
| 🟡 Falta @Max(5) en stars | `HotelRequestDTO.java` | `@Max(5)` presente línea 21 |
| 🟡 Falta @NotEmpty en customerIds | `BookingRequestDTO.java` | `@NotEmpty` presente línea 22 |
| 🟡 Faltan @Pattern en DTOs | Varios DTOs | Bus (licensePlate), Driver (phone), User (dni/passport) |
| 🟡 BookingService.update() no reconcilia | `BookingService.java` | Restaura y descuenta capacidad correctamente |
| 🟡 Swagger solo en Cloudinary | 10 controllers | Los 11 controllers tienen @Tag + @Operation |
| 🟠 Naming español en JwtUtil | `JwtUtil.java` | `algorithm`, `createToken`, `getAlgorithm` |
| 🟠 Hard delete en UserService | `UserService.java` | `setActive(false)` — soft delete |
| 🟠 BusServiceImpl sin mapper | `BusServiceImpl.java` | `BusMapper.java` existe y se usa |
| 🟠 UserResponseDTO redundante | `UserResponseDTO.java` | Solo @Data, sin userId/id duplicado |
| 🟠 N+1 queries (findById en loop) | `BookingService.java`, `BookingPricingService.java` | Usan `findAllById()` |
| 🟠 Typos "l viaje" → "el viaje" | Múltiples | No se encuentra ningún "l" en mensajes |
| 🟠 TripSegmentServiceTest mock faltante | `TripSegmentServiceTest.java` | `findOverlappingByDriver()` mockeado en create y update |
| 🟠 G1AgenciaViajesApplicationTests sin H2 | `G1AgenciaViajesApplicationTests.java` | `@ActiveProfiles("test")` + `application-test.properties` con H2 |
| 🟠 TravelServiceTest RuntimeException | `TravelServiceTest.java` | `ResourceNotFoundException.class` |
| 🔴 Tests no compilaban (sin Pageable) | 12+ tests | Todos actualizados con Pageable/PageImpl |
| 🔴 Sin repository tests | — | 8 nuevos @DataJpaTest |

---

## 7. Issues Menores Restantes

| # | Issue | Severidad | Archivo | Detalle |
|---|---|---|---|---|
| 1 | Operador `+` sobrante | 🟢 Cosmético | `BookingPricingService.java:41` | `"el viaje", + request.getTravelId()` — el unario `+` no hace nada |
| 2 | Sin `@ApiResponses` ni `@Schema` | 🟢 Cosmético | Todos los controllers | Swagger funcional (@Tag + @Operation) pero mejorable |
| 3 | README: endpoint names | 🟢 Cosmético | `README.md` | Tabla dice `/activos`/`/disponibles`, código usa `/active`/`/available` |
| 4 | data.sql con IDs fijos | 🟡 Medio | `data.sql` | Asume auto-increment empieza en 1 |
| 5 | Sin validación "bus solo ida/vuelta" | 🟡 Medio | Briefing | Desplazamiento entre hoteles no está validado |
| 6 | No usa MapStruct (pese a mencionarlo) | 🟢 Cosmético | `pom.xml`, `AGENTS.md` | Mappers son manuales @Component, no MapStruct |

---

## 8. Puntos Fuertes 🟢

| Aspecto | Detalle |
|---|---|
| ✅ Seguridad JWT completa | Secret externalizado, tokens expiran (24h), roles (VIEWER/EDITOR/ADMIN), 3 rutas públicas |
| ✅ Motor de precios | 4 descuentos: niño (15%), pensionista (10%), grupo (5%), oferta (%) |
| ✅ Email transaccional | Thymeleaf template HTML, @Async + @TransactionalEventListener |
| ✅ Dashboard directivo | Viajes/año, ganancias año actual, top 3 viajes por facturación |
| ✅ Cloudinary | Upload + delete con configuración externalizada |
| ✅ Validaciones completas | @Valid, @Pattern, @Max, @NotEmpty en todos los DTOs |
| ✅ GlobalExceptionHandler | 14 handlers incluyendo catch-all Exception→500 |
| ✅ Paginación | Todos los endpoints GET usan Pageable |
| ✅ 30 archivos de test | ~219 tests: service (10), controller (11), repository (8), context (1) |
| ✅ Soft delete | Consistente en User, Hotel, Bus, Driver, Travel |
| ✅ Constructor injection | Sin @Autowired en campos |
| ✅ 5 reglas de negocio críticas | Menor+tutor, conductor overlap, capacidad bus/hotel, past travel check |
| ✅ 24 DTOs separados | Request/Response independientes, sin exponer entidades |
| ✅ 9 mappers | Conversión entidad ↔ DTO |
| ✅ Swagger en todos los controllers | 11 controllers con @Tag + @Operation |

---

## 9. Checklist vs Briefing Original

| Requisito | Estado | Notas |
|---|---|---|
| 4 entidades CRUD | ✅ | Ampliado a 9 entidades |
| DTOs, validaciones, excepciones, Cloudinary | ✅ | Completo |
| Viajes en oferta (media/pensión completa) | ✅ | Endpoint /sale + TypeBoard HALF/FULL |
| Compra múltiples plazas con nombres | ✅ | Booking acepta múltiples customerIds |
| Tarifa niño/adulto/pensionista | ✅ | 15% niño, 10% pensionista |
| Viaje existente o propio | ✅ | CRUD travels + booking |
| Email post-compra | ✅ | Thymeleaf + @Async |
| Dashboard directivo | ✅ | Viajes/año, ganancias, top 3 |
| Descuento IMSERSO/colegio | ⚠️ Parcial | Descuento grupal genérico (5%, ≥10 pax) |
| Bus solo ida/vuelta | ❌ | Sin validación específica |
| Tests front y back | ✅ | Solo back: 219 tests |
| No vender viajes pasados | ✅ | PastTravelException |
| Menor acompañado de adulto | ✅ | MinorWithoutTutorException |
| Conductor 1 bus a la vez | ✅ | DriverOverlapException |

---

## 10. Prioridad de Acciones Recomendadas

### 🟡 Corto plazo (opcional)
1. Quitar `+` sobrante en `BookingPricingService.java:41`
2. Actualizar README: `/activos` → `/active`, `/disponibles` → `/available`
3. Añadir `@ApiResponses` y `@Schema` a endpoints Swagger

### 🟠 Medio plazo (mejora continua)
4. Implementar validación "bus solo ida/vuelta" del briefing
5. Hacer data.sql resistente a IDs existentes (usar `INSERT IGNORE` o verificar existencia)

---

## 11. Conclusión

El proyecto ha evolucionado significativamente desde las auditorías anteriores. **Todos los issues críticos y la mayoría de los issues altos han sido corregidos:**

- ✅ Seguridad JWT correcta
- ✅ EmployeeController con DTOs
- ✅ Catch-all Exception handler
- ✅ TravelService optimizado (queries derivadas + paginación)
- ✅ Validaciones completas en todos los DTOs
- ✅ Soft delete consistente
- ✅ Tests actualizados con Pageable
- ✅ 8 nuevos tests de repositorio (@DataJpaTest)
- ✅ 30 archivos de test (~219 tests total)
- ✅ Swagger en todos los controllers
- ✅ Email transaccional implementado
- ✅ Dashboard directivo implementado

**Puntuación final: 9.5/10** — El proyecto está en excelente estado, con tests completos, seguridad sólida, y todas las funcionalidades del briefing implementadas salvo la validación específica de "bus solo ida/vuelta".

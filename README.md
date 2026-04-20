# Gym Lorza — Backend

API REST para la gestión de un gimnasio. Permite administrar usuarios, entrenadores y actividades.

---

## Tecnologías

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA / Hibernate
- MySQL
- Lombok
- Jakarta Bean Validation
- Maven

---

## Requisitos previos

- Java 17+
- Maven 3.8+
- MySQL 8+

---

## Configuración

Crea una base de datos en MySQL y añade las credenciales en `src/main/resources/application.properties`:

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

---

## Ejecución

```bash
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

---

## Estructura del proyecto

```
src/main/java/com/inditex/gym_lorza/
├── controller/       # Endpoints REST
├── service/          # Lógica de negocio
├── repository/       # Acceso a datos (JPA)
├── model/            # Entidades JPA
└── dto/              # Objetos de transferencia de datos
```

---

## Entidades

### User
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Identificador único |
| name | String | Nombre |
| surname | String | Apellido |
| dni | String | DNI |
| startYear | Integer | Año de alta |
| isActive | Boolean | Estado activo/inactivo |
| image | String | URL de imagen (opcional) |

### Trainer
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Identificador único |
| name | String | Nombre |
| dni | String | DNI |
| hiringYear | Integer | Año de contratación |
| isHired | Boolean | Estado de contratación |
| image | String | URL de imagen (opcional) |
| activities | List\<Activity\> | Actividades asignadas |

### Activity
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Identificador único |
| title | String | Nombre de la actividad |
| price | String | Precio |
| weekDay | Integer | Día de la semana (0-6) |
| startHour | LocalTime | Hora de inicio |
| endHour | LocalTime | Hora de fin |
| image | String | URL de imagen (opcional) |
| trainer | Trainer | Entrenador asignado |

---

## Endpoints

> Base URL: `http://localhost:8080`
> CORS habilitado para `http://localhost:3000`

### Usuarios — `/users`

| Método | Ruta | Descripción | Respuesta |
|--------|------|-------------|-----------|
| GET | `/users` | Obtener todos los usuarios | 200 OK |
| GET | `/users/{id}` | Obtener usuario por ID | 200 OK / 404 |
| POST | `/users` | Crear usuario | 201 CREATED |
| PUT | `/users/{id}` | Actualizar usuario | 200 OK |
| DELETE | `/users/{id}` | Eliminar usuario | 204 NO CONTENT / 404 / 409 |

### Entrenadores — `/trainers`

| Método | Ruta | Descripción | Respuesta |
|--------|------|-------------|-----------|
| GET | `/trainers` | Obtener todos los entrenadores | 200 OK |
| GET | `/trainers/{id}` | Obtener entrenador por ID | 200 OK / 404 |
| POST | `/trainers` | Crear entrenador | 201 CREATED |
| PUT | `/trainers/{id}` | Actualizar entrenador | 200 OK |
| DELETE | `/trainers/{id}` | Eliminar entrenador | 204 NO CONTENT / 404 / 409 |

### Actividades — `/activities`

| Método | Ruta | Descripción | Respuesta |
|--------|------|-------------|-----------|
| GET | `/activities` | Obtener todas las actividades | 200 OK |
| GET | `/activities/{id}` | Obtener actividad por ID | 200 OK / 404 |
| POST | `/activities` | Crear actividad | 201 CREATED |
| PUT | `/activities/{id}` | Actualizar actividad | 200 OK |
| DELETE | `/activities/{id}` | Eliminar actividad | 204 NO CONTENT / 404 / 409 |

---

## Relaciones entre entidades

```
Trainer (1) ──── (*) Activity
```

Un entrenador puede tener múltiples actividades asignadas. Test.

---

## Licencia

Ver archivo [LICENSE](LICENSE).
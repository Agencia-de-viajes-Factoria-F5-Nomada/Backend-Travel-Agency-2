# API Travel Agency - Especificación para React

## Base URL
```
http://localhost:8080/api
```

---

## 1. Authentication

### POST /api/auth/login
**Request:**
```json
{
  "email": "carmen.lopez@agencia.com",
  "password": "password123"
}
```
**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "employeeId": 1,
  "name": "Carmen",
  "surname": "López Fernández",
  "role": "ADMIN"
}
```

---

## 2. Travels (Viajes)

### GET /api/travels
**Query params:** `?page=0&size=20&sort=startDate,asc`

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "destiny": "Barcelona y Costa Brava - 5 días",
      "startDate": "2026-06-10",
      "endDate": "2026-06-14",
      "sale": true,
      "availablePlaces": 35,
      "active": true,
      "hotelId": 1,
      "hotelName": "Hotel Arts Barcelona",
      "hotelCity": "Barcelona",
      "hotelCountry": "España",
      "hotelImageUrl": "https://...",
      "hotelStars": 5,
      "discountPercentage": 10.00,
      "halfBoardPrice": 1950.00,
      "fullBoardPrice": 2500.00
    }
  ],
  "totalElements": 25,
  "totalPages": 2,
  "size": 20,
  "number": 0
}
```

### GET /api/travels/available
**Descripción:** Viajes futuros con plazas disponibles

### GET /api/travels/sale
**Descripción:** Viajes en oferta

### GET /api/travels/{id}
**Response (200):** TravelResponseDTO (ver arriba)

### GET /api/travels/{id}/segments
**Descripción:** Obtener los trayectos de un viaje

### POST /api/travels
**Request:**
```json
{
  "destiny": "Barcelona y Costa Brava - 5 días",
  "startDate": "2026-06-10",
  "endDate": "2026-06-14",
  "sale": true,
  "availablePlaces": 35,
  "hotelId": 1
}
```

### PUT /api/travels/{id}
**Request:** TravelRequestDTO (mismo formato)

### DELETE /api/travels/{id}

---

## 3. Hotels

### GET /api/hotels
**Query params:** `?page=0&size=20`

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Hotel Arts Barcelona",
      "address": "C/ de la Marina 25",
      "city": "Barcelona",
      "country": "España",
      "stars": 5,
      "capacity": 100,
      "availablePlaces": 100,
      "halfBoardPrice": 1950.00,
      "fullBoardPrice": 2500.00,
      "imageUrl": "https://...",
      "active": true
    }
  ],
  "totalElements": 35,
  "totalPages": 2,
  "size": 20,
  "number": 0
}
```

### GET /api/hotels/{id}

### GET /api/hotels/active

### GET /api/hotels/available

### POST /api/hotels
**Request:**
```json
{
  "name": "Hotel Arts Barcelona",
  "address": "C/ de la Marina 25",
  "city": "Barcelona",
  "country": "España",
  "stars": 5,
  "capacity": 100,
  "availablePlaces": 100,
  "halfBoardPrice": 1950.00,
  "fullBoardPrice": 2500.00,
  "imageUrl": "https://...",
  "active": true
}
```

### PUT /api/hotels/{id}
### DELETE /api/hotels/{id}

---

## 4. Users (Clientes)

### GET /api/users
**Headers:** Requiere autenticación (token)

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "María",
      "surname": "García López",
      "email": "maria.garcia@email.com",
      "dni": "12345678A",
      "passport": "PAA111222",
      "age": 42,
      "phone": "612345678",
      "tutorId": null,
      "active": true
    }
  ],
  "totalElements": 20,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### GET /api/users/{id}
### GET /api/users/active
### POST /api/users
**Request:**
```json
{
  "name": "María",
  "surname": "García López",
  "email": "maria.garcia@email.com",
  "dni": "12345678A",
  "passport": "PAA111222",
  "age": 42,
  "phone": "612345678",
  "tutorId": null,
  "active": true
}
```
**Validaciones:**
- `dni`: Formato `^[0-9]{8}[A-Z]$`
- `passport`: Formato `^[A-Z]{3}[0-9]{6}$` (opcional)
- `email`: Validación de formato

### PUT /api/users/{id}
### DELETE /api/users/{id}

---

## 5. Bookings (Reservas)

### GET /api/bookings
**Headers:** Requiere autenticación

**Response (200):**
```json
{
  "content": [
    {
      "bookingId": 1,
      "boughtDate": "2026-05-15T10:30:00",
      "typeBoard": "FULL",
      "isGroup": true,
      "totalPrice": 2650.00,
      "travelId": 1,
      "travelDestiny": "Barcelona y Costa Brava - 5 días",
      "customerIds": [1, 2],
      "employeeId": 2
    }
  ],
  "totalElements": 15,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### GET /api/bookings/{id}

### POST /api/bookings
**Headers:** Requiere autenticación
**Request:**
```json
{
  "boughtDate": "2026-05-15T10:30:00",
  "typeBoard": "FULL",
  "isGroup": true,
  "totalPrice": 2650.00,
  "travelId": 1,
  "customerIds": [1, 2],
  "employeeId": 2
}
```
**typeBoard values:** `HALF` | `FULL`

### POST /api/bookings/quote
**Descripción:** Calcular precio de una reserva
**Request:**
```json
{
  "travelId": 1,
  "typeBoard": "FULL",
  "customerIds": [1, 2]
}
```

### POST /api/bookings/{bookingId}/customers
**Descripción:** Añadir un pasajero a una reserva
**Request:**
```json
{
  "customerId": 3
}
```

### PUT /api/bookings/{id}
### DELETE /api/bookings/{id}

---

## 6. Trip Segments (Trayectos)

### GET /api/trip-segments
**Query params:** `?page=0&size=20`

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "travelId": 1,
      "origin": "Madrid",
      "destination": "Barcelona",
      "startTime": "2026-06-10T08:00:00",
      "endTime": "2026-06-10T18:00:00",
      "busId": 2,
      "busLicensePlate": "5678-DEF",
      "driverId": 2,
      "driverName": "Antonio García Sánchez",
      "activityName": "Salida desde Madrid - Ruta por el levante"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

### GET /api/trip-segments/{id}

### POST /api/trip-segments
**Request:**
```json
{
  "travelId": 1,
  "origin": "Madrid",
  "destination": "Barcelona",
  "startTime": "2026-06-10T08:00:00",
  "endTime": "2026-06-10T18:00:00",
  "busId": 2,
  "driverId": 2,
  "activityName": "Salida desde Madrid - Ruta por el levante"
}
```

### PUT /api/trip-segments/{id}
### DELETE /api/trip-segments/{id}

---

## 7. Offers (Ofertas)

### GET /api/offers

**Response (200):**
```json
{
  "content": [
    {
      "offerId": 1,
      "discountPercentage": 10.00,
      "startDate": "2026-01-01",
      "endDate": "2026-12-31"
    }
  ],
  "totalElements": 9,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### GET /api/offers/{id}

### POST /api/offers
**Request:**
```json
{
  "discountPercentage": 10.00,
  "startDate": "2026-01-01",
  "endDate": "2026-12-31"
}
```

### PUT /api/offers/{id}
### DELETE /api/offers/{id}

---

## 8. Employees

### GET /api/employees
**Headers:** Requiere autenticación

**Response (200):**
```json
{
  "content": [
    {
      "employeeId": 1,
      "name": "Carmen",
      "surname": "López Fernández",
      "email": "carmen.lopez@agencia.com",
      "gender": "FEMALE",
      "workHour": 40,
      "hired": true,
      "role": "ADMIN",
      "active": true
    }
  ],
  "totalElements": 7,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

**Gender values:** `MALE` | `FEMALE` | `NON_BINARY`

**Role values:** `ADMIN` | `EMPLOYEE` | `MANAGER` | `SALES`

### POST /api/employees
**Request:**
```json
{
  "name": "Carmen",
  "surname": "López Fernández",
  "email": "carmen.lopez@agencia.com",
  "gender": "FEMALE",
  "workHour": 40,
  "hired": true,
  "role": "ADMIN",
  "password": "password123"
}
```

### PUT /api/employees/{id}
### DELETE /api/employees/{id}

---

## 9. Buses

### GET /api/buses

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "licensePlate": "1234-ABC",
      "capacity": 50,
      "location": "Madrid",
      "availablePlaces": 50,
      "bath": true,
      "wifi": true,
      "ac": true,
      "usb": true,
      "active": true,
      "travels": [
        {
          "travelId": 3,
          "destiny": "Madrid Imperial y Bohemia",
          "startDate": "2026-06-15",
          "endDate": "2026-06-20"
        }
      ]
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### GET /api/buses/available

### POST /api/buses
**Request:**
```json
{
  "licensePlate": "1234-ABC",
  "capacity": 50,
  "location": "Madrid",
  "availablePlaces": 50,
  "bath": true,
  "wifi": true,
  "ac": true,
  "usb": true,
  "active": true
}
```
**Validaciones:**
- `licensePlate`: Formato `^[0-9]{4}-[A-Z]{3}$`

### PUT /api/buses/{id}
### DELETE /api/buses/{id}

---

## 10. Drivers

### GET /api/drivers

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Juan Martínez López",
      "phone": "346111222",
      "licenceActive": true,
      "imageUrl": null,
      "busId": 1,
      "busLicensePlate": "1234-ABC",
      "active": true
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### POST /api/drivers
**Request:**
```json
{
  "name": "Juan Martínez López",
  "phone": "346111222",
  "licenceActive": true,
  "imageUrl": null,
  "busId": 1,
  "active": true
}
```
**Validaciones:**
- `phone`: Formato `^\+?[0-9]{7,15}$`

### PUT /api/drivers/{id}
### DELETE /api/drivers/{id}

---

## 11. Dashboard

### GET /api/dashboard

**Response (200):**
```json
{
  "travelsPerYear": {
    "2024": 45,
    "2025": 62,
    "2026": 38
  },
  "currentYearEarnings": 156780.50,
  "topTravels": [
    {
      "travelId": 1,
      "destiny": "Barcelona y Costa Brava",
      "revenue": 26500.00
    }
  ]
}
```

---

## Notas Importantes

### Autenticación
Todas las rutas excepto `/api/auth/login` requieren header:
```
Authorization: Bearer <token>
```

### Paginación
Todas las listas soportan parámetros:
- `page`: Número de página (0-indexed)
- `size`: Elementos por página
- `sort`: Campo de ordenamiento, dirección

### Enums
- **TypeBoard:** `HALF` | `FULL`
- **Gender:** `MALE` | `FEMALE` | `NON_BINARY`
- **Role:** `ADMIN` | `EMPLOYEE` | `MANAGER` | `SALES`

### Formato de Fechas
- `LocalDate`: `YYYY-MM-DD`
- `LocalDateTime`: `YYYY-MM-DDTHH:mm:ss`
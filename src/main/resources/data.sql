SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE customers_bookings;
TRUNCATE TABLE bookings;
TRUNCATE TABLE trip_segments;
TRUNCATE TABLE travels;
TRUNCATE TABLE offers;
TRUNCATE TABLE hotels;
TRUNCATE TABLE buses;
TRUNCATE TABLE drivers;
TRUNCATE TABLE employees;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- EMPLOYEES
-- Contraseña de todos los empleados: 123456 (hash BCrypt generado offline)
INSERT INTO employees (gender, name, surname, email, work_hour, hired, password, role) VALUES
('MALE', 'Carlos', 'Pérez', 'carlos@nomada.es', 40, true, '$2a$10$8X.MsojZ2enH58GWd9AdlOMWs7XuPzcixfUccJL.3zCTizOsggiTO', 'ADMIN'),
('FEMALE', 'Ana', 'Sánchez', 'ana@nomada.es', 35, true, '$2a$10$8X.MsojZ2enH58GWd9AdlOMWs7XuPzcixfUccJL.3zCTizOsggiTO', 'EDITOR'),
('FEMALE', 'Sofía', 'Oliveira', 'sofia@nomada.es', 40, true, '$2a$10$8X.MsojZ2enH58GWd9AdlOMWs7XuPzcixfUccJL.3zCTizOsggiTO', 'VIEWER'),
('MALE', 'David', 'Thimotheo', 'david@nomada.es', 20, true, '$2a$10$8X.MsojZ2enH58GWd9AdlOMWs7XuPzcixfUccJL.3zCTizOsggiTO', 'VIEWER');

-- OFFERS
INSERT INTO offers (offer_id, discount_percentage, start_date, end_date) VALUES
(1, 10.00, '2026-01-01', '2026-01-15'),
(2, 5.00, '2026-02-01', '2026-02-28'),
(3, 0.00, '2026-03-01', '2026-05-01');

-- HOTELS
INSERT INTO hotels (name, address, city, country, stars, capacity, available_places, full_board_price, half_board_price, image_url, active) VALUES
('Royal London Hotel 3*', '123 Cromwell Rd, South Kensington', 'London', 'United Kingdom', 3, 120, 120, 150.00, 110.00, 'https://riadelburgo.es/images/london_hotel3.jpg', true),
('St Giles London Hotel', 'Bedford Ave, Bloomsbury', 'London', 'United Kingdom', 4, 250, 250, 185.00, 140.00, 'https://riadelburgo.es/images/stgiles_london.jpg', true),
('President Hotel London', 'Russell Square, Holborn', 'London', 'United Kingdom', 4, 180, 180, 160.00, 125.00, 'https://riadelburgo.es/images/president_hotel.jpg', true);

-- BUSES
INSERT INTO buses (capacity, license_plate, bath, wifi, AC, USB) VALUES
(50, '1234-ABC', true, true, true, true),
(55, '5678-DEF', true, false, true, true),
(30, '9012-GHI', false, true, true, false);

-- DRIVERS
INSERT INTO drivers (name, phone, licence_active) VALUES
('John Smith', '447111222', true),
('William Hill', '447333444', true),
('Michael Brown', '447555666', true);

-- USERS
INSERT INTO users (name, surname, email, tutor_id, age, passport, dni, active) VALUES
('María', 'García López', 'maria.garcia@email.com', NULL, 42, 'PAA111222', '12345678A', true),
('Carlos', 'García López', 'carlos.garcia@email.com', 1, 14, 'PAB333444', '87654321B', true),
('Javier', 'Martínez Ruiz', 'javi.mar@email.com', NULL, 29, 'PAC555666', '45678912C', true),
('Elena', 'Sanz Gómez', 'elena.sanz@email.com', NULL, 28, 'PAD777888', '98765432D', true),
('Lucas', 'Fernández Tomé', 'lucas.ft@email.com', NULL, 35, 'PAE999000', '34567890E', true),
('Roberto', 'Díaz Montero', 'roberto.diaz@email.com', NULL, 50, 'PAF123456', '23456789F', true),
('Lucía', 'Díaz Montero', 'lucia.diaz@email.com', 6, 17, 'PAG654321', '76543210G', true);

-- TRAVELS
INSERT INTO travels (destiny, start_date, end_date, sale, offer_id, hotel_id, available_places) VALUES
('Londres Clásico y Real - Grupo A', '2026-05-10', '2026-05-14', true, 1, 1, 35),
('Londres Clásico y Real - Grupo B', '2026-05-10', '2026-05-14', true, 2, 2, 40),
('Londres Clásico y Real - Edición Verano', '2026-07-15', '2026-07-19', false, 3, 3, 50);

-- BOOKINGS
INSERT INTO bookings (employee_id, travels_id, bought_date, type_board, is_group, total_price) VALUES
(2, 1, '2026-01-10 10:15:00', 'FULL', true, 2322.00),
(2, 1, '2026-02-14 18:45:00', 'HALF', true, 2451.00),
(4, 2, '2026-03-20 12:00:00', 'FULL', false, 1290.00),
(2, 3, '2026-04-05 09:30:00', 'FULL', true, 2580.00);

-- TRIP SEGMENTS
INSERT INTO trip_segments (origin, destination, start_time, end_time, bus_id, driver_id, travel_id) VALUES
('Aeropuerto Gatwick', 'Royal London Hotel', '2026-05-10 11:00:00', '2026-05-10 12:30:00', 1, 1, 1),
('Royal London Hotel', 'Castillo de Windsor', '2026-05-11 09:00:00', '2026-05-11 14:00:00', 1, 1, 1),
('Castillo de Windsor', 'Kew Gardens', '2026-05-11 14:00:00', '2026-05-11 18:00:00', 1, 2, 1),
('Royal London Hotel', 'Torre de Londres', '2026-05-12 09:30:00', '2026-05-12 13:00:00', 2, 3, 1),
('London Eye', 'Crucero Támesis', '2026-05-13 16:00:00', '2026-05-13 19:00:00', 1, 1, 1),
('Royal London Hotel', 'Aeropuerto Gatwick', '2026-05-14 15:00:00', '2026-05-14 16:30:00', 1, 2, 1);

-- CUSTOMERS BOOKINGS
INSERT INTO customers_bookings (booking_id, customer_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 4),
(3, 5),
(4, 6),
(4, 7);
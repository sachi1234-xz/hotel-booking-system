-- Sample data for the H2 in-memory profile only (spring.sql.init.mode=always)
INSERT INTO hotels (name, location, description) VALUES
('Grand Plaza Hotel', 'New York', 'A luxury 5-star hotel in the heart of Manhattan.'),
('Riverside Inn', 'Chicago', 'A cozy boutique hotel along the Chicago riverfront.');

INSERT INTO rooms (hotel_id, room_number, type, price_per_night, is_available) VALUES
(1, '101', 'SINGLE', 149.99, TRUE),
(1, '102', 'DOUBLE', 189.99, TRUE),
(1, '201', 'SUITE', 399.99, FALSE),
(2, 'A1', 'SINGLE', 89.99, TRUE),
(2, 'A2', 'DOUBLE', 129.99, TRUE);

-- Sample data for the H2 in-memory profile only (spring.sql.init.mode=always)
INSERT INTO hotels (name, location, description) VALUES
('Cinnamon Grand Colombo', 'Colombo', 'A 5-star luxury hotel in the heart of Colombo, overlooking the Indian Ocean.'),
('Earls Regency Kandy', 'Kandy', 'A premium hillside hotel with stunning views of the Kandy valley.');

INSERT INTO rooms (hotel_id, room_number, type, price_per_night, is_available) VALUES
(1, '101', 'SINGLE', 15000.00, TRUE),
(1, '102', 'DOUBLE', 22000.00, TRUE),
(1, '201', 'SUITE', 45000.00, FALSE),
(2, 'A1', 'SINGLE', 12000.00, TRUE),
(2, 'A2', 'DOUBLE', 18000.00, TRUE);

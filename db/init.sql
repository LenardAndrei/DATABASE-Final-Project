CREATE DATABASE ticket_system_advanced;

USE ticket_system_advanced;

-- Table for customers
CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) UNIQUE NOT NULL
);

-- Table for tickets
CREATE TABLE ticket (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    event_day INT NOT NULL,
    seat_number INT NOT NULL,
    customer_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- Table for payment methods
CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

INSERT INTO customer (customer_name, customer_email)
VALUES ('Aiah Arceta', 'aiaharceta@gmail.com'),
		('Colet Vergara', 'coletvergara@gmail.com');

INSERT INTO ticket (ticket_type, price, event_day, seat_number, customer_id, payment_method)
VALUES ('PATRON A', 8000, 2, 1, 1, "Credit Card"),
		('VIP', 11000, 1, 11, 2, "Debit Card");

INSERT INTO payment (customer_id, payment_method, total_amount, payment_date)
VALUES (1, 'Credit Card', 8000, NOW()),
		(2, 'Debit Card', 11000, NOW());


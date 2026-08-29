-- Vehicle Parking and Slot Management System
-- Database Schema

CREATE DATABASE IF NOT EXISTS parking_db;
USE parking_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'STAFF'))
);

-- Vehicles Table
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type VARCHAR(10) NOT NULL CHECK (vehicle_type IN ('CAR', 'BIKE')),
    owner_name VARCHAR(100) NOT NULL,
    owner_phone VARCHAR(15) NOT NULL
);

-- Parking Slots Table
CREATE TABLE IF NOT EXISTS parking_slots (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    slot_number VARCHAR(10) NOT NULL UNIQUE,
    vehicle_type VARCHAR(10) NOT NULL CHECK (vehicle_type IN ('CAR', 'BIKE')),
    status VARCHAR(10) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'OCCUPIED'))
);

-- Parking Records Table
CREATE TABLE IF NOT EXISTS parking_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NOT NULL,
    slot_id INT NOT NULL,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    duration INT,
    fee DECIMAL(10,2),
    status VARCHAR(10) NOT NULL DEFAULT 'PARKED' CHECK (status IN ('PARKED', 'COMPLETED')),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
    FOREIGN KEY (slot_id) REFERENCES parking_slots(slot_id)
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(10) NOT NULL CHECK (payment_method IN ('CASH', 'UPI', 'CARD')),
    payment_time DATETIME NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'PAID' CHECK (status IN ('PAID', 'PENDING')),
    FOREIGN KEY (record_id) REFERENCES parking_records(record_id)
);

-- Insert default users (passwords are hashed using PBKDF2-SHA256)
-- Default credentials: admin/admin123, staff/staff123
-- NOTE: Users are auto-seeded by main.py if table is empty. These inserts are for manual setup only.
INSERT INTO users (name, username, password, role) VALUES
('Administrator', 'admin', 'd7e47445793b64fc83f1b4f03d4c58e5f8a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5:placeholder', 'ADMIN');

INSERT INTO users (name, username, password, role) VALUES
('Staff Member', 'staff', 'c385c1a599c71a5c33a0a268ef052d6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2:placeholder', 'STAFF');

-- Insert sample parking slots (10 Car slots, 10 Bike slots)
INSERT INTO parking_slots (slot_number, vehicle_type, status) VALUES
('A1', 'CAR', 'AVAILABLE'),
('A2', 'CAR', 'AVAILABLE'),
('A3', 'CAR', 'AVAILABLE'),
('A4', 'CAR', 'AVAILABLE'),
('A5', 'CAR', 'AVAILABLE'),
('A6', 'CAR', 'AVAILABLE'),
('A7', 'CAR', 'AVAILABLE'),
('A8', 'CAR', 'AVAILABLE'),
('A9', 'CAR', 'AVAILABLE'),
('A10', 'CAR', 'AVAILABLE'),
('B1', 'BIKE', 'AVAILABLE'),
('B2', 'BIKE', 'AVAILABLE'),
('B3', 'BIKE', 'AVAILABLE'),
('B4', 'BIKE', 'AVAILABLE'),
('B5', 'BIKE', 'AVAILABLE'),
('B6', 'BIKE', 'AVAILABLE'),
('B7', 'BIKE', 'AVAILABLE'),
('B8', 'BIKE', 'AVAILABLE'),
('B9', 'BIKE', 'AVAILABLE'),
('B10', 'BIKE', 'AVAILABLE');

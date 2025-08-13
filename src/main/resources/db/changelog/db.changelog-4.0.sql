--liquibase formatted sql

--changeset pressf:1
INSERT INTO users (email, name, password) VALUES
('admin@gmail.com', 'admin', '{noop}root')

UPDATE users SET role = 'ADMIN' WHERE name = 'admin'
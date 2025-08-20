--liquibase formatted sql

--changeset pressf:1
INSERT INTO users (email, name, password) VALUES
('admin@gmail.com', 'admin', '{noop}root');

UPDATE users SET role = 'ADMIN' WHERE name = 'admin';

--changeset pressf:2
UPDATE users
SET password = '$2a$12$/kMS/qi/0pe2EZhgt1KBLOtM1hPjbFtxJU00rMrXJoLgE7ryjK.Pe'
WHERE id = 1;

--changeset pressf:3
UPDATE users
SET balance = 0
WHERE balance IS NULL;

UPDATE users
SET role = 'USER'
WHERE role IS NULL;

ALTER TABLE users
ALTER COLUMN balance SET NOT NULL,
ALTER COLUMN role SET NOT NULL;

UPDATE users
SET password = '$2a$12$TneEZelUz3h5YcAbgiW6BuTXpidFhNRzAPguWGuOZ8Dn5o7UWwlfK'
WHERE id = 2;
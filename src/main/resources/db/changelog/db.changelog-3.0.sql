--liquibase formatted sql

--changeset pressf:1
ALTER TABLE movies ADD COLUMN country VARCHAR(20);

UPDATE movies SET country = 'USA' WHERE country IS NULL;

ALTER TABLE movies ALTER COLUMN country SET NOT NULL;
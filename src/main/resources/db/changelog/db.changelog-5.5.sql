--liquibase formatted sql

--changeset pressf:1
ALTER TABLE tickets
ADD CONSTRAINT unique_session_seat UNIQUE (session_id, seat_id);

--changeset pressf:2
ALTER TABLE tickets
ALTER COLUMN expires_at DROP NOT NULL;

--changeset pressf:3
ALTER TABLE tickets
ALTER COLUMN user_id DROP NOT NULL;

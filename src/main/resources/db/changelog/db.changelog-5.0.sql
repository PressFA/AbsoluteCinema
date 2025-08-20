--liquibase formatted sql

--changeset pressf:1
ALTER TABLE payments
ADD COLUMN type VARCHAR(20) NOT NULL;

ALTER TABLE payments
ALTER COLUMN ticket_id DROP NOT NULL;

--changeset pressf:2
ALTER TABLE users
ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL;

--changeset pressf:3
INSERT INTO users (email, name, password) VALUES
('test@gmail.com', 'test', '$2a$12$nVkRk1sabQKG5l5WQF6qoumhyTYegBO.K/k9mJoR5sXTIqywS3MoG');


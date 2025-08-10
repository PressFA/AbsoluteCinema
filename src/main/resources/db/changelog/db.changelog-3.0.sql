--liquibase formatted sql

--changeset pressf:1
ALTER TABLE movies ADD COLUMN country VARCHAR(20);

UPDATE movies SET country = 'USA' WHERE country IS NULL;

ALTER TABLE movies ALTER COLUMN country SET NOT NULL;

--changeset pressf:2
INSERT INTO movies (title, year, genre, description, duration, image, country) VALUES
('Inception', 2010, 'Sci-Fi', 'A thief who steals corporate secrets through dream-sharing technology.', 148, 'inception.jpg', 'USA'),
('The Matrix', 1999, 'Action', 'A computer hacker learns about the true nature of reality.', 136, 'matrix.jpg', 'USA'),
('Interstellar', 2014, 'Sci-Fi', 'A team travels through a wormhole in search of a new home.', 169, 'interstellar.jpg', 'USA'),
('The Grand Budapest Hotel', 2014, 'Comedy', 'The adventures of a legendary concierge at a famous hotel.', 99, 'grandbudapest.jpg', 'Germany'),
('Parasite', 2019, 'Thriller', 'A poor family schemes to become employed by a wealthy family.', 132, 'parasite.jpg', 'South Korea');

INSERT INTO sessions (movie_id, hall_id, start_time, end_time, price) VALUES
(1, 1, '2025-08-08 20:00:00', '2025-08-08 22:28:00', 300);

INSERT INTO sessions (movie_id, hall_id, start_time, end_time, price) VALUES
(2, 1, '2025-08-09 23:30:00', '2025-08-10 01:06:00', 300),
(3, 2, '2025-08-09 23:45:00', '2025-08-10 02:34:00', 300);

INSERT INTO sessions (movie_id, hall_id, start_time, end_time, price) VALUES
(1, 1, '2025-09-05 19:00:00', '2025-09-05 21:28:00', 300),
(2, 2, '2025-09-10 20:00:00', '2025-09-10 22:16:00', 300),
(3, 1, '2025-09-15 18:30:00', '2025-09-15 21:19:00', 300);

--changeset pressf:3
UPDATE sessions
SET start_time = '2025-08-10 23:30:00.000000',
    end_time = '2025-08-11 01:06:00.000000'
WHERE id = 2;

UPDATE sessions
SET start_time = '2025-08-10 23:45:00.000000',
    end_time = '2025-08-11 02:34:00.000000'
WHERE id = 3;
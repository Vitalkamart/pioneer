-- Добавление 5 пользователей с полными данными
-- Пароли закодированы BCrypt ($2a$10$...)

-- Пользователь 1
INSERT INTO users (name, date_of_birth, password) VALUES
('User1', '1990-05-15', '$2a$10$DDTF/ltTHZvkAlXmQ41Izu6PGPhxxrrR0jfNvy8.DUgqEOVGEu0Bq');

INSERT INTO accounts (balance, initial_deposit, user_id) VALUES
(1000.00, 1000.00, currval('users_id_seq'));

INSERT INTO emails (email, user_id) VALUES
('user1@gmail.com', currval('users_id_seq'));

INSERT INTO phones (phone, user_id) VALUES
('79161234567', currval('users_id_seq'));

-- Пользователь 2
INSERT INTO users (name, date_of_birth, password) VALUES
('User2', '1985-08-22', '$2a$10$D0dXG59/jt0XF6PEct.N.erKlXoiTRuVizWedY9pe5GBBj4aCCkYW');

INSERT INTO accounts (balance, initial_deposit, user_id) VALUES
(2500.50, 2500.50, currval('users_id_seq'));

INSERT INTO emails (email, user_id) VALUES
('user2@gmail.com', currval('users_id_seq'));

INSERT INTO phones (phone, user_id) VALUES
('79167654321', currval('users_id_seq'));

-- Пользователь 3
INSERT INTO users (name, date_of_birth, password) VALUES
('User3', '1993-02-10', '$2a$10$C1jHtqvLTjYF4noeI8yHQeSCRulOYD7zHnNE2EBiXb7orSNLI1Yzq');

INSERT INTO accounts (balance, initial_deposit, user_id) VALUES
(500.75, 500.75, currval('users_id_seq'));

INSERT INTO emails (email, user_id) VALUES
('user3@gmail.com', currval('users_id_seq'));

INSERT INTO phones (phone, user_id) VALUES
('79165554433', currval('users_id_seq'));

-- Пользователь 4
INSERT INTO users (name, date_of_birth, password) VALUES
('User4', '1988-11-30', '$2a$10$oRpmJVBVA1lYYotbvpJy0Otk1AJhm3dixblcZXlPWWSkDdaROyFJ2');

INSERT INTO accounts (balance, initial_deposit, user_id) VALUES
(3000.00, 3000.00, currval('users_id_seq'));

INSERT INTO emails (email, user_id) VALUES
('user4@gmail.com', currval('users_id_seq')),
('user4@mail.ru', currval('users_id_seq'));

INSERT INTO phones (phone, user_id) VALUES
('79168887766', currval('users_id_seq'));

-- Пользователь 5
INSERT INTO users (name, date_of_birth, password) VALUES
('User5', '1995-07-04', '$2a$10$FJ3AHk3/4r.Dm1o.r8NaDOb7rQ/uxt5wCmN0TBYjC0YLjCQXikEl2');

INSERT INTO accounts (balance, initial_deposit, user_id) VALUES
(1500.25, 1500.25, currval('users_id_seq'));

INSERT INTO emails (email, user_id) VALUES
('user5@gmail.com', currval('users_id_seq'));

INSERT INTO phones (phone, user_id) VALUES
('79162223344', currval('users_id_seq')),
('74951234567', currval('users_id_seq'));
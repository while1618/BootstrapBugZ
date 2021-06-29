-- schema
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
CREATE TABLE roles
(
    role_name VARCHAR PRIMARY KEY
);
CREATE TABLE users
(
    user_id    INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR NOT NULL,
    last_name  VARCHAR NOT NULL,
    username   VARCHAR NOT NULL,
    email      VARCHAR NOT NULL,
    password   VARCHAR NOT NULL,
    activated  BOOLEAN NOT NULL,
    non_locked BOOLEAN NOT NULL
);
CREATE TABLE user_roles
(
    user_id   INTEGER NOT NULL,
    role_name VARCHAR NOT NULL
);
ALTER TABLE user_roles
    ADD FOREIGN KEY (user_id) REFERENCES users (user_id);
ALTER TABLE user_roles
    ADD FOREIGN KEY (role_name) REFERENCES roles (role_name);
-- data
INSERT INTO roles (role_name)
VALUES ('USER');
INSERT INTO roles (role_name)
VALUES ('ADMIN');
-- password for users is "qwerty123"
INSERT INTO users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   activated,
                   non_locked)
VALUES ('Admin',
        'Admin',
        'admin',
        'admin@localhost.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true);
INSERT INTO users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   activated,
                   non_locked)
VALUES ('User',
        'User',
        'user',
        'user@localhost.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true);
INSERT INTO users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   activated,
                   non_locked)
VALUES ('Not Activated',
        'Not Activated',
        'notActivated',
        'notActivated@localhost.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        false,
        true);
INSERT INTO users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   activated,
                   non_locked)
VALUES ('Locked',
        'Locked',
        'locked',
        'locked@localhost.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        false);
INSERT INTO users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   activated,
                   non_locked)
VALUES ('For Update 1',
        'For Update 1',
        'forUpdate1',
        'forUpdate1@localhost.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true);
INSERT INTO users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   activated,
                   non_locked)
VALUES ('For Update 2',
        'For Update 2',
        'forUpdate2',
        'forUpdate2@localhost.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true);
INSERT INTO users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   activated,
                   non_locked)
VALUES ('For Update 3',
        'For Update 3',
        'forUpdate3',
        'forUpdate3@localhost.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true);
INSERT INTO user_roles (user_id, role_name)
VALUES (1, 'USER');
INSERT INTO user_roles (user_id, role_name)
VALUES (1, 'ADMIN');
INSERT INTO user_roles (user_id, role_name)
VALUES (2, 'USER');
INSERT INTO user_roles (user_id, role_name)
VALUES (3, 'USER');
INSERT INTO user_roles (user_id, role_name)
VALUES (4, 'USER');
INSERT INTO user_roles (user_id, role_name)
VALUES (5, 'USER');
INSERT INTO user_roles (user_id, role_name)
VALUES (6, 'USER');
INSERT INTO user_roles (user_id, role_name)
VALUES (7, 'USER');

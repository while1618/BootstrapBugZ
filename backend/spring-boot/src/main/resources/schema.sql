DROP TABLE IF EXISTS user_roles;

DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS roles;

CREATE TABLE roles
(
    role_id   serial PRIMARY KEY,
    role_name varchar(128) NOT NULL
);

CREATE TABLE users
(
    user_id    serial PRIMARY KEY,
    first_name varchar(128) NOT NULL,
    last_name  varchar(128) NOT NULL,
    username   varchar(128) NOT NULL,
    email      varchar(128) NOT NULL,
    password   varchar(128) NOT NULL,
    activated  boolean      NOT NULL,
    non_locked boolean      NOT NULL,
    created_at timestamp    NOT NULL
);

CREATE TABLE user_roles
(
    user_id integer NOT NULL,
    role_id integer NOT NULL
);

ALTER TABLE
    user_roles
    ADD
        FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE
    user_roles
    ADD
        FOREIGN KEY (role_id) REFERENCES roles (role_id);
drop table if exists user_roles;
drop table if exists users;
drop table if exists roles;
create table roles
(
    role_name VARCHAR(128) not null primary key
);
create table users
(
    user_id    INTEGER      not null primary key auto_increment,
    first_name VARCHAR(128) not null,
    last_name  VARCHAR(128) not null,
    username   VARCHAR(128) not null,
    email      VARCHAR(128) not null,
    password   VARCHAR(128) not null,
    activated  BOOLEAN      not null,
    non_locked BOOLEAN      not null
);
create table user_roles
(
    user_id   INTEGER      not null,
    role_name VARCHAR(128) not null
);
ALTER TABLE user_roles
    ADD FOREIGN KEY (user_id) REFERENCES users (user_id);
ALTER TABLE user_roles
    ADD FOREIGN KEY (role_name) REFERENCES roles (role_name);

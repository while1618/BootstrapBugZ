drop table if exists user_roles;
drop table if exists users;
drop table if exists roles;

create table roles
(
    role_id INTEGER not null primary key auto_increment,
    name    VARCHAR not null
);

insert into roles (name) values ('ROLE_USER');
insert into roles (name) values ('ROLE_ADMIN');

create table users
(
    user_id                    INTEGER   not null primary key auto_increment,
    first_name                 VARCHAR   not null,
    last_name                  VARCHAR   not null,
    username                   VARCHAR   not null,
    email                      VARCHAR   not null,
    password                   VARCHAR   not null,
    updated_at                 TIMESTAMP not null,
    logout_from_all_devices_at TIMESTAMP not null,
    activated                  BOOLEAN   not null,
    non_locked                 BOOLEAN   not null
);

-- password for users is "123"
insert into users (first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values ('Admin', 'Admin', 'admin', 'skill.potion21@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, true);
insert into users (first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values ('User', 'User', 'user', 'decrescendo807@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, true);
insert into users (first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values ('Not Activated', 'Not Activated', 'not_activated', 'marcellus.hts@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, false, true);
insert into users (first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values ('Locked', 'Locked', 'locked', 'uvazeni.potpukovnik.naucnik@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, false);

create table user_roles
(
    user_id INTEGER not null,
    role_id INTEGER not null
);

ALTER TABLE user_roles ADD FOREIGN KEY (user_id) REFERENCES users(user_id);
ALTER TABLE user_roles ADD FOREIGN KEY (role_id) REFERENCES roles(role_id);

insert into user_roles (user_id, role_id) values (1, 1);
insert into user_roles (user_id, role_id) values (1, 2);
insert into user_roles (user_id, role_id) values (2, 1);
insert into user_roles (user_id, role_id) values (3, 1);
insert into user_roles (user_id, role_id) values (4, 1);

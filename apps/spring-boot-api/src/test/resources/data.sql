drop table if exists user_roles;
drop table if exists users;
drop table if exists roles;
create table roles (
    role_name VARCHAR not null primary key
);
insert into roles (role_name)
values ('USER');
insert into roles (role_name)
values ('ADMIN');
create table users (
    user_id INTEGER not null primary key auto_increment,
    first_name VARCHAR not null,
    last_name VARCHAR not null,
    username VARCHAR not null,
    email VARCHAR not null,
    password VARCHAR not null,
    activated BOOLEAN not null,
    non_locked BOOLEAN not null
);
-- password for users is "qwerty123"
insert into users (
        first_name,
        last_name,
        username,
        email,
        password,
        activated,
        non_locked
    )
values (
        'Admin',
        'Admin',
        'admin',
        'skill.potion21@gmail.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true
    );
insert into users (
        first_name,
        last_name,
        username,
        email,
        password,
        activated,
        non_locked
    )
values (
        'User',
        'User',
        'user',
        'decrescendo807@gmail.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true
    );
insert into users (
        first_name,
        last_name,
        username,
        email,
        password,
        activated,
        non_locked
    )
values (
        'Not Activated',
        'Not Activated',
        'not_activated',
        'marcellus.hts@gmail.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        false,
        true
    );
insert into users (
        first_name,
        last_name,
        username,
        email,
        password,
        activated,
        non_locked
    )
values (
        'Locked',
        'Locked',
        'locked',
        'uvazeni.potpukovnik.naucnik@gmail.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        false
    );
create table user_roles (
    user_id INTEGER not null,
    role_name VARCHAR not null
);
ALTER TABLE user_roles
ADD FOREIGN KEY (user_id) REFERENCES users (user_id);
ALTER TABLE user_roles
ADD FOREIGN KEY (role_name) REFERENCES roles (role_name);
insert into user_roles (user_id, role_name)
values (1, 'USER');
insert into user_roles (user_id, role_name)
values (1, 'ADMIN');
insert into user_roles (user_id, role_name)
values (2, 'USER');
insert into user_roles (user_id, role_name)
values (3, 'USER');
insert into user_roles (user_id, role_name)
values (4, 'USER');

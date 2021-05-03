insert into roles (name)
values ('USER');
insert into roles (name)
values ('ADMIN');
-- password for user and admin is "qwerty123"
insert into users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   updated_at,
                   last_logout,
                   activated,
                   non_locked)
values ('Admin',
        'Admin',
        'admin',
        'skill.potion21@gmail.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        current_timestamp,
        current_timestamp,
        true,
        true);
insert into users (first_name,
                   last_name,
                   username,
                   email,
                   password,
                   updated_at,
                   last_logout,
                   activated,
                   non_locked)
values ('User',
        'User',
        'user',
        'decrescendo807@gmail.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        current_timestamp,
        current_timestamp,
        true,
        true);
insert into user_roles (user_id, role_id)
values (1, 1);
insert into user_roles (user_id, role_id)
values (1, 2);
insert into user_roles (user_id, role_id)
values (2, 1);
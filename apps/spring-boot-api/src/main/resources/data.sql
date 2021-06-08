insert into roles (role_name)
values ('USER');
insert into roles (role_name)
values ('ADMIN');
-- password for user and admin is "qwerty123"
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
insert into user_roles (user_id, role_name)
values (1, 'USER');
insert into user_roles (user_id, role_name)
values (1, 'ADMIN');
insert into user_roles (user_id, role_name)
values (2, 'USER');

INSERT INTO roles (role_name)
VALUES ('USER');
INSERT INTO roles (role_name)
VALUES ('ADMIN');
-- password for user and admin is "qwerty123"
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
        'skill.potion21@gmail.com',
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
        'decrescendo807@gmail.com',
        '$2a$10$b8zZNzhplNH37WyfR2kQ5uRr2U4ui3BAjjBQy4aNH.mh40Jj3cMV6',
        true,
        true);
INSERT INTO user_roles (user_id, role_name)
VALUES (1, 'USER');
INSERT INTO user_roles (user_id, role_name)
VALUES (1, 'ADMIN');
INSERT INTO user_roles (user_id, role_name)
VALUES (2, 'USER');

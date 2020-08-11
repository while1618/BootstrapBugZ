insert into roles (name) values ('ROLE_USER');
insert into roles (name) values ('ROLE_ADMIN');

-- password for user and admin is "123"
insert into users (first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values ('Admin', 'Admin', 'admin', 'skill.potion21@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, true);
insert into users (first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values ('User', 'User', 'user', 'decrescendo807@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, true);

insert into user_roles (user_id, role_id) values (1, 1);
insert into user_roles (user_id, role_id) values (1, 2);
insert into user_roles (user_id, role_id) values (2, 1);

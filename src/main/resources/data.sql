insert into roles (role_id, name) values (1, 'ROLE_USER');
insert into roles (role_id, name) values (2, 'ROLE_ADMIN');

-- password for user and admin is "123"
insert into users (user_id, first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values (1, 'Admin', 'Admin', 'admin', 'admin@localhost.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, true);
insert into users (user_id, first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values (2, 'User', 'User', 'user', 'user@localhost.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, true);

insert into user_roles (user_id, role_id) values (1, 1);
insert into user_roles (user_id, role_id) values (1, 2);
insert into user_roles (user_id, role_id) values (2, 1);

insert into roles (role_id, name) values (1, 'ROLE_USER');
insert into roles (role_id, name) values (2, 'ROLE_ADMIN');

insert into users (user_id, first_name, last_name, username, email, password, updated_at, logout_from_all_devices_at, activated, non_locked)
values (1, 'Dejan', 'Zdravkovic', 'while', 'bagzi1996@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', current_timestamp, current_timestamp, true, true);

insert into user_roles (user_id, role_id) values (1, 1);
insert into user_roles (user_id, role_id) values (1, 2);
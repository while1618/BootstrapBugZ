insert into roles (role_id, name) values (1, 'ROLE_USER');
insert into roles (role_id, name) values (2, 'ROLE_MODERATOR');
insert into roles (role_id, name) values (3, 'ROLE_ADMIN');

insert into users (user_id, first_name, last_name, username, email, password, activated) values (1, 'Dejan', 'Zdravkovic', 'while', 'bagzi1996@gmail.com', '$2a$10$/zJs.45ISa/1e5UOzxrUpuhhdheUJRZtNCzDYilIF9oJMyvwpVHre', true);

insert into user_roles (user_id, role_id) values (1, 1);
insert into user_roles (user_id, role_id) values (1, 2);
insert into user_roles (user_id, role_id) values (1, 3);
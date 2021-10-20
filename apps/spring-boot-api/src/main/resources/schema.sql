DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
CREATE TABLE roles
(
  role_name VARCHAR(128) PRIMARY KEY
);
CREATE TABLE users
(
  user_id    serial PRIMARY KEY,
  first_name VARCHAR(128) NOT NULL,
  last_name  VARCHAR(128) NOT NULL,
  username   VARCHAR(128) NOT NULL,
  email      VARCHAR(128) NOT NULL,
  password   VARCHAR(128) NOT NULL,
  activated  BOOLEAN      NOT NULL,
  non_locked BOOLEAN      NOT NULL
);
CREATE TABLE user_roles
(
  user_id   INTEGER      NOT NULL,
  role_name VARCHAR(128) NOT NULL
);
ALTER TABLE user_roles
  ADD FOREIGN KEY (user_id) REFERENCES users (user_id);
ALTER TABLE user_roles
  ADD FOREIGN KEY (role_name) REFERENCES roles (role_name);

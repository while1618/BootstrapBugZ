# BootstrapBugZ

### GitHub Actions

[![Spring Boot CI](https://github.com/while1618/BootstrapBugZ/actions/workflows/spring-boot.yml/badge.svg?branch=master)](https://github.com/while1618/BootstrapBugZ/actions/workflows/spring-boot.yml)
[![Svelte Kit CI](https://github.com/while1618/BootstrapBugZ/actions/workflows/svelte-kit.yml/badge.svg)](https://github.com/while1618/BootstrapBugZ/actions/workflows/svelte-kit.yml)

### Run

Just run docker-compose from the root directory:

```bash
docker-compose up --build -d
```

You can find the application on the following urls:

- ui: http://localhost:5173
- api: http://localhost:8080/v1/users
- api-docs: http://localhost:8080/swagger-ui/index.html

Login credentials:

- username: user/admin
- password: qwerty123


### Environment variables

| Variable                 | Description                                                                                               | Default          |
| ------------------------ | --------------------------------------------------------------------------------------------------------- | ---------------- |
| APP_NAME                 | Name of the app that will be used in the application                                                      | BootstrapBugZ    |
| HTTP_PROTOCOL            | HTTP protocol                                                                                             | http             |
| UI_HOST                  | UI Host                                                                                                   | localhost        |
| UI_PORT                  | Port on which you'll find UI running                                                                      | 5173             |
| API_PORT                 | Port on which you'll find API running                                                                     | 8080             |
| POSTGRES_HOST            | **Used only to override application.properties when starting locally.** Host on which postgres is running | localhost        |
| POSTGRES_PORT            | Port on which you'll find postgres running                                                                | 5432             |
| POSTGRES_DATABASE        | Name of the postgres database                                                                             | bootstrapbugz    |
| POSTGRES_USERNAME        | Postgres username                                                                                         | postgres         |
| POSTGRES_PASSWORD        | Postgres password                                                                                         | root             |
| REDIS_HOST               | **Used only to override application.properties when starting locally.** Host on which redis is running    | localhost        |
| REDIS_PORT               | Port on which you'll find redis running                                                                   | 6379             |
| REDIS_DATABASE           | Name of the redis database                                                                                | 0                |
| REDIS_PASSWORD           | Redis password                                                                                            | root             |
| MAIL_HOST                | SMTP host                                                                                                 | smtp.mailgun.org |
| MAIL_PORT                | SMTP port                                                                                                 | 587              |
| MAIL_USERNAME            | SMTP username                                                                                             | username         |
| MAIL_PASSWORD            | SMTP password                                                                                             | password         |
| JWT_SECRET               | Secret used to sign jwt                                                                                   | secret           |
| SPRING_SECURITY_PASSWORD | Password used for auto-generated users                                                                    | qwerty123        |


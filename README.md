# BootstrapBugZ

### GitHub Actions

[![Spring Boot CI](https://github.com/while1618/BootstrapBugZ/actions/workflows/spring-boot.yml/badge.svg?branch=master)](https://github.com/while1618/BootstrapBugZ/actions/workflows/spring-boot.yml)
[![Svelte Kit CI](https://github.com/while1618/BootstrapBugZ/actions/workflows/svelte-kit.yml/badge.svg)](https://github.com/while1618/BootstrapBugZ/actions/workflows/svelte-kit.yml)

### Run

To run the application via docker, you'll need to create `.env` file in `frontend/svelte-kit` with
the following data:

```bash
JWT_SECRET=secret
API_URL=http://bootstrapbugz-api:8080/v1
```

Then run docker-compose from the root directory:

```bash
docker-compose up --build -d
```

You can find the application on the following urls:

- frontend: http://localhost:5174
- api: http://localhost:8081/v1/users
- swagger-ui: http://localhost:8081/swagger-ui/index.html

Login credentials:

- username: user/admin
- password: qwerty123
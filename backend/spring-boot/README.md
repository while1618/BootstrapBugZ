# SpringBoot

## Running locally (dev)

### Requirements

Before running, you'll need to have the following software installed on your machine:
- java 21
- maven
- docker
- postgres (optional)
- redis (optional)

### Running

If you don't want to install postgres and redis locally, you can just use `docker-compose-db-dev.yml` to run them via docker. To start db, just run the following:
```bash
docker-compose -f docker-compose-db-dev.yml up -d
```

After db is up, run:
```bash
mvn clean install
```

And then, you can start the API via terminal:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Or just use intellij. Run config for intellij is already configured, so just start the `Application` config.

## Running with docker

### Requirements

Before running, you'll need to have the following software installed on your machine:
- docker

### Running

To start the API via docker you need to run:
```bash
docker-compose -f docker-compose-api-dev.yml up --build -d
```

You can find the API on the following urls:

- api: http://localhost:8080/v1/users
- api-docs: http://localhost:8080/swagger-ui/index.html
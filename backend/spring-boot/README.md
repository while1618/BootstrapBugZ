## 🖥️ Running the Backend

### 🧑‍💻 Running Locally

For the API to start properly, I'll need Postgres and Redis running. The easies way to that is via `docker-compose-db-dev.yml` file in the `backend` directory:

```bash
docker-compose -f docker-compose-db-dev.yml up -d
```

After the services are up and running, start the application:

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Alternatively, use IntelliJ IDEA. A pre-configured run configuration is available - just click and run! 🚀

### 🐳 Running via Docker

For running the API using Docker, execute:

```bash
docker-compose -f docker-compose-api-dev.yml up --build -d
```

💡 **Note**: Email functionalities require SMTP configuration. Refer to the Environment Variables section for setup.

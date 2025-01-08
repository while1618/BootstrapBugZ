## 🖥️ Running the Backend

### 🧑‍💻 Running Backend Locally

If you prefer not to install PostgreSQL and Redis locally, you can use the `docker-compose-db-dev.yml` file in the `backend` directory:

```bash
docker-compose -f docker-compose-db-dev.yml up -d
```

After the services are running, start the application:

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Alternatively, use IntelliJ IDEA. A pre-configured `Application` run configuration is available—just click and run! 🛠️

### 🐳 Running Backend via Docker

For running the backend API using Docker, execute:

```bash
docker-compose -f docker-compose-api-dev.yml up --build -d
```

💡 **Note**: Email functionalities require SMTP configuration. Refer to the Environment Variables section for setup.

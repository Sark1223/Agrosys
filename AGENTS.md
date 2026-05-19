# Agrosys

Multi-service Spring Boot microservices project with Docker. Agriculture digitization system.

## Architecture

```
agrosys-gateway:8080 -> agrosys-auth:8083, agrosys-web:8081, agrosys-plot:8084, 
                  agrosys-worker:8085, agrosys-transaction:8086, agrosys-task:8087

MySQL: mysql-auth (port 3307), mysql-agrosys (port 3308)
```

## Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| agrosys-gateway | 8080 | - | Spring Cloud Gateway |
| agrosys-auth | 8083 | mysql-auth | JWT authentication |
| agrosys-web | 8081 | - | Web frontend |
| agrosys-plot | 8084 | mysql-agrosys | Plot management |
| agrosys-worker | 8085 | mysql-agrosys | Background worker |
| agrosys-transaction | 8086 | mysql-agrosys | Transaction service |
| agrosys-task | 8087 | mysql-agrosys | Task service |

## Commands

```bash
# Start all services (development with hot reload)
docker-compose -f docker-compose.dev.yml up --build

# Rebuild single service
docker-compose -f docker-compose.dev.yml build agrosys-web
docker-compose -f docker-compose.dev.yml build --no-cache agrosys-auth  # No cache

# Start/stop
docker-compose -f docker-compose.dev.yml start   # Resume paused containers
docker-compose -f docker-compose.dev.yml stop    # Pause (faster restart)
docker-compose -f docker-compose.dev.yml down    # Stop and remove
```

## Tech Stack

- Java 21, Spring Boot 3.5.13, Spring Cloud Gateway 2025.0.1
- Maven, MySQL 8.0, Flyway (migrations in `src/main/resources/db`)
- JWT authentication via jjwt 0.11.5

## Development

- `docker-compose.dev.yml` uses `Dockerfile.dev` with volume mounts for hot reload
- Each service mounts its `src` directory - code changes reflect immediately
- Only rebuild with `--no-cache` when changing `pom.xml` dependencies
- Services wait for MySQL health checks before starting

## Environment

Required vars in `.env` (already configured):
- `DB_URL`, `DB_URL_AUTH`, `MYSQL_ROOT_PASSWORD`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (min 64 chars), `JWT_EXPIRATION`
- `GATEWAY_URL`

## Entry Points

- Main class: `src/main/java/com/agrosys/{service}/Application.java`
- Controllers: `src/main/java/com/agrosys/{service}/controller/`
- Entities: `src/main/java/com/agrosys/{service}/entity/`
- Repositories: `src/main/java/com/agrosys/{service}/repository/`
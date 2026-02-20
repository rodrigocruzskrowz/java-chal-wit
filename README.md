# WIT Java Challenge - Calculator

A distributed calculator application built with Spring Boot and Apache Kafka, composed of three modules:

- **common** — Shared DTOs (`CalcRequest`, `CalcResponse`) used by both modules
- **rest** — REST API that receives calculation requests and communicates with the calculator via Kafka
- **calculator** — Kafka listener that performs the arithmetic and returns the result

---

## Prerequisites

- [Java 25](https://openjdk.org/)
- [Maven 3.8+](https://maven.apache.org/)
- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) (both included in Docker Desktop)

---

## Building the Project

### 1. Install the common module

The `common` module must be installed first so the other modules can resolve it as a dependency:

```bash
cd common
mvn install
cd ..
```

### 2. Build the rest and calculator modules
note: Use the flag `-DskipTests` to skip tests.

```bash
cd rest
mvn clean package
cd ../calculator
mvn clean package
cd ..
```

---

## Running with Docker

### Start all services

```bash
docker-compose up --build
```

This will start:
- **Kafka** on port `9092`
- **rest** on port `8081`
- **calculator** on port `8082`

### Stop all services

```bash
docker-compose down
```

### Rebuild after code changes

```bash
cd <module>
mvn clean package
cd ..
docker-compose up --build
```

---

## API Endpoints

Base URL: `http://localhost:8081/api/v1/calc`

| Method | Endpoint         | Description          | Params       |
|--------|-----------------|----------------------|--------------|
| GET    | `/sum`          | Add two numbers      | `a`, `b`     |
| GET    | `/subtraction`  | Subtract two numbers | `a`, `b`     |
| GET    | `/multiplication` | Multiply two numbers | `a`, `b`   |
| GET    | `/division`     | Divide two numbers   | `a`, `b`     |

### Example Request

```bash
curl -v "http://localhost:8081/api/v1/calc/sum?a=1&b=2"
```

### Example Response

```json
{"result": 3}
```

---

## Running Tests

Run tests for a specific module:

```bash
cd rest
mvn test

cd calculator
mvn test
```

---

## Logging

Both modules use SLF4J with Logback. Logs include MDC fields for request tracing:

```
2026-02-20 10:00:01 [main] [uid=abc-123] [correlationId=xyz-456] wit.calc.rest.service.CalcService - ::::> Sending calculation request: 1 + 2
```

- **`uid`** — Unique identifier per calculation request, propagated across both modules via Kafka
- **`correlationId`** — Unique identifier per HTTP request, set by the REST module

Log files are written to the `logs/` directory inside each module and can be accessed via Docker logs:

```bash
docker exec rest cat /app/logs/app.log
```

To trace a specific request across both modules:

```bash
# Windows
docker logs rest | findstr "your-uid-here"

# Linux / Mac
docker logs rest | grep "your-uid-here"
```

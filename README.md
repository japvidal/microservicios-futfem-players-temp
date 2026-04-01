# microservicios-futfem-players-temp

`microservicios-futfem-players-temp` is the temporary player service used by the Tikitakas backend for staging, transitional, or imported footballer data. It complements the main `players` repository by offering a separate service boundary for records that should not yet be treated as canonical player entries.

The service is built with Java 21, Spring Boot, Spring Data JPA, MySQL, Springdoc OpenAPI, and Maven Wrapper. Like the rest of the domain services, it reuses the shared CRUD infrastructure provided by `microservicios-common`, registers in Eureka, and is exposed through the API gateway instead of being consumed directly by client applications.

Typical local execution:

```bash
./mvnw spring-boot:run
```

Gateway route:

- `/api/futfem/playerstemp/**`

The current `v0.1.0` release aligns this repository with the platform’s CI/CD standards: test profile support, Docker image generation, Jenkins pipeline automation, and OpenAPI server metadata suitable for gateway-based Swagger access.

This service is useful whenever the platform needs a safe place to ingest, review, or transform player-related data before promoting it into the main player catalog, reducing coupling between provisional workflows and the production dataset.

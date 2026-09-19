# Application services (M0-T4)

The four Java/Spring Boot services (HLD §4 System architecture). Each stage is a Kafka
consumer that reads a case event, does one job, writes its result, and emits the next event;
nothing calls the next stage directly (HLD §4). At this milestone each service is a skeleton:
it boots, answers `GET /health`, and runs as a container. Kafka consumers, domain logic and the
event helpers arrive in later milestones (M1+).

## What each service owns

| Service | Main class | Owns (HLD §4) |
| --- | --- | --- |
| `edge` | `com.titleengine.edge.EdgeApplication` | Auth, tenant routing, idempotent case creation, webhook delivery — never business logic. |
| `case` | `com.titleengine.casesvc.CaseApplication` | Case lifecycle, SLA clock, cost, append-only audit — never touches documents or portals. |
| `engine` | `com.titleengine.engine.EngineApplication` | Chain build, party resolution, rules-pack evaluation, findings — never signs anything. |
| `review` | `com.titleengine.review.ReviewApplication` | Human decisions, opinion drafting, delivery — overlays machine findings, never overwrites them. |

The package for the case service is `casesvc` because `case` is a Java reserved word.

## Ports

| Service | Port |
| --- | --- |
| `edge` | 8080 |
| `case` | 8081 |
| `engine` | 8082 |
| `review` | 8083 |

## Health endpoint

Every service answers:

```
GET /health  →  200  application/json
{"status":"UP","service":"<edge|case|engine|review>","version":"<from build; \"dev\" if unset>"}
```

`version` is the jar's `Implementation-Version` (stamped by the Boot jar); it is `"dev"` when
the service runs from classes (e.g. `bootRun` or a test). There is no auth on `/health` at this
milestone (auth is M1-T6 / M7-T1).

## Run one service locally

```bash
./gradlew :services:case:bootRun     # then: curl -s localhost:8081/health
./gradlew :services:edge:bootRun     # 8080
./gradlew :services:engine:bootRun   # 8082
./gradlew :services:review:bootRun   # 8083
```

## Build the images

Each service has a multi-stage `Dockerfile` whose **build context is the repo root** so the
Gradle build can see `libs/`:

```bash
docker build -f services/case/Dockerfile -t title-engine/case:dev .
```

The runtime image is `eclipse-temurin:21-jre`, runs as a non-root user, carries only the boot
jar, and declares a `HEALTHCHECK` against `/health`.

## Run the full stack (infra + services)

`docker-compose.services.yml` is an overlay on the infra file. `make up` brings up the
infra plus these four services (it runs both compose files); `make down` tears the whole
two-file stack down. The equivalent explicit commands are:

```bash
docker compose -f docker-compose.yml -f docker-compose.services.yml up -d --build --wait
docker compose -f docker-compose.yml -f docker-compose.services.yml down -v --remove-orphans
```

For infra-only work, run `docker compose -f docker-compose.yml up -d` directly (the base
file alone no longer passes `--wait`; see `docs/local-infra.md`).

Each service depends on `kafka`, `mongo`, `postgres` (`service_healthy`) and on `minio-init`
(`service_completed_successfully`). Nothing consumes these stores yet — the dependency graph is
the contract for later milestones. In containers, services reach Kafka at `kafka:19092`
(`KAFKA_BOOTSTRAP_SERVERS`); host clients use `127.0.0.1:9092` (see `docs/local-infra.md`).
Because these services now hold `docker compose up --wait` open, the M0-T2 `infra-ready` gate
was removed from the base file; the base file alone no longer passes `--wait` (see
`docs/local-infra.md`).

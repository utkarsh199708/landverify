# Review notes — recurring gotchas carried between reviews

The `reviewer` agent is read-only (no `Write`), so it cannot persist its own memory. The orchestrator copies anything a reviewer asks to remember into this file. Reviewers: read this before every review.

## Repo-wide

- **Windows exec bits.** Files committed from this machine default to mode `100644`. Any script or `gradlew` must be `100755` (`git ls-files -s <path>`). The first CI run failed on this (exit code 126).
- **Nested Gradle `include()`** always yields `:libs` and `:services` container nodes. A criterion saying "exactly N projects" paired with a mandated nested include is a brief contradiction, not a coder defect.
- **Agent memory directories** (`.claude/agent-memory/<agent>/`) are committed by design. Report as MINOR at most, unless they contain code or secrets.
- **`uv python install 3.12`** fails on this dev machine (no symlink privilege). Verify Python steps individually; CI runs on Ubuntu where it works.

## Local infra (M0-T2, M0-T4)

- **Kafka listeners.** Containers must bootstrap to `kafka:19092`; the host uses `127.0.0.1:9092`. A service using `kafka:9092` or `localhost:9092` from inside a container is a MAJOR.
- **`make up` drives the two-file stack** (`docker-compose.yml` + `docker-compose.services.yml`) after M0-T4. The base file alone exits non-zero under `--wait` because `minio-init` is a one-shot; that is expected and documented, not a defect.
- **Service jars** are copied by explicit name (`<svc>.jar`) and `/health` reports `version: "dev"` only because `project.version` is unspecified. If a task adds release versioning, check the Dockerfile `COPY` and the version fallback together.
- **MinIO images** come from `quay.io` (Docker Hub gates them); same pinned tags.
- **Criterion commands using `docker compose ps`** must use `-a` to see exited one-shot containers.

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

## Parallel waves and briefs

- **Gradle daemon contention.** With two worktrees building at once, `make lint test` can exit 2 with "Gradle build daemon has been stopped: stop command received" and no test or lint error. Re-run once before treating it as a failure.
- **Exact-copy interfaces can smuggle state tokens.** Statute names (e.g. "s.71A") never belong in `libs/domain` or engine code, even when HLD §3 lists them; the pack cites the statute. Institution names (SAR, DCLR, CO) and tenure vocabulary (raiyati, khas mahal) are allowed. If a brief pins such a string, flag it as a brief-level decision for the orchestrator.
- **Package-private helpers escape reflection scans** that only cover public types; when a structural test guards an invariant, check helpers by grep too.

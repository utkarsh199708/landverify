# Local infrastructure (M0-T2)

`docker-compose.yml` at the repo root brings up the five stores the platform needs
(HLD §9 Data & storage, §14 Tech stack). It is **infra only** — no application
services (those arrive as an overlay in M0-T4), no Kafka topics, no Postgres tables,
no OpenSearch index templates or analysis plugins.

## Quick start

```bash
cp .env.example .env          # optional: compose has ${VAR:-default} fallbacks
docker compose pull           # first pull is ~2 GB; do this before timing a start
make up                       # infra + the four services (both compose files), --wait
bash scripts/infra-smoke.sh   # one OK line per store; exit 0 iff all answer
make down                     # tears the whole two-file stack down, removes volumes
```

Since M0-T4, `make up` brings up the infra **plus** the four application services
(it runs both compose files, `docker-compose.yml` and `docker-compose.services.yml`).
For infra-only work, run `docker compose -f docker-compose.yml up -d` directly — but note
the base file alone no longer passes `--wait` (see the `minio-init` note below).

The **first** `make up` may exceed the wait timeout because it is pulling images
(~2 GB) and building the four service images. Run `docker compose pull` once first;
subsequent cold starts come up in ~10–15 s on a warm image cache.

> `.env` is git-ignored. `docker-compose.yml` reads every credential via
> `${VAR:-default}`, so the stack also runs with **no** `.env` at all (it uses the
> defaults below). Copy `.env.example` to `.env` only when you want to change them.

## Services, ports, credentials

All ports bind to `127.0.0.1` only — nothing is reachable off the loopback
interface. All containers share one Docker network named `title`. The Compose
project name is `title-engine`, so volumes are `title-engine_<name>`.

**Kafka has two bootstrap addresses.** Clients running in another container on the
`title` network connect to `kafka:19092` (advertised listener `PLAINTEXT`); the
`19092` port is internal to the network and not published. Clients on the developer
machine connect to `127.0.0.1:9092` (advertised listener `PLAINTEXT_HOST`, the only
published Kafka port). M0-T4 services must bootstrap to `kafka:19092`.

| Service | Role (HLD §9) | Image | Host port(s) | Credentials |
| --- | --- | --- | --- | --- |
| `kafka` | Event log (KRaft, single node, PLAINTEXT) | `apache/kafka:3.8.0` | `127.0.0.1:9092` (host) | none (PLAINTEXT) |
| `mongo` | Case DB | `mongo:7` | `127.0.0.1:27017` | none locally; db `$MONGO_INITDB_DATABASE` |
| `postgres` | Audit ledger (schema is M1-T4) | `postgres:16` | `127.0.0.1:5432` | `$POSTGRES_USER` / `$POSTGRES_PASSWORD`, db `$POSTGRES_DB` |
| `opensearch` | Name-resolution search index | `opensearchproject/opensearch:2.17.0` | `127.0.0.1:9200` | security plugin disabled (local only) |
| `minio` | S3-compatible object store (versioned) | `quay.io/minio/minio:RELEASE.2024-08-29T01-40-52Z` | `127.0.0.1:9000` (S3), `127.0.0.1:9001` (console) | `$MINIO_ROOT_USER` / `$MINIO_ROOT_PASSWORD` |
| `minio-init` | One-shot: creates bucket `$MINIO_BUCKET` with versioning, then exits 0 | `quay.io/minio/mc:RELEASE.2024-08-17T11-33-50Z` | — | uses MinIO root creds |

Credentials come from `.env` (see the template below), which the developer copies
from `.env.example`. Nothing is hard-coded in `docker-compose.yml` outside
`${VAR:-default}` references.

### `.env.example` contents

`.env.example` is committed at the repo root. Run `cp .env.example .env` (or rely on
the compose `${VAR:-default}` fallbacks). Its contents — matching the defaults:

```dotenv
POSTGRES_USER=title
POSTGRES_PASSWORD=title
POSTGRES_DB=ledger
MONGO_INITDB_DATABASE=cases
MINIO_ROOT_USER=minio
MINIO_ROOT_PASSWORD=minio12345
MINIO_BUCKET=documents
OPENSEARCH_JAVA_OPTS=-Xms512m -Xmx512m
```

## Health and the smoke test

Every long-running service has a healthcheck that exercises the **service**, not just
the TCP port (`interval 5s`, `timeout 5s`, `retries 18`, `start_period 20s`):

- kafka — `kafka-topics.sh --list` through the broker (host listener, `localhost:9092`)
- mongo — `db.runCommand({ ping: 1 })`
- postgres — `pg_isready -U $POSTGRES_USER -d $POSTGRES_DB`
- opensearch — `_cluster/health?wait_for_status=yellow`
- minio — `GET /minio/health/live`

`scripts/infra-smoke.sh` goes one step further and runs a real query against each
store (list topics, ping, `SELECT 1`, cluster status green|yellow, bucket exists),
printing one line per store and exiting non-zero — naming the store — on any failure.

## Notes / deliberate omissions

- **OpenSearch analysis plugins:** the ICU and phonetic analysers used for name
  resolution are **not** installed here. They come with M3-T3.
- **Kafka topics / Postgres tables / OpenSearch index templates:** created by later
  milestones (M1-T2, M1-T4, M3-T3). `auto.create.topics.enable=false` — nothing is
  auto-created. The Postgres init SQL under `infra/postgres/` creates the `ledger`
  database only; the append-only ledger schema and its trigger are M1-T4.
- **MinIO image registry:** Docker Hub now gates `minio/minio` behind auth (anonymous
  pulls return 401), so the images are pulled from `quay.io/minio/*` at the same
  pinned versions.
- **`minio-init` exits, so the base file alone no longer passes `--wait`:** this Compose
  build (v5.5.1) fails `docker compose up --wait` if *any* container has exited — even
  with code 0 — unless a still-running service depends on it via
  `service_completed_successfully`. M0-T2 held `make up` green with a throwaway
  `infra-ready` gate; **M0-T4 removed that gate** because the four application services in
  `docker-compose.services.yml` now depend on `minio-init` completing and hold `--wait`
  open themselves. Consequence: `docker compose up -d --wait` on the **base file alone**
  again exits non-zero once `minio-init` finishes (the containers still start correctly —
  only the `--wait` gate reports failure). For a full stack that passes `--wait`, use the
  two-file command:

  ```bash
  docker compose -f docker-compose.yml -f docker-compose.services.yml up -d --wait
  ```

  Because `minio-init` exits, `docker compose ps` (without `-a`) hides it — use
  `docker compose ps -a` to see it in `exited (0)`:

  ```bash
  docker compose ps -a --format json | \
    python -c "import sys,json; rows=[json.loads(l) for l in sys.stdin if l.strip()]; print({r['Service']: (r.get('State'), r.get('Health',''), r.get('ExitCode')) for r in rows})"
  ```

## Resetting state

```bash
# Stop everything and delete all data volumes and the network:
docker compose down -v --remove-orphans

# Verify nothing is left (expect 0):
docker volume ls -q | grep -c title-engine
```

`down -v` removes the containers, the `title` network, and the named volumes
(`title-engine_kafka-data`, `_mongo-data`, `_postgres-data`, `_opensearch-data`,
`_minio-data`). The next `make up` starts from a clean slate: fresh Kafka cluster,
empty databases, and a freshly created `$MINIO_BUCKET`.

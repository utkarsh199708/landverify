#!/usr/bin/env bash
# M0-T2 smoke test for local infra (docker-compose.yml).
# Exit 0 iff every store answers a real query:
#   kafka       lists topics
#   mongo       ping ok
#   postgres    SELECT 1
#   opensearch  cluster status green|yellow
#   minio       bucket $MINIO_BUCKET exists
# Prints one OK/FAIL line per store; on any failure exits non-zero and names the
# store(s) that failed (see brief acceptance criterion 5 — the negative test).
set -u -o pipefail

# On Git Bash (Windows) MSYS rewrites POSIX paths in argv (e.g. /opt/kafka/... or
# /bin/sh) into Windows paths before they reach `docker ... exec`. Disable that so
# the in-container paths pass through verbatim. Both vars are ignored on Linux CI.
export MSYS_NO_PATHCONV=1
export MSYS2_ARG_CONV_EXCL='*'

cd "$(dirname "$0")/.." || exit 2

# Read a key from .env if present, else fall back to the compose defaults (the same
# values documented in docs/local-infra.md). .env is not sourced (it may contain
# unquoted spaces, e.g. OPENSEARCH_JAVA_OPTS); each key is grepped individually.
env_val() {
  local key="$1" default="$2" line=""
  if [ -f .env ]; then
    line="$(grep -E "^${key}=" .env | head -1 | cut -d= -f2-)"
  fi
  if [ -n "$line" ]; then printf '%s' "$line"; else printf '%s' "$default"; fi
}

POSTGRES_USER="$(env_val POSTGRES_USER title)"
POSTGRES_DB="$(env_val POSTGRES_DB ledger)"
MINIO_ROOT_USER="$(env_val MINIO_ROOT_USER minio)"
MINIO_ROOT_PASSWORD="$(env_val MINIO_ROOT_PASSWORD minio12345)"
MINIO_BUCKET="$(env_val MINIO_BUCKET documents)"

failed=""
pass() { echo "OK:   $1"; }
fail() { echo "FAIL: $1"; failed="${failed} ${2}"; }

# kafka — list topics through the broker (proves the broker answers, not just a port).
if docker compose exec -T kafka /opt/kafka/bin/kafka-topics.sh \
      --bootstrap-server localhost:9092 --list >/dev/null 2>&1; then
  pass "kafka lists topics"
else
  fail "kafka did not list topics" kafka
fi

# mongo — ping.
if docker compose exec -T mongo mongosh --quiet \
      --eval "db.runCommand({ ping: 1 }).ok" 2>/dev/null | grep -q 1; then
  pass "mongo ping ok"
else
  fail "mongo ping failed" mongo
fi

# postgres — SELECT 1 as the configured user/db.
if docker compose exec -T postgres psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" \
      -tAc "SELECT 1" 2>/dev/null | grep -q 1; then
  pass "postgres SELECT 1"
else
  fail "postgres SELECT 1 failed" postgres
fi

# opensearch — cluster status must be green or yellow.
os_status="$(docker compose exec -T opensearch \
    curl -sf "http://localhost:9200/_cluster/health" 2>/dev/null \
    | grep -o '"status":"[a-z]*"' | cut -d'"' -f4)"
if [ "$os_status" = "green" ] || [ "$os_status" = "yellow" ]; then
  pass "opensearch status ${os_status}"
else
  fail "opensearch status not green|yellow (got '${os_status:-none}')" opensearch
fi

# minio — bucket exists. Uses a throwaway mc on the compose network (the minio
# server image has no mc; minio-init has exited). --no-deps avoids starting anything.
if docker compose run --rm --no-deps --entrypoint /bin/sh minio-init -c \
      "mc alias set local http://minio:9000 '$MINIO_ROOT_USER' '$MINIO_ROOT_PASSWORD' >/dev/null 2>&1 && mc ls 'local/$MINIO_BUCKET' >/dev/null 2>&1" >/dev/null 2>&1; then
  pass "minio bucket ${MINIO_BUCKET} exists"
else
  fail "minio bucket ${MINIO_BUCKET} missing/unreachable" minio
fi

if [ -n "$failed" ]; then
  echo "SMOKE FAILED:${failed}"
  exit 1
fi
echo "SMOKE OK: all five stores answered"
exit 0

#!/usr/bin/env bash
# Local mirror of .github/workflows/ci.yml: runs exactly the commands the three CI
# jobs run, in the same order, and exits non-zero on the first failure. Use this to
# reproduce a CI result before pushing (PLAN.md §1.6: nothing merges red).
#
# Requires java 21, uv and GNU make on PATH — same tools the Makefile needs.
set -euo pipefail

echo "== java job =="
./gradlew spotlessCheck checkstyleMain checkstyleTest test --no-daemon

echo "== python job =="
uv python install 3.12
uv sync --frozen
uv run ruff check . && uv run ruff format --check .
uv run pytest

echo "== make job =="
uv python install 3.12
make lint && make test

echo "== ci-local: all jobs passed =="

# Root build contract (CLAUDE.md "Commands"). Runs under GNU make in Git Bash on
# Windows and under bash on Ubuntu CI. Use ./gradlew and forward-slash paths only.
# `&&` (not `;`) so a failure in the first toolchain stops the target non-zero.
.PHONY: up down test lint fmt rules-test

# up/down drive the full two-file stack: infra (docker-compose.yml) plus the four
# application services (docker-compose.services.yml). The base file alone no longer
# passes --wait after M0-T4 removed the infra-ready gate; use it directly only for
# infra-only work (see docs/local-infra.md).
COMPOSE := docker compose -f docker-compose.yml -f docker-compose.services.yml

up:
	$(COMPOSE) up -d --build --wait --wait-timeout 300

down:
	$(COMPOSE) down -v --remove-orphans

test:
	./gradlew test && uv run pytest

lint:
	./gradlew spotlessCheck checkstyleMain checkstyleTest && uv run ruff check . && uv run ruff format --check .

fmt:
	./gradlew spotlessApply && uv run ruff format . && uv run ruff check --fix .

rules-test:
	@echo "not bootstrapped yet — see briefs/M2/M2-T3.md"; exit 1

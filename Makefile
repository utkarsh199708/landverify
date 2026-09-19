# Root build contract (CLAUDE.md "Commands"). Runs under GNU make in Git Bash on
# Windows and under bash on Ubuntu CI. Use ./gradlew and forward-slash paths only.
# `&&` (not `;`) so a failure in the first toolchain stops the target non-zero.
.PHONY: up down test lint fmt rules-test

up:
	docker compose up -d --wait --wait-timeout 90

down:
	docker compose down -v --remove-orphans

test:
	./gradlew test && uv run pytest

lint:
	./gradlew spotlessCheck checkstyleMain checkstyleTest && uv run ruff check . && uv run ruff format --check .

fmt:
	./gradlew spotlessApply && uv run ruff format . && uv run ruff check --fix .

rules-test:
	@echo "not bootstrapped yet — see briefs/M2/M2-T3.md"; exit 1

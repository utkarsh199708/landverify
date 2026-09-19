# Build & local development

This repo is a polyglot monorepo: Java services and libraries built with Gradle,
Python workers built with `uv`. One `Makefile` at the root drives both toolchains.

## Prerequisites

| Tool | Version | Notes |
| --- | --- | --- |
| JDK | 21 | The Gradle build pins a Java 21 toolchain; a different default JDK on PATH is fine as long as a 21 toolchain is discoverable. |
| GNU make | 4.x | Runs under Git Bash on Windows and bash on Ubuntu (CI). |
| `uv` | 0.6+ | Provisions Python and runs the workers' tests. |
| Docker + Compose | current | Only needed for `make up` / `make down` (local infra; see M0-T2). |

You do **not** need a system Python 3.12. `uv` installs it for you:

```
uv python install 3.12
```

The development machine may have Python 3.11 on PATH; the build does not use it —
`requires-python = ">=3.12"` makes `uv` select the 3.12 it manages.

The Gradle wrapper (`./gradlew`) downloads the pinned Gradle version on first use;
you do not need Gradle installed.

## The five targets

```
make up      # start local infra (Kafka, Mongo, Postgres, OpenSearch, MinIO), wait for health
make down    # stop local infra and remove volumes
make test    # ./gradlew test  &&  uv run pytest
make lint    # ./gradlew spotlessCheck checkstyleMain checkstyleTest  &&  uv run ruff check .  &&  uv run ruff format --check .
make fmt     # ./gradlew spotlessApply  &&  uv run ruff format .  &&  uv run ruff check --fix .
```

`make up` / `make down` require the compose file, which lands in M0-T2; until then
they will fail. Always run `make fmt` before committing.

## Running one module's tests

Java, a single module (Gradle project paths use `:`):

```
./gradlew :services:engine:test
./gradlew :libs:domain:test
```

Python, a single worker:

```
uv run pytest workers/docproc
uv run pytest workers/llm-gateway/tests/test_smoke.py::test_imports
```

## Java modules

Seven Gradle subprojects. Libraries are plain `java-library`; services are Spring
Boot 3.x and depend only on the shared libraries, never on each other.

```
libs:domain      com.titleengine.domain
libs:events      com.titleengine.events
libs:rules       com.titleengine.rules
services:edge    com.titleengine.edge
services:case    com.titleengine.casesvc   (not "case": Java reserved word)
services:engine  com.titleengine.engine    (also depends on libs:rules)
services:review  com.titleengine.review
```

The shared convention (Java 21 toolchain, JUnit 5, Spotless `googleJavaFormat`,
`-Werror -Xlint:all`, checkstyle) lives in the root `build.gradle.kts`; module
build files stay a few lines. Versions are pinned in `gradle/libs.versions.toml`.

## Python workers

A `uv` workspace rooted at `pyproject.toml` with two members under `workers/`.
`ruff` (line length 100, `py312`, rules `E,F,I,B,UP`) and `pytest` are shared dev
dependencies. `uv.lock` is committed. `uv run pytest` syncs the workspace (installing
both workers editable) and runs the suite.

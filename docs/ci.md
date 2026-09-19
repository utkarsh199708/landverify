# Continuous integration

Every push to `main` and every pull request targeting `main` runs
`.github/workflows/ci.yml`. No other triggers fire. The gate exists so that
`main` is always green (PLAN.md §1.6: "Nothing merges red.").

## What runs

Three jobs, all on `ubuntu-latest`, each capped at `timeout-minutes: 20`:

| Job | Commands | Purpose |
| --- | --- | --- |
| `java` | `./gradlew spotlessCheck checkstyleMain checkstyleTest test --no-daemon` | Java format, checkstyle and tests. |
| `python` | `uv python install 3.12`, `uv sync --frozen`, `uv run ruff check . && uv run ruff format --check .`, `uv run pytest` | Python 3.12 (HLD §14), lint, format check and tests. |
| `make` | `make lint && make test` | Exercises the `Makefile` itself so CI and `make` cannot drift. Runs after `java` and `python` pass (`needs: [java, python]`). |

The `java` and `python` jobs run the two toolchains directly for fast, cached
feedback. The `make` job re-runs the same work through the `Makefile`; if the
Makefile and the workflow ever disagree, that job goes red.

### Caching

- Gradle: `gradle/actions/setup-gradle@v4` caches the Gradle user home and build
  cache automatically.
- `uv`: `astral-sh/setup-uv@v3` with `enable-cache: true`, keyed on `uv.lock`
  (`cache-dependency-glob: uv.lock`).

A cache hit or miss changes only speed, never the result — `uv sync --frozen`
fails if `uv.lock` is out of date, and Gradle re-checks inputs.

### Security

- Workflow-level `permissions: { contents: read }` — the token can only read the
  repo.
- No secrets are referenced (there are none yet); `grep -c 'secrets\.'` on the
  workflow returns `0`.
- `concurrency` cancels superseded runs on the same ref.

## Run the same thing locally

```
bash scripts/ci-local.sh
```

This runs exactly the commands the three jobs run, in order, and exits non-zero
on the first failure. Run it before opening a PR to reproduce a CI result. It
needs the same tools CI uses: a Java 21 toolchain, `uv`, and GNU make on `PATH`.

## Reading a failure

1. Open the failed run in the GitHub Actions tab and find the red job.
2. The failing job names the toolchain:
   - `java` red → run `./gradlew spotlessCheck checkstyleMain checkstyleTest test`
     locally; `spotlessApply` (or `make fmt`) fixes format failures.
   - `python` red → run `uv run ruff check .`, `uv run ruff format --check .`,
     `uv run pytest`; `make fmt` fixes format failures.
   - `make` red while `java` and `python` are green → the `Makefile` has drifted
     from the workflow; reconcile the two.
3. Reproduce with `bash scripts/ci-local.sh`, fix, and push again.

## Dependency updates

`.github/dependabot.yml` opens weekly, grouped update PRs (max 5 open each) for
three ecosystems: `github-actions`, `gradle`, and `pip` (dependabot reads the uv
workspace's `pyproject.toml`).

## Not here yet

- Docker-in-CI for `make up` / compose `/health` checks — a later `compose` job
  (M0-T4).
- `make rules-test` — a commented placeholder sits in the `make` job, enabled by
  M2-T3.
- Dependency audit (M7-T7) and the eval-harness regression block (M4-T8).

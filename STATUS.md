# STATUS.md — where the build is

Updated by the orchestrator after every integrated wave. If this file disagrees with `git log`, git wins and this file is corrected first.

| Milestone | State | Waves done | Last integrated | Notes |
| --- | --- | --- | --- | --- |
| M0 Repo bootstrap | **done** 2026-09-19 | 4 | 09bb5ea (T1–T4) | exit: `make up && make lint && make test && make down` → 0 locally (9 containers, 22 gradle tasks, 2 pytest); CI green on main |
| M1 Domain + case service | not started | 0 | — | |
| M2 Rules engine + JH pack | not started | 0 | — | needs human legal sign-off before merge (PLAN §6) |
| M3 Title engine | not started | 0 | — | |
| M4 Doc processing + gateway | not started | 0 | — | |
| M5 Acquisition + runner | not started | 0 | — | |
| M6 Review console + report | not started | 0 | — | |
| M7 Edge, security, NFRs | not started | 0 | — | |
| M8 Pilot readiness | not started | 0 | — | |

## Task ledger

| Task | State | Branch | Reviewer verdict | Merged commit |
| --- | --- | --- | --- | --- |
| M0-T1 | merged 2026-09-19 | wave/M0-T1 | REWRITE BRIEF (brief defect only; code clean) → merged after brief reworded | 69c4d06 |
| M0-T2 | merged 2026-09-19 | wave/M0-T2 | SEND BACK (.env.example, Kafka listeners) → MERGE | a34dc29 |
| M0-T3 | merged 2026-09-19; CI green on main (run 35443289078) | wave/M0-T3 | MERGE | e1cdc01 + 153df0d |
| M0-T4 | merged 2026-09-19 | wave/M0-T4 | MERGE (+ Makefile follow-up) | 09bb5ea |

## Deferred MAJORs

| From task | Finding | Picked up in |
| --- | --- | --- |
| M0-T1 | `subprojects {}` in root `build.gradle.kts` also applies java-library/checkstyle/spotless to the `:libs` and `:services` container nodes; a stray `src/` under those roots would be silently built | M1-T1 brief: guard with a settings-level check or `configure(subprojects.filter { it.childProjects.isEmpty() })` |
| M0-T1 | JUnit/AssertJ/launcher versions pinned inline in root build, not in `gradle/libs.versions.toml`; version ownership split across two files | M1-T1 brief: move to catalog via `versionCatalogs` accessor |
| M0-T3 | CI `make` job runs `make lint && make test` without `uv sync --frozen`, so the lock-freshness guarantee of the python job is not reproduced where the Makefile is exercised | M2-T3 brief (it edits the `make` job to enable `make rules-test`) |
| M0-T4 | Root `.dockerignore` added outside brief scope (harmless; excludes only .git, .github, .claude, build dirs) | none needed; recorded so scope creep is visible |

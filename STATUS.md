# STATUS.md — where the build is

Updated by the orchestrator after every integrated wave. If this file disagrees with `git log`, git wins and this file is corrected first.

| Milestone | State | Waves done | Last integrated | Notes |
| --- | --- | --- | --- | --- |
| M0 Repo bootstrap | in progress | 0 | — | wave 1a = T1 running; T2 waits for Docker; wave 2 = T3+T4 |
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
| M0-T2 | brief ready; held until Docker is installed | | | |
| M0-T3 | brief ready (wave 2, after T1) | | | |
| M0-T4 | brief ready (wave 2, after T1+T2) | | | |

## Deferred MAJORs

| From task | Finding | Picked up in |
| --- | --- | --- |
| M0-T1 | `subprojects {}` in root `build.gradle.kts` also applies java-library/checkstyle/spotless to the `:libs` and `:services` container nodes; a stray `src/` under those roots would be silently built | M1-T1 brief: guard with a settings-level check or `configure(subprojects.filter { it.childProjects.isEmpty() })` |
| M0-T1 | JUnit/AssertJ/launcher versions pinned inline in root build, not in `gradle/libs.versions.toml`; version ownership split across two files | M1-T1 brief: move to catalog via `versionCatalogs` accessor |

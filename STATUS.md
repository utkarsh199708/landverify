# STATUS.md — where the build is

Updated by the orchestrator after every integrated wave. If this file disagrees with `git log`, git wins and this file is corrected first.

| Milestone | State | Waves done | Last integrated | Notes |
| --- | --- | --- | --- | --- |
| M0 Repo bootstrap | **done** 2026-09-19 | 4 | 09bb5ea (T1–T4) | exit: `make up && make lint && make test && make down` → 0 locally (9 containers, 22 gradle tasks, 2 pytest); CI green on main |
| M1 Domain + case service | paused (human stop 2026-09-19) | 1 | b348065 (T1) | T4, T2, T6 stopped mid-task, worktrees kept; on resume decide: resume each coder by branch, or delete worktrees and redispatch fresh |
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
| M1-T1 | merged 2026-09-19 | wave/M1-T1 | MERGE (2 majors fixed pre-merge) | 08e6d0a |
| M1-T4 | STOPPED by human 2026-09-19 mid-task; worktree `.claude/worktrees/agent-a9dd9baf7dcf62dc8` kept, 11 uncommitted files, 0 commits | wave/M1-T4 | | |
| M1-T2 | STOPPED by human 2026-09-19 mid-task; worktree `.claude/worktrees/agent-a311b6c9ee28af1f0` kept, 29 uncommitted files, 0 commits | wave/M1-T2 | | |
| M1-T5 | brief ready (wave 2, after T1+T4) | | | |
| M1-T6 | STOPPED by human 2026-09-19 mid-task; worktree `.claude/worktrees/agent-a1e89fb1440f2f79d` kept, 4 uncommitted files, 0 commits | wave/M1-T6 | | |

## Deferred MAJORs

| From task | Finding | Picked up in |
| --- | --- | --- |
| M0-T1 | `subprojects {}` applied conventions to container nodes | resolved in M1-T1 |
| M0-T1 | JUnit/AssertJ/launcher versions pinned inline in root build | resolved in M1-T1 |
| M0-T3 | CI `make` job runs `make lint && make test` without `uv sync --frozen`, so the lock-freshness guarantee of the python job is not reproduced where the Makefile is exercised | M2-T3 brief (it edits the `make` job to enable `make rules-test`) |
| M0-T4 | Root `.dockerignore` added outside brief scope (harmless; excludes only .git, .github, .claude, build dirs) | none needed; recorded so scope creep is visible |

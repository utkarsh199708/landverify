# CLAUDE.md — Land Title Diligence Engine (Jharkhand-first)

This file is loaded by the orchestrator and by every subagent. Keep it short; detail lives in `PLAN.md`, `docs/HLD.md` and the briefs.

## What this is

A pipeline that assembles a property's chain of title from public records and borrower documents, applies a state's land law as versioned rule packs, and produces an evidence-linked risk report that an advocate reviews and signs. Jharkhand is the first state. The full design is the HLD linked from `docs/HLD.md`; cite it by section number.

## How work happens here

- **Orchestrator:** Claude Fable in the main session. Writes briefs, dispatches, reviews, merges. Does not write production code.
- **Coders:** `coder` subagents (Opus 4.8), one brief each, in isolated worktrees on branches named `wave/<task-id>`.
- **Rules:** `rules-author` subagent writes rule packs under `packs/` only.
- **Review:** `reviewer` subagent, read-only, runs on every branch before merge.
- **Tests:** `test-runner` subagent runs suites and returns failures only.
- Protocol, wave rules and report formats: `PLAN.md` §2. Task list: `PLAN.md` §4. Current position: `STATUS.md`.

If you are a subagent: your brief is your contract. Touch only the files it names. If the brief and this file disagree, this file wins and you say so in `DEVIATIONS`.

## Non-negotiables (every PR is checked against these)

1. **Models never decide.** LLMs produce typed facts and draft prose. Severity, rule outcomes and party merges come from deterministic code and the rules engine. A model call in the engine's decision path is a blocker.
2. **No finding without evidence.** A `Finding` must carry evidence pointers (document id + page + region, or rule id + facts). The constructor rejects anything else; do not add a bypass.
3. **The engine knows no state.** No state name, statute name or state-specific constant in engine source. State law is data in `packs/<state>/`, versioned, with fixture tests. A lint test enforces this; do not weaken it.
4. **Category is a sourced, dated fact.** A party's category is stored only as read from a specific document at a specific date. It is never derived from a name or any other attribute. Unknown category is a blocker finding, not a default.
5. **One door to models.** Every model call goes through `workers/llm-gateway`. No other service reads a model API key. A test scans for this.
6. **Aadhaar is never stored.** Masked at extraction; the masked form is what is grounded and persisted. A store-scan test enforces this.
7. **The ledger is append-only.** Hash-chained rows, DB trigger denies update/delete. Replays create new report versions; nothing mutates a sealed report.
8. **Determinism.** Same `FactBundle` + same pack version → byte-identical findings. Sort everything; no wall-clock or random in the engine.

## Stack and layout

| Area | Choice | Path |
| --- | --- | --- |
| Services (case, engine, review, edge) | Java 21, Spring Boot 3.x, Gradle (Kotlin DSL) | `services/<name>/` |
| Shared libraries | Java | `libs/domain`, `libs/events`, `libs/rules` |
| Document processing, LLM gateway | Python 3.12, `uv`, `ruff`, `pytest` | `workers/docproc/`, `workers/llm-gateway/` |
| Rule packs | YAML + fixtures | `packs/base/`, `packs/jh/` |
| Web app (review, ops, lender portal) | React + TypeScript + Vite | `web/` |
| Local infra | docker-compose: Kafka (KRaft), MongoDB, Postgres, OpenSearch, MinIO | `docker-compose.yml` |
| Fixtures | synthetic cases, facts, golden documents | `fixtures/` |
| Briefs, decisions, status | Markdown | `briefs/`, `DECISIONS.md`, `STATUS.md`, `QUESTIONS.md` |

Not in the stack, by decision (HLD §14): graph database, general-purpose workflow orchestrator, real-time streaming analytics, general business-rules engine.

## Commands

```
make up        # start local infra, wait for health
make down
make test      # all suites: Gradle + pytest + rules fixtures
make lint      # spotless/checkstyle + ruff + rule-pack lint + "no state literal" check
make fmt       # format everything; run before committing
make rules-test PACK=jh VERSION=2026.09
```

Until M0-T1 lands, these targets are stubs that say so.

## Conventions

- **Branches:** `wave/<task-id>`. Commits reference the task id: `M1-T3: case state machine with SLA clock`.
- **Tests first for invariants.** Every non-negotiable touched by a task gets a negative test that fails without the guard.
- **Errors are typed.** No stringly-typed error handling across service boundaries.
- **Events are the only cross-stage call.** Stage A never calls stage B; it emits and B consumes.
- **Dates:** ISO-8601 with zone; SLA arithmetic in working days on the IST calendar; holiday list is data.
- **Money:** integer paise. Never floats.
- **Logs:** structured JSON, case id on every line, PII redacted at the sink.
- **No new dependency** without naming it in the report's `DEVIATIONS` with a one-line reason.
- **Comments explain why,** not what. Reference HLD sections where a design choice is non-obvious.

## When unsure

Do not guess and do not ask the human directly. Write the question to `QUESTIONS.md` under your task id, choose the conservative option, record it in your report's `DEVIATIONS`, and continue. The orchestrator decides or escalates.

## Definitely do not

- Edit files outside your brief's scope, "while you're there".
- Add a model call to make a decision the rules engine should make.
- Downgrade a blocker to a warning to make a test pass.
- Store, log or index an unmasked government ID number.
- Force-push, rewrite history, or merge to `main` yourself (subagents commit to their branch only).

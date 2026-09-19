# PLAN.md — Orchestrated build plan

**Project:** Land Title Diligence Engine, Jharkhand-first (design: `docs/HLD.md` → live doc)
**Orchestrator:** Claude Fable 5.1 in the main Claude Code session
**Coders / reviewers:** Claude Opus 4.8 subagents defined in `.claude/agents/`
**Last updated:** 2026-09-19

Read order at session start: `CLAUDE.md` → `PLAN.md` → the HLD (link in `docs/HLD.md`) → `STATUS.md` → `DECISIONS.md`. Nothing is dispatched before all five are in context.

---

## 0. Roles and models

| Role | Definition | Model | Runs as | Writes code? |
| --- | --- | --- | --- | --- |
| Orchestrator | main session | Fable (`claude --model fable`) | main conversation | **No.** Briefs, dispatch, review, integrate, decide |
| Coder | `.claude/agents/coder.md` | `claude-opus-4-8` | subagent, `isolation: worktree` | Yes — only files named in its brief |
| Rules author | `.claude/agents/rules-author.md` | `claude-opus-4-8` | subagent, `isolation: worktree` | Only `packs/**` YAML + fixtures |
| Reviewer | `.claude/agents/reviewer.md` | `claude-opus-4-8` | subagent, read-only | No — ranked findings only |
| Test runner | `.claude/agents/test-runner.md` | `haiku` | subagent, background | No — runs suites, returns failures only |
| Human | you | — | — | Decisions in §6 only |

**Why this split.** Fable holds the whole design in one context and makes every cross-cutting decision; that is the expensive, judgment-heavy work and it should not be spent on boilerplate. Opus 4.8 is the strongest generally available coding model, and each coder gets a fresh, narrow context: one brief, one worktree, one branch. The reviewer is a second Opus 4.8 instance that has not seen the code being written, so work is never grading itself. Haiku runs test suites so thousands of lines of Gradle and pytest output never enter Fable's context.

**Model notes.**
- `claude-opus-4-8` is pinned by full ID in each agent file. If the org allowlist blocks it, Claude Code substitutes and warns in the transcript. Acceptable fallbacks: `opus` (resolves to Opus 5) or `sonnet` (Sonnet 5). Never let coders fall back to Haiku.
- Start the main session with `claude --model fable`; `.claude/settings.json` also sets `"model": "fable"` as the project default. Fable may draw on usage credits depending on plan — check `/usage` before a long wave.
- Subagents inherit the main session's extended-thinking setting. Keep it on.
- `CLAUDE_CODE_MAX_SUBAGENT_SPAWN_DEPTH=1` in settings: coders cannot spawn their own subagents. The hierarchy is deliberately flat — one orchestrator, one layer of workers.

---

## 1. Operating principles (the orchestrator's contract)

1. **Fable does not write production code.** It writes briefs, reads reports, reads reviewer summaries, merges, and decides. If Fable finds itself editing a `.java` or `.py` file, stop and write a brief instead. The single exception: one-line fixes to merge conflicts in files it has already reviewed.
2. **One brief = one coder = one worktree = one branch.** Never two coders on the same file in the same wave.
3. **Acceptance tests are written into the brief before dispatch.** A coder cannot declare done without running them and quoting the result.
4. **The HLD is the spec.** If it is ambiguous, Fable writes the question to `QUESTIONS.md` and either takes the conservative reading and logs it in `DECISIONS.md`, or stops and asks the human when the item is in §6.
5. **Context hygiene.** Fable keeps summaries, never transcripts. Verbose output — test logs, large diffs, portal HTML — is read by a subagent that returns a summary.
6. **Main is always green.** Nothing merges red. Nothing merges without a reviewer pass.
7. **Every decision is written down.** `DECISIONS.md` is append-only, dated, one line of context, one line of decision.

---

## 2. The wave protocol

A milestone runs as one or more waves. A wave is a set of independent briefs dispatched in parallel.

```
 ┌─ PLAN (Fable) ──────────────────────────────────────────────────────────┐
 │ 1. pick next tasks: deps met, file sets disjoint, ≤ 4 per wave          │
 │ 2. write briefs/M<n>/<task-id>.md from briefs/TEMPLATE.md               │
 │ 3. git commit briefs to main   ◄── worktrees branch from the default    │
 │                                    branch, not from the working tree    │
 └───────────────────────────────────┬─────────────────────────────────────┘
                                     ▼
 ┌─ DISPATCH (parallel) ───────────────────────────────────────────────────┐
 │ Agent(coder,  "Implement briefs/M1/M1-T3.md. Report in the fixed       │
 │                format.")  × N                                           │
 └───────────────────────────────────┬─────────────────────────────────────┘
                                     ▼
 ┌─ REVIEW (parallel, one reviewer per coder result) ──────────────────────┐
 │ Agent(reviewer, "Review branch wave/M1-T3 against briefs/M1/M1-T3.md   │
 │                  and the non-negotiables in CLAUDE.md.")                │
 └───────────────────────────────────┬─────────────────────────────────────┘
                                     ▼
 ┌─ DECIDE (Fable) ────────────────────────────────────────────────────────┐
 │ no blockers → merge                                                     │
 │ blockers    → SendMessage the same coder with the findings (resume)     │
 │ brief wrong → rewrite brief, redispatch fresh                           │
 └───────────────────────────────────┬─────────────────────────────────────┘
                                     ▼
 ┌─ INTEGRATE (Fable + test-runner) ───────────────────────────────────────┐
 │ git merge wave/* → main · Agent(test-runner, "make test")               │
 │ red → fix brief → coder · green → commit · update STATUS.md             │
 └─────────────────────────────────────────────────────────────────────────┘
```

**Rules of the wave**

- **Commit to main before dispatching.** A subagent worktree branches from the default branch, not from the session's uncommitted tree. Uncommitted briefs are invisible to coders.
- **Cap: 4 coders per wave.** The settings cap is 6 so reviewers and the test runner can run alongside. Integration cost grows faster than parallelism gains beyond four.
- **Resume beats redispatch.** A coder that missed something is sent the reviewer's findings with `SendMessage` and continues in its own worktree with its own context. A fresh coder is only for a brief that was wrong.
- **The report is the interface.** Fable reads a coder's report and a reviewer's findings, not the coder's transcript. A report missing a section is sent back.
- **Merge on "no blockers", not "no findings".** Majors go into the next brief for that area; minors are logged.
- **Name the coder.** Pass `name: "<task-id>"` on the Agent call so it can be resumed by name.

**Coder report format** (fixed; the coder prompt enforces it):

```
BRIEF:        <task-id>
BRANCH:       wave/<task-id>
STATUS:       done | partial | blocked
FILES:        <one path per line>
TESTS:        <exact command> → <passed>/<failed>/<skipped>
ACCEPTANCE:   <each criterion from the brief → MET | NOT MET, with the evidence line>
DEVIATIONS:   <anything done differently from the brief, and why; "none" if none>
QUESTIONS:    <numbered; "none" if none>
```

**Reviewer output format** (fixed):

```
BRANCH:    wave/<task-id>
VERDICT:   MERGE | SEND BACK | REWRITE BRIEF
BLOCKERS:  <numbered; file:line; what breaks; "none" if none>
MAJORS:    <numbered>
MINORS:    <numbered>
INVARIANTS CHECKED: <which CLAUDE.md non-negotiables were exercised and how>
```

---

## 3. Brief format

Every brief is a copy of `briefs/TEMPLATE.md` with all sections filled. Summary of the contract:

| Section | Must contain |
| --- | --- |
| Goal | One sentence. What exists after this task that did not before |
| HLD refs | Section numbers, and the specific table rows or invariants that apply |
| In scope / out of scope | Explicit file paths or globs the coder may touch; everything else is out |
| Interfaces | Exact signatures, schemas, event names, topic names — copied, not paraphrased |
| Acceptance | Numbered, each verifiable by a command or a test name |
| Tests to write | Named test cases the coder must add, including at least one negative case per invariant |
| Dependencies | Other task IDs that must be merged first |
| Notes for the reviewer | What to look at hardest |

A brief that cannot state its acceptance criteria as commands is not ready to dispatch.

---

## 4. Milestones

Order is fixed. A milestone starts only when the previous one's exit criterion is met and recorded in `STATUS.md`.

| # | Milestone | Waves (est.) | Exit criterion |
| --- | --- | --- | --- |
| M0 | Repo bootstrap | 1 | `make up && make test` green locally and in CI |
| M1 | Domain model + case service | 2 | `POST /cases` → events → ledger rows; replay yields a new version; every transition audited |
| M2 | Rules engine + Jharkhand pack v0 | 2 | All pack fixtures pass; engine source has no state literal; legal sign-off logged |
| M3 | Title engine | 2 | 20 synthetic cases → expected findings; byte-identical output on rerun |
| M4 | Document processing + LLM gateway | 3 | Eval harness runs; ungrounded fields fail the gate; per-doc cost emitted |
| M5 | Acquisition + runner tasks | 2 | Adapters have `health()` + canary; runner tasks round-trip through Blocked |
| M6 | Review console + report | 3 | End-to-end demo: create → Delivered with the advocate loop in the UI |
| M7 | Edge, security, NFRs | 2 | Every row of HLD §11 has a passing test or a recorded measurement |
| M8 | Pilot readiness | 1 | Failure drills from HLD §12 pass; runbooks reviewed; demo runs clean twice |

### M0 — Repo bootstrap

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M0-T1 | Monorepo layout + build tooling: Gradle multi-project (`services/{edge,case,engine,review}`, `libs/{domain,events,rules}`), Python workspace with `uv` (`workers/{docproc,llm-gateway}`), `Makefile` targets `up down test lint fmt` | — | `make lint && make test` runs both toolchains and passes on an empty tree |
| M0-T2 | Local infra: `docker-compose.yml` with Kafka (KRaft), MongoDB, Postgres, OpenSearch, MinIO; healthchecks; `make up` blocks until healthy | — | `make up` exits 0 within 90 s on a cold start |
| M0-T3 | CI: GitHub Actions running `make lint test` for Java and Python with dependency caching | M0-T1 | Green run on `main` |
| M0-T4 | Skeleton services: each service boots, serves `/health`, has one contract test; shared event envelope stub in `libs/events` | M0-T1, M0-T2 | All four `/health` endpoints return 200 under compose |

### M1 — Domain model + case service

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M1-T1 | Domain model (`libs/domain`): every entity in HLD §3 as immutable types with JSON Schema export; `Finding` constructor rejects empty evidence pointers | M0 | Schema files generated; negative test for evidence-less `Finding` |
| M1-T2 | Event envelope + topics (`libs/events`): the eight events in HLD §5, JSON Schemas in repo, producer/consumer helpers with idempotency keys | M1-T1 | Round-trip test per event; duplicate delivery is a no-op |
| M1-T3 | Case state machine: eight states, transitions only on events, SLA clock in working days (IST calendar, holiday list as data), `Blocked` carries reason + owner | M1-T2 | Illegal transition test per state; SLA test across a weekend and a holiday |
| M1-T4 | Audit ledger: Postgres append-only table, hash chain (`prev_hash`, `row_hash`), DB trigger denying `UPDATE`/`DELETE`, `ledger verify` CLI | M0-T2 | Tamper test: modified row fails verify; update attempt raises |
| M1-T5 | Cost ledger: per-case line items (model, OCR, transcription, portal fee, runner fee), summary endpoint | M1-T1 | Sum matches line items to the paisa |
| M1-T6 | Tenancy + RBAC: tenant id on every record, per-tenant key abstraction (file-backed in dev), roles `lender-ops advocate runner transcriber ops admin` | M1-T1 | Cross-tenant read test fails; role matrix test |
| M1-T7 | Replay: re-emit from any stage event for a case → new report version; old version untouched | M1-T3, M1-T4 | Replay test asserts two versions, first byte-identical to before |

### M2 — Rules engine + Jharkhand pack v0

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M2-T1 | Expression language (`libs/rules`): grammar for boolean/comparison/`in`/`is null`/whitelisted functions such as `category_at(date)`, `same_police_station(a, b)`; parser + evaluator; no arbitrary code paths | M1-T1 | Fuzz test: no input reaches eval of anything outside the whitelist |
| M2-T2 | Rule schema + loader: YAML shape from HLD §7; versioned packs; base pack extended by state packs; unknown fields rejected; lint test asserts no state literal in engine source | M2-T1 | Loader rejects a rule with an unknown key; lint test present and passing |
| M2-T3 | Fixture runner: `rules test --pack <state> --version <v>` runs every rule's `tests:`; wired into `make test` | M2-T2 | A deliberately failing fixture fails the build |
| M2-T4 | Base pack (national families: transfer, registration, limitation, succession) with fixtures — **rules-author** | M2-T3 | All fixtures pass |
| M2-T5 | Jharkhand pack, transferability family, with fixtures — **rules-author**; every rule carries a citation and a `verify:` instruction | M2-T4 | All fixtures pass; §6 escalation: human legal review before merge |
| M2-T6 | Jharkhand pack, remaining families (form, capacity, succession, encumbrance/litigation, flats, coverage) — **rules-author** | M2-T5 | All fixtures pass; human legal review logged |

### M3 — Title engine

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M3-T1 | `FactBundle` input contract from document processing; fixtures in `fixtures/facts/` | M1-T1 | Schema validated; three sample bundles load |
| M3-T2 | Transferability gate (HLD §6) — runs before chain build; blocker findings carry rule id + evidence | M2-T2, M3-T1 | Gate-fail case produces no chain and one blocker |
| M3-T3 | Party + category resolution: normalisation, clustering with confidence, OpenSearch index with ICU + phonetic analysers; uncertain merges become findings; category is a dated fact with a source document — a test asserts it is never derived from a name | M3-T1 | Merge/split fixture set passes; the "never derived from name" test exists and passes |
| M3-T4 | Chain build backwards from current record to root-of-title; inferred and customary succession links flagged | M3-T3 | Golden chains reconstructed for 10 fixtures |
| M3-T5 | Link validation: the seven checks in HLD §6, each a pure function with a negative fixture | M3-T4 | 7 × negative fixtures fail as expected |
| M3-T6 | Gap detection with hypothesis + verification instruction | M3-T4 | Gap fixtures produce the expected reason text |
| M3-T7 | Overlays: encumbrance + litigation matching; low-confidence hits always surfaced | M3-T3 | Low-confidence hit appears in findings with its score |
| M3-T8 | Findings + ordinal score; 20 synthetic golden cases under `fixtures/cases/jh/`; determinism test | M3-T2…T7 | 20/20 expected outputs; rerun is byte-identical |

### M4 — Document processing + LLM gateway (Python)

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M4-T1 | LLM gateway service: prompt registry (prompt + schema + model, immutable once tagged), structured output, provider abstraction with failover, PII redaction in logs, per-case cost events; a repo test asserts no other service reads a model API key | M0 | Failover test; key-isolation test |
| M4-T2 | Classifier: document type + script detection; eval harness entry | M4-T1 | ≥ target on synthetic set; confusion matrix emitted |
| M4-T3 | OCR adapter: pluggable backend, dewarp + denoise pre-pass, layout preserved | M0 | Golden page round-trips with bounding boxes |
| M4-T4 | Extraction: one JSON Schema per document type (start with the eight highest-volume types listed in HLD §8), per-field confidence | M4-T1, M4-T3 | Each schema has a fixture and a failing negative |
| M4-T5 | Grounding: every value → OCR span → page + bbox; ungrounded values fail | M4-T4 | Injected hallucination fixture is rejected |
| M4-T6 | Gate: per-field-class thresholds as config; below-gate routing to review | M4-T5 | Threshold change alters routing in test |
| M4-T7 | Transcription intake for non-OCR scripts: skip OCR → task → `Document(source=human)`; two-transcriber diff | M1-T1 | Disagreement fixture produces a finding |
| M4-T8 | Eval harness: golden-set format, field-level precision/recall per prompt version, CI regression block on names/dates/numbers | M4-T2…T6 | Harness runs in CI; a regressed prompt fails the build |

### M5 — Acquisition + runner tasks

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M5-T1 | Adapter interface + registry: `fetch(parcel) → documents[]`, `health()`, fee reporting; hourly canary scheduler | M1-T2 | Mock adapter passes contract tests |
| M5-T2 | Jharbhoomi adapter (headless browser, session pool, captcha-fallback hook) | M5-T1 | Recorded-fixture tests; health canary |
| M5-T3 | Bhu-Naksha adapter | M5-T1 | Recorded-fixture tests |
| M5-T4 | eCourts adapter (CNR-keyed API) with name-variant fan-out | M5-T1, M3-T3 | Variant fan-out test; order PDF stored |
| M5-T5 | CERSAI adapter behind the interface (stub until credentials) | M5-T1 | Contract tests against stub |
| M5-T6 | Runner task service: task types (NEC, khatian copy, revenue-register check, CO order), SLA, per-district capacity, structured return forms → Document ingestion, photo upload | M1-T3 | Task round-trip moves case Blocked → InReview |
| M5-T7 | Acquisition orchestrator: fan-out per state pack, timeouts, `acquisition.complete`, Blocked wiring | M5-T1…T6 | Timeout path and happy path both emit the right events |

### M6 — Review console + report

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M6-T1 | Web app scaffold (React + TypeScript + Vite), role-based views, auth against edge | M1-T6 | Login as each role shows the right shell |
| M6-T2 | Queue view: SLA ordering, state-licence filter | M6-T1 | Sort/filter tests |
| M6-T3 | Chain view: timeline of links, gaps as red spans with hypothesis | M3-T8 | Renders all 20 golden cases |
| M6-T4 | Finding card + evidence pane: page render with bbox highlight | M4-T5 | Click finding → highlighted region |
| M6-T5 | Decisions: accept / override-with-reason / verify → ledger rows; verification task creation | M1-T4, M5-T6 | Every decision appears in the ledger |
| M6-T6 | Opinion draft assembly via gateway from accepted findings in the lender's template; editable | M4-T1 | Draft contains only accepted findings |
| M6-T7 | Report: PDF + JSON, hash seal → ledger, e-sign adapter interface (DSC / eSign) | M6-T6 | Hash on ledger matches file |
| M6-T8 | Delivery: webhook with retry, email fallback, ledger event | M1-T2 | Failed webhook → retry → fallback path tested |

### M7 — Edge, security, NFRs

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M7-T1 | Lender API (OpenAPI), idempotent case creation, per-tenant API keys, webhooks | M1 | Idempotency test; OpenAPI lint |
| M7-T2 | PII: Aadhaar masked at extraction and never persisted (test scans all stores), PAN encrypted, contact details excluded from the index | M4-T4, M1-T6 | Store scan test finds zero Aadhaar patterns |
| M7-T3 | Per-tenant encryption keys + crypto-shred deletion flow | M1-T6 | Post-shred read fails; ledger still verifies |
| M7-T4 | Observability: OpenTelemetry traces per case id across all services; metrics for gate pass rate, override rate per rule, adapter health, runner ageing, cost per case | all | One trace spans a full case in the demo |
| M7-T5 | SLA alerts (day 2, day 3; 24 h and 40 h for no-physical-search cases) | M1-T3 | Alert fires in a clock-advanced test |
| M7-T6 | Load test at 300 cases/month equivalent burst; bottlenecks recorded in `docs/perf.md` | M6 | Report committed |
| M7-T7 | Security pass: vault abstraction for secrets, dependency audit in CI, authz tests per role | M7-T1 | Audit job green; authz matrix green |

### M8 — Pilot readiness

| Task | Title | Depends | Acceptance (summary) |
| --- | --- | --- | --- |
| M8-T1 | Failure drills from HLD §12 as scripted scenarios (portal down, ledger tamper, model outage, runner backlog) | M7 | Each drill has a runbook and a passing script |
| M8-T2 | Runbooks for ops console actions; seed data; demo script | M7 | Demo runs clean twice from a fresh compose |
| M8-T3 | Docs: API reference, state-pack authoring guide (HLD §13), rules authoring guide | M7 | Reviewed by the human |

---

## 5. Definition of done (every task)

- Acceptance criteria in the brief: all `MET`, with evidence.
- Tests added: named in the brief, present, passing, and at least one negative test per invariant touched.
- `make lint test` green on the branch.
- No files touched outside the brief's scope. (Reviewer checks `git diff --stat main...wave/<id>`.)
- No new dependency without a line in the report's `DEVIATIONS`.
- Reviewer verdict `MERGE`, or `SEND BACK` resolved.
- Merged to main; `STATUS.md` row updated; commit message references the task id.

---

## 6. Escalate to the human (stop and ask)

Fable stops and asks — with the decision framed and a recommendation — for exactly these:

1. Any rule in the Jharkhand pack before it merges (M2-T5, M2-T6): the human records legal sign-off in `DECISIONS.md`.
2. Any change to a non-negotiable in `CLAUDE.md`.
3. Introducing a paid external service, credential, or portal automation against terms of use.
4. Schema changes to the audit ledger or event envelope after M1 is closed.
5. Anything listed in HLD §16 / `QUESTIONS.md` that blocks a brief.
6. A milestone exit criterion that cannot be met as written.

Everything else Fable decides and logs.

---

## 7. Cost and context controls

| Lever | Setting | Why |
| --- | --- | --- |
| Coder effort | `high` | Correctness over speed on implementation |
| Reviewer effort | `high` | Reviews are cheap relative to a bad merge |
| Test runner | `haiku`, effort `low`, `background: true`, `omitClaudeMd: true` | Pure log-reading; no design context needed |
| Coder `maxTurns` | 200 | Long enough for a real task; partial output is resumable |
| Reviewer `maxTurns` | 80 | Bounded read-only pass |
| Coders per wave | ≤ 4 | Integration cost |
| Nesting | depth 1 | Flat hierarchy; Fable is the only orchestrator |
| Fable's own context | summaries only; `/compact` between milestones | Long sessions degrade otherwise |
| Prompt cache | keep the session's default; consider `experimental.cacheTtl: 1h` on `coder` for long waves | Cheaper resumed coders |

---

## 8. Starting and resuming

**First session:** paste `KICKOFF.md` → *Start*.
**Any later session:** paste `KICKOFF.md` → *Resume*.
**Between milestones:** run `/compact` with the guide text in `KICKOFF.md`, then continue.

`STATUS.md` is the only file that says where the build is. If it disagrees with the git log, the git log wins and `STATUS.md` is corrected first.

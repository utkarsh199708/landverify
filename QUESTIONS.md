# QUESTIONS.md — open questions, by task

Subagents append here instead of asking. The orchestrator answers inline (`→ answer, date`) or escalates per PLAN.md §6. Resolved items move to DECISIONS.md.

## From the HLD (§16) — human decisions still open

- [ ] Who signs: own advocate panel vs lender's empanelled advocates on our console.
- [ ] Software licence per case vs managed service owning the runner network.
- [ ] NEC access: what the registration portal exposes online, and for which years.
- [ ] Category evidence standard agreed with the pilot lender.
- [ ] Revenue-court coverage: which districts have online status, which need physical register search.
- [ ] Jamshedpur company-area sub-lease process, obtained from the lessee's records office.
- [ ] Transcription labelled set (~300 records, two-transcriber agreement) — who produces it.
- [ ] First lender.

## From tasks

<!-- <TASK-ID>: question → answer -->

- M0 (orchestrator, 2026-09-19): The development machine has no JDK, no GNU make, no `uv`, no Docker and no WSL (checked 2026-09-19). Coders cannot run `make lint test` or `make up`, so no M0 acceptance criterion can be verified locally. Escalated under PLAN §6.6. → resolved 2026-09-19: human installed Temurin JDK 21, GNU make 4.4.1, uv 0.12.17, Docker Desktop 4.91 (WSL2 backend; needed the Virtual Machine Platform feature and a reboot).
- M0-T3 (orchestrator, 2026-09-19): No GitHub remote exists. "Green run on main" needs an org/repo. → answered 2026-09-19: `https://github.com/utkarsh199708/landverify.git`, added as `origin`.
- M0-T2 (coder, 2026-09-19): `.env.example` is in the brief scope, but the permission config denies `Read(./.env.*)`, which also blocks Write/Bash creation of that path (`.env.example` matches `.env.*`). I could not create `.env.example` nor run the literal `cp .env.example .env`. Conservative choice: `docker-compose.yml` is fully functional with `${VAR:-default}` defaults so `make up`/`docker compose` work with no `.env`, and the exact `.env.example` contents are documented in `docs/local-infra.md`. → resolved 2026-09-19: main narrowed the deny to `.env` / `.env.local` / `.env.*.local` and un-ignored `.env.example`; after `git merge main` the template `.env.example` was created and committed on `wave/M0-T2`.
- M1-T2 (orchestrator, 2026-09-19): HLD §9 "compacted topics per case" conflicts with replay (compaction drops older events per key). Implemented as one non-compacted topic per event type, keyed by case id, 90-day retention, archived to the object store later. → *human: confirm, or say if per-case topics were intended.*

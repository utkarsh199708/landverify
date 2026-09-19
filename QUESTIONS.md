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

- M0 (orchestrator, 2026-09-19): The development machine has no JDK, no GNU make, no `uv`, no Docker and no WSL (checked 2026-09-19). Coders cannot run `make lint test` or `make up`, so no M0 acceptance criterion can be verified locally. Escalated under PLAN §6.6. → *pending human: install JDK 21, GNU make, uv, Docker Desktop (needs WSL2), or point the build at another machine.*
- M0-T3 (orchestrator, 2026-09-19): No GitHub remote exists. "Green run on main" needs an org/repo. → answered 2026-09-19: `https://github.com/utkarsh199708/landverify.git`, added as `origin`.
- M0-T2 (coder, 2026-09-19): `.env.example` is in the brief scope, but the permission config denies `Read(./.env.*)`, which also blocks Write/Bash creation of that path (`.env.example` matches `.env.*`). I could not create `.env.example` nor run the literal `cp .env.example .env`. Conservative choice: `docker-compose.yml` is fully functional with `${VAR:-default}` defaults so `make up`/`docker compose` work with no `.env`, and the exact `.env.example` contents are documented in `docs/local-infra.md`. → *pending human: narrow the deny rule so a template like `.env.example` is allowed (e.g. deny `Read(./.env)` and `Read(./.env.*.local)` instead of `Read(./.env.*)`), then commit the template.*

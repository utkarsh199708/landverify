# DECISIONS.md — append-only

Format: `YYYY-MM-DD · <who: orchestrator|human> · <context in one line> → <decision in one line>`

- 2026-09-19 · human · Orchestration model → Fable orchestrates; Opus 4.8 codes and reviews; Haiku runs tests; flat hierarchy (spawn depth 1).
- 2026-09-19 · human · First state → Jharkhand, Chotanagpur divisions; Santhal Pargana detect-and-block in v1 (HLD §1, §15).
- 2026-09-19 · human · Unknown holder category → blocker finding, never a warning (HLD §6, §16).
- 2026-09-19 · human · Non-OCR scripts → human transcription path, not OCR (HLD §8).
- 2026-09-19 · orchestrator · HLD is a Claude Docs page, not a file → exported to `docs/HLD.full.md` (cache) and summarised in `docs/HLD-summary.md`; the doc stays the source of truth.
- 2026-09-19 · orchestrator · M0 tasks have dependencies (T3←T1; T4←T1,T2) → M0 runs as two waves: wave 1 = T1+T2, wave 2 = T3+T4.
- 2026-09-19 · orchestrator · M0-T1 and M0-T2 both need `make up` → Makefile is owned by T1 (`up` = `docker compose up -d --wait --wait-timeout 90`); T2 owns only compose + infra files.
- 2026-09-19 · orchestrator · M0-T4 needs service containers but T2 owns `docker-compose.yml` → services go in an overlay `docker-compose.services.yml`.
- 2026-09-19 · orchestrator · Event envelope shape is needed before M1 → fixed in briefs/M0/M0-T4.md (`EventEnvelope` record: eventId, eventType, tenantId, caseId, producer, occurredAt, idempotencyKey, payloadHash, payloadJson); M1-T2 builds on it, changes after M1 closes are a PLAN §6.4 escalation.
- 2026-09-19 · orchestrator · Dev machine has Python 3.11 only → workers require 3.12 and `uv` installs it; no build depends on system Python.
- 2026-09-19 · human · GitHub remote → `https://github.com/utkarsh199708/landverify.git` (`origin`); `main` is pushed after every integrated wave.
- 2026-09-19 · orchestrator · Git on this machine has `core.autocrlf=true` → repo sets `core.autocrlf=false` locally and M0-T1 adds `.gitattributes` with `eol=lf` for scripts and `gradlew`.

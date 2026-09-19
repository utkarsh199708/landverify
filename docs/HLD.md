# HLD — Land Title Diligence Engine (Jharkhand-first)

The authoritative High-Level Design lives as a living document here:

**https://claude.ai/code/artifact/daae30c8-8204-45f9-9c86-d604cace531c**

## How to use it from Claude Code

1. **Read it directly.** Claude Code's `Artifact` tool can read a claude.ai artifact link. The orchestrator's first action in any session is to read the HLD from that URL and keep a section-by-section summary in `docs/HLD-summary.md` (generated, not hand-written).
2. **Or vendor a copy.** Open the doc, use its export action (Markdown), and save the result as `docs/HLD.full.md`. Commit it. Re-export whenever the doc changes; the doc is the source of truth, the file is a cache.

## Section map (for brief references)

Briefs and code comments cite the HLD by section number. The numbering is stable:

| § | Title | What briefs draw from it |
| --- | --- | --- |
| 1 | Purpose & scope | v0 boundaries, success metrics |
| 2 | Actors & external systems | Adapter list, runner network, transcriber role |
| 3 | Domain model | Entity definitions and invariants (TitleLink, Chain, Gap, Finding, Party) |
| 4 | System architecture | The six layers and what each never does |
| 5 | End-to-end flow | Eight case states, trigger events, SLAs |
| 6 | Title engine | Transferability gate → resolution → chain build → link validation → gaps → overlays → score |
| 7 | State rules pack | Rule YAML shape, expression language, rule families, authoring workflow |
| 8 | Document & LLM layer | Classify → OCR → extract → ground → gate; gateway; evals; transcription path |
| 9 | Data & storage | Five stores, tenancy, PII handling |
| 10 | Human review workflow | Review console loop and elements |
| 11 | Non-functional requirements | Targets every milestone exit is checked against |
| 12 | Failure modes & mitigations | Drill list for M8 |
| 13 | Adding a state | The four-package extensibility contract |
| 14 | Tech stack | Choices and what is deliberately excluded |
| 15 | Phasing | v0 / v1 / v2 |
| 16 | Open questions | Human decisions; mirrored in `QUESTIONS.md` |

## Non-negotiables lifted from the HLD

These are restated in `CLAUDE.md` so every subagent sees them without reading the whole design:

- The rules engine is deterministic code over typed facts. Models produce facts and draft prose; they never decide a finding's severity, a rule outcome, or a party merge.
- A `Finding` without evidence pointers cannot be persisted.
- The engine's source contains no state name. State-specific law lives in versioned rule packs (data), tests included.
- A party's category is a dated fact tied to a source document; it is never inferred.
- All model calls go through the LLM gateway; no other service holds a model API key.
- Aadhaar numbers are never stored, in any form.
- The audit ledger is append-only and hash-chained; replays produce new report versions, never mutations.

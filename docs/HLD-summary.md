# HLD summary (generated from the live doc, 2026-09-19)

Source: https://claude.ai/code/artifact/daae30c8-8204-45f9-9c86-d604cace531c — vendored copy in `docs/HLD.full.md`. This file is what the orchestrator re-reads after `/compact`. Cite sections by number.

## Section summaries

**§1 Purpose & scope.** A lender sends a property and borrower; the system builds the chain of title from Jharkhand revenue records (khatian, Register-II), the registration record (deeds, NEC) and borrower uploads, and returns an evidence-linked *risk report* (not a title guarantee) signed by an advocate in 3–5 working days. The first question is transferability under the CNT Act (kism of the land, category of every holder), examined before ownership. v0 = Chotanagpur divisions (Ranchi, Jamshedpur, Dhanbad, Bokaro, Hazaribagh), urban residential resale on raiyati / khas mahal / municipal land plus Jamshedpur sub-lease; lender-initiated only. Out: Santhal Pargana (SPT s.20, detect-and-block in v1), agricultural, bhuinhari, Mundari khuntkatti, gair mazarua (detect and block), consumer checks, title insurance, physical site inspection. Success: 80% under 5 working days (48 h with no physical search), zero untraceable findings, zero CNT transferability findings overturned, advocate under 45 min per case.

**§2 Actors & external systems.** People: lender ops (LOS REST + webhook or portal), reviewing advocate (console), runner per district (task app, structured return forms, photo upload), Kaithi/Urdu transcriber (console; output is a Document with source = human), internal ops (ops console + alerts), rules author (versioned config repo), admin. External sources: Jharbhoomi (Register-II, mutation, lagan), Bhu-Naksha, CS/RS khatian (Record Room via runner, Kaithi/Urdu), Jharnibandhan / DSR (deeds, index, NEC; physical for older years), Circle Office (mutation orders), DC office SAR/DCLR courts (revenue litigation, s.71A; not on eCourts; highest-risk blind spot), eCourts/NJDG (CNR-keyed API), municipal bodies, Tata Steel / UISL (Jamshedpur sub-lease consent), CERSAI (paid API), society/builder documents (untrusted until cross-checked). The runner network is the primary acquisition channel, modelled as tasks with SLAs, return forms, per-district capacity, and fees on the cost ledger from day one.

**§3 Domain model.** Centre is the TitleLink (one transfer, backed by documents, from parties to parties); a Chain is an ordered list of links; every Finding attaches to a link, document or parcel. Case owns everything. Party is separate from any document (same person spelled five ways over 30 years). Gap is a first-class object with a reason and hypothesis. Category and kism are stored as dated, sourced facts because a well-formed sale can still be void for who the seller was or what the land was.

**§4 System architecture.** Six layers over Kafka: Edge (auth, tenant routing, idempotent case creation, webhooks; no business logic), Case service (state machine, SLA, cost, audit ledger; never touches documents or portals), Acquisition (adapters, runner queue, portal fee ledger; never interprets content), Document processing (classify, OCR, extract, ground, gate; never decides legal meaning), Title engine (chain, party resolution, rules pack, findings; never signs), Review & report (console, opinion, delivery; overlays, never overwrites machine findings). Each stage consumes an event, does one job, emits the next; nothing calls the next stage directly. Rules pack is data inside the engine; the LLM gateway is the only place model calls happen.

**§5 End-to-end flow.** Eight states, forward only on events; Blocked is the only state that waits on the outside world and carries reason + owner; it is the only stage allowed to breach the 5-working-day SLA. Every transition is appended to the audit ledger with actor, timestamp, payload hash. Replay from `acquisition.complete` with a newer model produces a new report version, never a mutation.

**§6 Title engine.** Deterministic code over typed facts; LLMs produce facts and draft rationale text, never severity. Pipeline: transferability gate (kism from khatian + Register-II, category of current holder; gair mazarua aam, bhuinhari, Mundari khuntkatti, Santhal Pargana raiyati → blocker; ST-held raiyati → blocker unless every transfer has DC prior sanction and stayed within the police-station area) → party + category resolution (normalise script, honorifics, initials, father's name; cluster with confidence; category attached only as a dated fact from a document, never surname; uncertain merges and unknown categories become findings) → chain build backwards from Register-II through registered instruments and mutation orders to the RS/CS khatian entry (root of title); succession links inferred and marked; ST holders: Hindu Succession Act excluded by s.2(2), link marked customary → link validation (seven checks) → gap detection (interval with no valid link, or chain short of 30 years; carries hypothesis + verification instruction) → overlays (encumbrances from IGR index, CERSAI, recitals, marked released/live; litigation matched by party cluster and parcel, scored, always shown even at low confidence) → score: ordinal Clear / Clear with conditions / Defective / Not marketable from the highest-severity open finding plus counts; no numeric blend.

**§7 State rules pack.** Each state's law is a versioned data package authored by a lawyer, reviewed like code, evaluated by the engine; engine source never contains a state name. Fixed rule shape (below). `when` is a small expression language, no arbitrary code. Seven Jharkhand families, ~86 rules estimated. Authoring: lawyer drafts + tests → second lawyer reviews → engineer confirms compile + tests → version tagged → engine loads by state + version → every finding records the pack version. Pack changes never rewrite findings; rerun produces a new report version. Uniform national law (TPA, Registration Act, Limitation Act, succession statutes) is a base pack every state pack extends.

**§8 Document & LLM layer.** File in, typed fields out, each with confidence and a page + region pointer. Classify (~25 doc types + language + script) → OCR (layout-aware Devanagari/English; dewarp + denoise for old scans; Kaithi and Urdu skip OCR and go to a transcriber; transcript is a Document with source = human) → Extract (one JSON schema per doc type, structured output, per-field confidence) → Ground (every value matched back to OCR span or transcript line → page + bbox) → Gate (per-field-class thresholds; names, dates, category, kism strict; ungrounded values fail regardless of confidence). LLM gateway: single service owning prompt registry (prompt + schema + model version, immutable once tagged), PII-redacted logging, per-case token cost, retries, second-provider failover; no other service holds a model key. Evals: golden set per doc type per state (target 200 each), field-level precision/recall per prompt version; ship only if no regression on party names, dates, registration numbers. LLM may write: gated fields, draft rationale, first draft of opinion. Never a severity, rule outcome or party merge. Cost target under ₹150 model + OCR per case.

**§9 Data & storage.** Object store (S3-compatible, versioned, India region; raw files, OCR output, reports; case lifetime + 8 years then crypto-shred). Case DB MongoDB (Case, Parcel, Document metadata + fields, Party, TitleLink, Finding, Report). OpenSearch (party name variants with phonetic + transliteration analysers; parcel identifiers; rebuildable). Kafka (compacted per-case topics, every stage event with payload hash; 90 days hot then archived). Audit ledger: append-only Postgres with hash chain (every transition, decision, seal; permanent). Tenancy: tenant id on every record + per-tenant PII encryption key; name index shared across tenants (names only). PII: Aadhaar never stored (masked at extraction, masked form grounded); PAN encrypted, shown only to the advocate; borrower contacts only in the case record, never indexed.

**§10 Human review workflow.** One screen (finding left, evidence right), one loop (accept / override with reason / verify). Every decision is a ledger row; overrides feed evals and rule-change candidates. Elements: queue by SLA remaining with assignment by state licence and workload; chain view (30-year timeline, gaps as red spans with hypotheses); finding card (severity, rule, editable rationale draft, verification instruction, evidence links; severity change needs a reason); evidence pane (page with grounded region highlighted, OCR text alongside); verification task (structured request to runner or lender, returns as a Document, re-triggers the engine); opinion draft (from accepted findings in the lender's template; e-sign Aadhaar-based or DSC; signed PDF hash goes on the ledger). Findings sorted by severity, duplicates collapsed; Clear cases get a shortened checklist.

**§11 Non-functional requirements.** Extraction ≥ 98% precision / ≥ 95% recall on names, dates, khata, plot, kism, category. 100% finding traceability enforced at schema level. Turnaround 80% < 5 wd, < 48 h without physical search, 100% < 10 wd excluding Blocked. Throughput 300 cases/month launch, 3,000/month end of year one. Availability 99.5% lender API. Security: India region, encryption at rest and in transit, per-tenant PII keys, RBAC, portal credentials in a vault. DPDP: purpose limitation, consent referenced on case, deletion = crypto-shred of the tenant key, no training on borrower data, caste category stored only as sourced dated fact. Auditability: reconstruct what the advocate saw at signing. Cost per report under ₹900 all-in. Observability: per-stage latency, gate pass rate, override rate per rule, adapter health, runner ageing per district, cost per case. Non-goal: real time.

**§12 Failure modes & mitigations.** Category misdetermined (never inferred; unknown = blocker; advocate confirms on every CNT link). Revenue-court proceeding missed (runner DC register check mandatory for raiyati land; report states registers searched and date). Transcription error (double transcription; disagreement = finding). Register-II lags mutation (both shown; finding asks advocate which controls). Jamshedpur consent missing (required document; blocker). Portal down / captcha / UI change (hourly canary, schema validation, backoff, ops alert, runner task after 12 h, adapters versioned and hot-swappable). OCR misread (strict gates; cross-document disagreement is a finding). Party over-merge (uncertain merges emitted as findings; reversible and logged). Party under-merge (analysers tuned; overrides feed the index). Hallucination (ungrounded fails gate). Stale rule (override rate per rule; monthly legal review; pack version on every finding). Litigation false negative (low-confidence always shown; search under every variant; report states parameters). Records wrong (record-vs-reality findings). Backlog (overflow panel, second runner, shortened checklist). Lender drift (versioned API, email fallback, delivery is a ledger event). Model outage (second provider; provider-neutral prompts). Liability handled by positioning, contract, insurance, not code. M8 drills: portal down, ledger tamper, model outage, runner backlog.

**§13 Adding a state.** Four packages against fixed interfaces: Adapters (`fetch(parcel) → documents[]`, `health()`, fee reporting, runner task templates), Document schemas (one per doc type, mapped to common model), Rules pack (delta over base, with tests), Golden set (~200 labelled docs per major type), plus Script pack if a new script. Target 8–10 weeks. Order after Jharkhand: Bihar (strict subset of JH rules), then UP or West Bengal, then western states. Runner network, lender empanelment and advocate panels do not port.

**§14 Tech stack.** Java 21 + Spring Boot (case, engine, review, edge); Python (doc workers); Kafka; MongoDB case DB; Postgres append-only hash-chained ledger; OpenSearch with ICU + phonetic; S3-compatible object store; in-house rules evaluator over a small expression language with YAML rules in git; layout-aware OCR with Kaithi/Urdu via human transcription; structured-output LLMs behind the gateway with second provider; headless-browser portal workers with session pools and human-solved captcha fallback; single web app with role views; Kubernetes in India region, vault, per-tenant KMS; OpenTelemetry traces per case id. Excluded: graph DB, workflow orchestrator, real-time streaming analytics, general BRE (Drools-style).

**§15 Phasing.** v0 (months 0–6): Jharkhand Chotanagpur, urban resale, 1–2 lenders, 60–100 cases/month; exit 80% < 5 wd, advocate < 45 min, zero overturned transferability findings, override rate < 15% on names/dates/category. v1 (6–12): Santhal Pargana detect-and-block, Bihar, LAP + balance transfer, 4+ lenders, ~1,000/month, Clear cases pass with shortened checklist; exit cost < ₹900, Bihar live in 10 weeks. v2 (year 2): UP or WB, one western state, agricultural in Bihar, developer diligence, continuous monitoring of the lender's live book (the reason replay is built in from day one).

**§16 Open questions.** Who signs; software vs service; NEC access; category evidence standard; revenue-court coverage; Jamshedpur company area; Kaithi labelled set; first lender. Mirrored in `QUESTIONS.md`.

## Invariants checklist (from §4, §6, §7, §8, §9, §11; restated in CLAUDE.md)

- [ ] Models never decide: severity, rule outcome and party merge come only from deterministic code + rules engine (§6, §8)
- [ ] A Finding without evidence pointers cannot be persisted; enforced at schema/constructor level (§3, §11)
- [ ] Engine source contains no state name; state law is data in versioned packs with fixture tests (§7)
- [ ] Category is a dated fact from a specific document; never derived from name; unknown = blocker (§3, §6, §12)
- [ ] Kism is a title fact, not metadata; decides which rules apply before the chain is built (§3, §6)
- [ ] All model calls go through the LLM gateway; no other service holds a model key (§8)
- [ ] Prompt registry entries are immutable once tagged (§8)
- [ ] Ungrounded extracted values fail the gate regardless of confidence (§8)
- [ ] Aadhaar never stored; masked at extraction; PAN encrypted, advocate-only; contacts never indexed (§9)
- [ ] Audit ledger append-only, hash-chained; update/delete denied (§9)
- [ ] Replay produces a new report version; sealed reports are never mutated (§5, §7)
- [ ] Determinism: same FactBundle + same pack version → byte-identical findings (CLAUDE.md #8)
- [ ] Stages communicate only by events; no stage calls the next directly (§4)
- [ ] Review overlays machine findings, never overwrites them (§4)
- [ ] Every finding records the pack version that produced it (§7)
- [ ] Documents are immutable once ingested (§3)
- [ ] Low-confidence litigation hits are always surfaced (§6, §12)
- [ ] Uncertain party merges become findings; merges are reversible and logged (§6, §12)
- [ ] Blocked is the only state that may breach the SLA; it carries reason + owner (§5)
- [ ] Tenant id on every record; per-tenant PII key; only names cross tenants via the index (§9)
- [ ] Money in integer paise; dates ISO-8601 with zone; SLA in IST working days with holiday list as data (CLAUDE.md)
- [ ] Score is ordinal only, never a blended number (§6)

## Exact names checklist

**Case states (§5, eight):** `Received`, `Acquiring`, `Processing`, `Analysing`, `InReview`, `Blocked`, `Signed`, `Delivered`.

**Transitions (§5):** Received→Acquiring (parcel resolved); Acquiring→Processing (all sources returned or timed out); Processing→Analysing (extraction gated); Analysing→InReview (findings emitted); InReview→Blocked (needs runner or lender input); Blocked→InReview (input received); InReview→Signed (advocate signs); Signed→Delivered (report + JSON pushed).

**Events (§5, eight):** `case.created`, `parcel.resolved`, `acquisition.complete`, `docs.gated`, `findings.emitted`, `review.needs_input`, `opinion.signed`, `report.sealed`.

**Entities (§3):** `Case`, `Parcel`, `Document`, `Party`, `TitleLink`, `Chain`, `Encumbrance`, `LitigationHit`, `Finding`, `Report`. Plus `Gap` (first-class object on Chain).

**Entity fields (§3):**
- Case: tenant, lender ref, parcel, borrower parties, status, SLA clock, cost ledger
- Parcel: district, anchal, mauza, thana number, khata number, plot (khesra) number, kism, CS and RS khatian references, flat or holding number, area, ULPIN (optional)
- Kism values: `raiyati`, `khas mahal`, `gair mazarua khas`, `gair mazarua aam`, `bhuinhari`, `khuntkatti`
- Document: type, source (`portal` / `upload` / `runner` / `transcription`), raw file, pages, script, extracted fields (with confidence + bounding boxes), trust level
- Party: canonical name, name variants, role, category (`ST` / `SC` / `BC` / `General`) as dated fact with source, ID hints (PAN masked), relationships
- TitleLink: from-parties, to-parties, instrument type (`sale`, `gift`, `release`, `partition`, `succession`, `court decree`, `settlement`, `sub-lease assignment`), date, registration ref, stamp paid, consideration, DC sanction reference (s.46 / s.49), mutation (dakhil-kharij) status, supporting document ids, validation status
- Chain: ordered links, root-of-title document, coverage window (30 years), gaps
- Encumbrance: type (`mortgage`, `charge`, `lis pendens`, `lease`, `s.71A restoration claim`, `SAR proceeding`), holder, amount, source, released?
- LitigationHit: CNR or revenue case number, forum (`civil court` / `SAR` / `DCLR` / `CO`), parties matched, match confidence, stage, relevance
- Finding: severity (`blocker` / `major` / `minor` / `info`), rule id, subject (link / doc / parcel), evidence pointers, machine rationale, reviewer decision, verification instruction
- Report: version, findings, chain summary, advocate opinion, signature, delivered-to

**Rule fields (§7, in order):** `id`, `state`, `version`, `applies_to`, `when`, `severity`, `title`, `citation`, `rationale`, `verify`, `tests`. Rule id pattern example: `JH-CNT-046-01`; test id pattern `JH-CNT-046-01-pass-1` / `-fail-1`. Pack version example: `2026.09`.

**Expression language examples (§7):** `parcel.kism == "raiyati"`, `parcel.division == "chotanagpur"`, `from_party.category_at(link.date) == "ST"`, `link.instrument in [...]`, `link.dc_sanction is null`, `same_police_station(from_party, to_party)`, `and` / `or` / `not`, `!=`.

**Rule families (§7):** Transferability (~25), Form & registration (~15), Capacity & authority (~12), Succession (~10), Encumbrance & litigation (~10), Flats (~8), Coverage (~6).

**Link validation checks (§6, seven):** Transferability, Competence, Capacity, Form, Timing, Consideration, Recitals.

**Score values (§6):** `Clear`, `Clear with conditions`, `Defective`, `Not marketable`.

**Review decisions (§10):** accept, override with reason, verify.

**Document pipeline steps (§8):** Classify, OCR, Extract, Ground, Gate. Scripts: Devanagari, English, Kaithi, Urdu. Gate outcomes: pass / review / reject.

**Document types named (§8):** CS/RS khatian, Register-II extract, lagan receipt, mutation order, Bhu-Naksha extract, sale deed, NEC, caste certificate, DC sanction order, Tata sub-lease or consent, holding tax receipt (~25 total).

**Roles (§2, CLAUDE.md):** `lender-ops`, `advocate`, `runner`, `transcriber`, `ops`, `admin`; plus rules author.

**Runner task types (§2, PLAN M5-T6):** NEC, khatian copy, revenue-register check, CO order.

**Adapters (§2):** Jharbhoomi, Bhu-Naksha, eCourts, CERSAI (v0 online); municipal, Circle Office, DC office, Record Room, DSR via runner.

**Adapter interface (§13):** `fetch(parcel) → documents[]`, `health()`, fee reporting.

**Stores (§9):** object store (S3-compatible / MinIO locally), MongoDB, OpenSearch, Kafka, Postgres ledger.

**SLA (§5, §11):** 5 working days; alerts at day 2 and day 3; 24 h and 40 h for no-physical-search cases; Blocked time excluded.

**Cost line items (PLAN M1-T5, §8, §11):** model, OCR, transcription, portal fee, runner fee; target < ₹150 model + OCR, < ₹900 all-in.

**Statutes referenced (data for packs, never in engine):** CNT Act 1908 s.46, s.49, s.71A; SPT Act s.20; Hindu Succession Act s.2(2), s.6; Jharkhand Apartment (Flat) Ownership Act 2011; RERA (post-2017); TPA; Registration Act; Limitation Act.

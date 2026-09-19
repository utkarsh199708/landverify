# <TASK-ID>: <title>

**Milestone:** M<n>
**Agent:** coder | rules-author
**Branch:** `wave/<TASK-ID>`
**Depends on:** <task ids, merged to main, or "none">

## Goal

<One sentence. What exists after this task that did not exist before.>

## HLD references

- §<n> <section title> — <the specific table row, invariant or sentence that applies>
- §<n> …

## Scope

**May touch (create or edit):**
- `path/or/glob/**`
- `path/to/specific/File.java`

**Must not touch:** everything else. In particular: <name anything adjacent and tempting>.

## Interfaces (exact — copy, do not paraphrase)

```
<signatures, JSON Schemas, event names, topic names, CLI flags, HTTP routes>
```

## Behaviour

<Numbered, precise. What happens on the happy path. What happens on each error path. What is logged, what is emitted, what is persisted.>

## Acceptance criteria (each verifiable by a command or a named test)

1. `<command>` → <expected result>
2. Test `<TestClass#method or test_function>` passes and fails when <guard> is removed.
3. …

## Tests to write

| Test | Proves | Type |
| --- | --- | --- |
| `<name>` | <criterion or invariant> | positive / negative / property / contract |

At least one negative test per CLAUDE.md non-negotiable this task touches: <list which ones apply>.

## Non-negotiables in play

<Tick the ones this task can violate and say how the tests guard them.>
- [ ] Models never decide
- [ ] No finding without evidence
- [ ] Engine knows no state
- [ ] Category is a sourced, dated fact
- [ ] One door to models
- [ ] Aadhaar never stored
- [ ] Ledger append-only
- [ ] Determinism

## Out of scope (explicitly)

<Things a diligent engineer might reasonably do here that belong to another task. Name the task.>

## Notes for the reviewer

<Where to look hardest. What would be easy to fake.>

## Definition of done

Report in the fixed format; all acceptance criteria `MET` with evidence; `make lint test` green on the branch; committed on `wave/<TASK-ID>` with messages prefixed `<TASK-ID>:`.

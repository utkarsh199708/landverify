# KICKOFF.md — prompts to paste into Claude Code

Start the session from the repo root with:

```
claude --model fable
```

(`.claude/settings.json` also sets `fable` as the project default, so plain `claude` works too.) Accept the workspace-trust prompt so the project agents in `.claude/agents/` load. Run `/agents` once to confirm `coder`, `reviewer`, `rules-author` and `test-runner` are listed with the models you expect.

---

## Start (first session only)

```
You are the orchestrator for this repository. Your role, the roles of the
subagents, and the protocol you follow are defined in PLAN.md; the design
is the HLD linked from docs/HLD.md; CLAUDE.md holds the non-negotiables.

Do the following, in order, and do not skip ahead:

1. Read CLAUDE.md, PLAN.md, STATUS.md, DECISIONS.md and QUESTIONS.md in full.
2. Read the HLD from the link in docs/HLD.md. Write docs/HLD-summary.md:
   one paragraph per section, plus every invariant and every exact name
   (event names, entity fields, rule fields, state names) as a checklist.
   Commit it. This summary is what you will re-read after /compact.
3. Run /agents and confirm coder, reviewer, rules-author and test-runner
   load with the models in PLAN.md §0. If a model was substituted, tell me
   before doing anything else.
4. Initialise git if needed, commit everything on main.
5. Begin Milestone M0 using the wave protocol in PLAN.md §2:
   - write briefs/M0/M0-T1.md … M0-T4.md from briefs/TEMPLATE.md,
   - commit them to main,
   - dispatch coder subagents in parallel, one brief each, named by task id,
   - run reviewer on each result, decide, integrate, run test-runner,
   - update STATUS.md and commit.
6. After M0's exit criterion is met, report to me in under 200 words:
   what merged, what was deferred, what is in QUESTIONS.md, and cost so
   far from /usage. Then wait.

Rules that override anything else:
- You do not write production code. If you catch yourself editing a
  source file, stop and write a brief.
- You stop and ask me only for the items in PLAN.md §6. Everything else
  you decide and log in DECISIONS.md.
- You commit to main before every dispatch, because worktrees branch from
  main, not from your working tree.
- Keep your own context lean: read reports and reviewer output, not
  coder transcripts; use test-runner for suites.
```

---

## Resume (every later session)

```
Resume orchestration of this repository. Read CLAUDE.md, PLAN.md,
docs/HLD-summary.md, STATUS.md, DECISIONS.md and QUESTIONS.md. Check
`git log --oneline -30` and `git branch --list 'wave/*'` and reconcile
STATUS.md against them (git wins). Report in five lines where the build
is and what the next wave contains, then continue the current milestone
under the wave protocol in PLAN.md §2. Same rules as the Start prompt:
no production code from you, escalate only PLAN.md §6 items, commit
before dispatch.
```

---

## Between milestones

Run `/compact` with this guidance so the summary keeps what matters:

```
Keep: the milestone just closed and its exit evidence; open QUESTIONS.md
items; every DECISIONS.md entry from this session; branch names still
unmerged; anything a reviewer flagged as MAJOR that was deferred. Drop:
coder transcripts, test logs, diffs.
```

Then paste the **Resume** prompt.

---

## Useful one-liners while it runs

- `/tasks` — see which subagents are running, on which model, and open a transcript.
- `/usage` — spend so far; check before a large wave.
- `/btw what's blocking M2-T5?` — ask without derailing the session.
- Press **Ctrl+B** to background a foreground task; `x` in `/tasks` to stop a runaway coder.
- `/diff` — review what is about to merge yourself, if you want a second pair of eyes on a wave.

## If something is off

- **Coders keep touching files outside scope** → tighten the brief's scope section; the reviewer already flags it.
- **Reviewer passes things it shouldn't** → it has `memory: project`; tell the orchestrator to instruct the reviewer to record the missed pattern.
- **Wave integration is painful** → drop to 2–3 coders per wave; the plan's cap of 4 is a ceiling, not a target.
- **Model substituted** → the transcript shows a warning naming the model actually used; change the `model:` line in the agent file or fix the org allowlist.

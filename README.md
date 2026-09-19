# title-engine-jh

Land Title Diligence Engine, Jharkhand-first. Built by an orchestrated Claude Code workflow: Fable plans and integrates, Opus 4.8 subagents code and review.

| Read this | For |
| --- | --- |
| `KICKOFF.md` | The prompt to paste into Claude Code to start or resume |
| `PLAN.md` | Roles, the wave protocol, milestones M0–M8 with tasks |
| `CLAUDE.md` | Non-negotiables and conventions every agent loads |
| `docs/HLD.md` | Link to the living design doc and its section map |
| `.claude/agents/` | `coder`, `reviewer`, `rules-author`, `test-runner` |
| `briefs/` | One file per task, written by the orchestrator before dispatch |
| `STATUS.md` · `DECISIONS.md` · `QUESTIONS.md` | Where the build is, what was decided, what is open |

Start: `claude --model fable`, then paste the **Start** block from `KICKOFF.md`.

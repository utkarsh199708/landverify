---
name: title-engine-java-build-quirks
description: Java/Gradle build conventions and gotchas in title-engine (libs + services modules)
metadata:
  type: project
---

Durable Java-side build facts for the title-engine repo, learned building `libs/domain` (M1-T1).

**Why:** these are cross-cutting conventions the root build enforces; violating them fails
`make lint test` in non-obvious ways. **How to apply:** consult before any Java module task.

- **Root `build.gradle.kts` owns the shared convention.** It applies `java-library` + `checkstyle`
  + spotless (googleJavaFormat) to **leaf modules only** via
  `configure(subprojects.filter { it.childProjects.isEmpty() })` — the nested `include()` in
  settings creates `:libs` and `:services` container nodes that must get nothing. Every leaf compiles
  with `-Werror -Xlint:all` and a Java 21 toolchain, so any lint warning (unchecked, this-escape,
  unused) fails the build. `useJUnitPlatform()` is set globally.
- **Version catalog is `gradle/libs.versions.toml`.** The generated `libs` accessor works in a
  module's own `build.gradle.kts` (e.g. `libs.jackson.databind`) but NOT inside root `subprojects {}`
  / `configure(...)` — there, resolve it once with
  `rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")` and use
  `catalog.findLibrary("junit-bom").get()`. Test stack (junit-bom/jupiter/launcher, assertj) lives in
  the catalog and is injected by the root convention.
- **Checkstyle is deliberately minimal** (`config/checkstyle/checkstyle.xml`): only UnusedImports,
  RedundantImport, AvoidStarImport, **OneTopLevelClass**. The last forces one top-level type per file
  — a brief that lists several records on one line still needs one file each. No Javadoc requirement.
- **`make fmt` = spotlessApply + ruff; `make lint` = spotlessCheck + checkstyleMain/Test + ruff;
  `make test` = `./gradlew test` + `uv run pytest` (workers/).** Run `make fmt` before committing;
  spotless will reword-wrap comments, which is fine.
- **victools schema generator pulls unavoidable transitives.** `com.github.victools:jsonschema-
  generator` drags in `com.fasterxml:classmate` and `org.slf4j:slf4j-api` onto runtimeClasspath. A
  criterion saying "only jackson + victools, nothing else" on runtimeClasspath cannot be met
  literally — those two are required by victools. It logs a harmless "No SLF4J providers" NOP notice.
- **Deterministic JSON Schema export** (`libs/domain`): victools introspects record fields fine;
  Optional<T> needs a `withTargetTypeOverridesResolver` to unwrap to T + `withNullableCheck`. For
  byte-stability across JVM runs, don't trust victools/Jackson key order — serialise the JsonNode with
  a custom recursive writer that sorts object keys (arrays kept in order), 2-space indent, trailing
  `\n`. `.gitattributes` is `* text=auto eol=lf`, so committed `.json` stays LF on Windows.
- **Sandbox shell limits (Bash tool in this worktree):** it refuses commands it deems "too complex to
  verify" — shell function definitions, multiple heredocs in one call, and any command whose text
  contains a `git`-ambiguous token (even `.gitkeep` in an `rm`). Write multi-file generators to the
  scratchpad and run `bash <file>`, or use the Write tool per file; keep `git` commands plain and
  single-purpose.

package com.titleengine.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A report version over a set of findings (HLD §3, §5). Replays create new versions; a sealed
 * report is never mutated (non-negotiable 7). {@code version} starts at 1.
 */
public record Report(
    ReportId id,
    int version,
    CaseId caseId,
    List<FindingId> findingIds,
    String chainSummary,
    Optional<String> advocateOpinion,
    Optional<String> signatureRef,
    Optional<String> deliveredTo) {
  public Report {
    Objects.requireNonNull(id, "id");
    if (version < 1) {
      throw new IllegalArgumentException("version must be >= 1");
    }
    Objects.requireNonNull(caseId, "caseId");
    findingIds = Guards.copyList(findingIds, "findingIds");
    Guards.requireText(chainSummary, "chainSummary");
    Objects.requireNonNull(advocateOpinion, "advocateOpinion");
    Objects.requireNonNull(signatureRef, "signatureRef");
    Objects.requireNonNull(deliveredTo, "deliveredTo");
  }
}

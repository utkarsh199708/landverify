package com.titleengine.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A single finding (HLD §3, §6, non-negotiable 2). The compact constructor rejects null or empty
 * evidence with {@code IllegalArgumentException("evidence")}; there is no factory, builder or
 * deserialiser that can bypass it, so a finding without evidence cannot exist in the JVM.
 */
public record Finding(
    FindingId id,
    Severity severity,
    String ruleId,
    String packVersion,
    FindingSubject subject,
    List<EvidencePointer> evidence,
    String machineRationale,
    Optional<ReviewDecision> decision,
    String verificationInstruction) {
  public Finding {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(severity, "severity");
    Guards.requireText(ruleId, "ruleId");
    Guards.requireText(packVersion, "packVersion");
    Objects.requireNonNull(subject, "subject");
    if (evidence == null || evidence.isEmpty()) {
      throw new IllegalArgumentException("evidence");
    }
    evidence = List.copyOf(evidence);
    Guards.requireText(machineRationale, "machineRationale");
    Objects.requireNonNull(decision, "decision");
    Guards.requireText(verificationInstruction, "verificationInstruction");
  }
}

package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Non-negotiable 2: a Finding cannot exist without evidence. */
class FindingTest {

  private static Finding finding(List<EvidencePointer> evidence) {
    return new Finding(
        new FindingId("f1"),
        Severity.BLOCKER,
        "JH-CNT-046-01",
        "2026.09",
        new ParcelSubject(new ParcelId("p1")),
        evidence,
        "machine rationale",
        Optional.empty(),
        "verify this");
  }

  @Test
  void rejects_empty_evidence() {
    assertThatThrownBy(() -> finding(List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("evidence");
  }

  @Test
  void rejects_null_evidence() {
    assertThatThrownBy(() -> finding(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("evidence");
  }

  @Test
  void accepts_rule_evidence_only() {
    Finding f = finding(List.of(Fixtures.ruleEvidence()));
    assertThat(f.evidence()).hasSize(1);
    assertThat(f.evidence().get(0)).isInstanceOf(RuleEvidence.class);
  }
}

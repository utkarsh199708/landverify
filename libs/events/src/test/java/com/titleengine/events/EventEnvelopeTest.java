package com.titleengine.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EventEnvelopeTest {

  private static EventEnvelope envelopeWith(String eventType, String caseId) {
    return new EventEnvelope(
        "evt-1",
        eventType,
        "tenant-1",
        caseId,
        "edge",
        OffsetDateTime.parse("2026-09-19T00:00:00Z"),
        "idem-1",
        "abc123",
        "{}");
  }

  @Test
  void rejects_blank_case_id() {
    assertThatThrownBy(() -> envelopeWith("case.created", "  "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("caseId");
  }

  @Test
  void rejects_unknown_event_type() {
    assertThatThrownBy(() -> envelopeWith("does.not.exist", "case-1"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "case.created",
        "parcel.resolved",
        "acquisition.complete",
        "docs.gated",
        "findings.emitted",
        "review.needs_input",
        "opinion.signed",
        "report.sealed"
      })
  void accepts_every_wire_name(String wire) {
    EventEnvelope envelope = envelopeWith(wire, "case-1");
    assertThat(envelope.eventType()).isEqualTo(wire);
  }
}

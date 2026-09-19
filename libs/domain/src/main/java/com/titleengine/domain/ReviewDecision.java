package com.titleengine.domain;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * An advocate's decision on a finding (HLD §10). {@code at} is an input, never read from the clock
 * (non-negotiable 8). An override must carry a non-blank reason.
 */
public record ReviewDecision(
    ReviewDecisionKind kind, String actor, OffsetDateTime at, Optional<String> reason) {
  public ReviewDecision {
    Objects.requireNonNull(kind, "kind");
    Guards.requireText(actor, "actor");
    Objects.requireNonNull(at, "at");
    Objects.requireNonNull(reason, "reason");
    if (kind == ReviewDecisionKind.OVERRIDE && (reason.isEmpty() || reason.get().isBlank())) {
      throw new IllegalArgumentException("OVERRIDE requires a non-blank reason");
    }
  }
}

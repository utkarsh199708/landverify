package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** HLD §10: an override must carry a non-blank reason; accept/verify need none. */
class ReviewDecisionTest {

  private static final OffsetDateTime AT = OffsetDateTime.parse("2026-02-02T10:00:00+05:30");

  @Test
  void override_requires_reason() {
    assertThatThrownBy(
            () -> new ReviewDecision(ReviewDecisionKind.OVERRIDE, "adv-1", AT, Optional.empty()))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(
            () -> new ReviewDecision(ReviewDecisionKind.OVERRIDE, "adv-1", AT, Optional.of("  ")))
        .isInstanceOf(IllegalArgumentException.class);

    ReviewDecision override =
        new ReviewDecision(ReviewDecisionKind.OVERRIDE, "adv-1", AT, Optional.of("wrong severity"));
    assertThat(override.reason()).contains("wrong severity");

    ReviewDecision accept =
        new ReviewDecision(ReviewDecisionKind.ACCEPT, "adv-1", AT, Optional.empty());
    assertThat(accept.kind()).isEqualTo(ReviewDecisionKind.ACCEPT);
  }
}

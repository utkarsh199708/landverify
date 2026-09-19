package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Records defensively copy their list fields, so callers cannot mutate them after construction. */
class ImmutabilityTest {

  @Test
  void list_fields_are_unmodifiable() {
    List<PartyId> borrowers = new ArrayList<>();
    borrowers.add(new PartyId("p1"));

    Case caseRecord =
        new Case(
            new CaseId("c1"),
            new TenantId("t1"),
            "lender-ref",
            new ParcelId("parcel-1"),
            borrowers,
            CaseState.RECEIVED,
            OffsetDateTime.parse("2026-01-01T00:00:00Z"));

    // Mutating the source list after construction does not leak into the record.
    borrowers.add(new PartyId("p2"));
    assertThat(caseRecord.borrowerPartyIds()).hasSize(1);

    // The stored list itself is unmodifiable.
    assertThatThrownBy(() -> caseRecord.borrowerPartyIds().add(new PartyId("p3")))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}

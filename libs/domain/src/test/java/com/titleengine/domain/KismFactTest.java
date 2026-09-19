package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Kism is a sourced dated fact grounded in the document it cites (HLD §3, §6). */
class KismFactTest {

  @Test
  void evidence_must_point_at_source_document() {
    DocumentId source = new DocumentId("khatian-1");
    DocumentId elsewhere = new DocumentId("register-ii-9");
    assertThatThrownBy(
            () ->
                new KismFact(
                    Kism.RAIYATI,
                    LocalDate.parse("2019-06-01"),
                    source,
                    Fixtures.docEvidence(elsewhere)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void accepts_evidence_grounded_in_source_document() {
    DocumentId source = new DocumentId("khatian-1");
    KismFact fact =
        new KismFact(
            Kism.RAIYATI, LocalDate.parse("2019-06-01"), source, Fixtures.docEvidence(source));
    assertThat(fact.kism()).isEqualTo(Kism.RAIYATI);
  }
}

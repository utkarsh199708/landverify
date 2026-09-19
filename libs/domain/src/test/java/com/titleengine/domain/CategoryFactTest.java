package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Non-negotiable 4: category is a sourced dated fact grounded in the document it cites. */
class CategoryFactTest {

  @Test
  void evidence_must_point_at_source_document() {
    DocumentId source = new DocumentId("khatian-1");
    DocumentId elsewhere = new DocumentId("some-other-doc");
    assertThatThrownBy(
            () ->
                new CategoryFact(
                    Category.ST,
                    LocalDate.parse("2020-01-01"),
                    source,
                    Fixtures.docEvidence(elsewhere)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejects_rule_evidence() {
    // A rule firing can never source a category; only a document or transcript can.
    DocumentId source = new DocumentId("khatian-1");
    assertThatThrownBy(
            () ->
                new CategoryFact(
                    Category.ST, LocalDate.parse("2020-01-01"), source, Fixtures.ruleEvidence()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void accepts_evidence_grounded_in_source_document() {
    DocumentId source = new DocumentId("khatian-1");
    CategoryFact fact =
        new CategoryFact(
            Category.ST, LocalDate.parse("2020-01-01"), source, Fixtures.docEvidence(source));
    assertThat(fact.category()).isEqualTo(Category.ST);
  }
}

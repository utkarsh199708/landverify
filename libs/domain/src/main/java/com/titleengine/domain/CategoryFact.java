package com.titleengine.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A party category read from a specific document at a specific date (HLD §3, non-negotiable 4).
 * Category is never derived from a name; it exists only as this sourced, dated fact. The evidence
 * must be document- or transcript-grounded in the very {@code sourceDocument} it cites.
 */
public record CategoryFact(
    Category category, LocalDate asOf, DocumentId sourceDocument, EvidencePointer evidence) {
  public CategoryFact {
    Objects.requireNonNull(category, "category");
    Objects.requireNonNull(asOf, "asOf");
    Objects.requireNonNull(sourceDocument, "sourceDocument");
    Objects.requireNonNull(evidence, "evidence");
    // A rule firing cannot source a category; only a document or transcript can (non-negotiable 4).
    if (!(evidence instanceof DocumentEvidence || evidence instanceof TranscriptEvidence)) {
      throw new IllegalArgumentException("evidence must be document- or transcript-grounded");
    }
    DocumentId grounded = Guards.groundedDocumentId(evidence);
    if (grounded == null || !grounded.equals(sourceDocument)) {
      throw new IllegalArgumentException("evidence must point at sourceDocument");
    }
  }
}

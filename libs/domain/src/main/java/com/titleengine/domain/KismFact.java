package com.titleengine.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A parcel's kism read from a specific document at a specific date (HLD §3, §6). Like a category,
 * kism is a sourced dated fact; the evidence must be grounded in the cited {@code sourceDocument}.
 */
public record KismFact(
    Kism kism, LocalDate asOf, DocumentId sourceDocument, EvidencePointer evidence) {
  public KismFact {
    Objects.requireNonNull(kism, "kism");
    Objects.requireNonNull(asOf, "asOf");
    Objects.requireNonNull(sourceDocument, "sourceDocument");
    Objects.requireNonNull(evidence, "evidence");
    DocumentId grounded = Guards.groundedDocumentId(evidence);
    if (grounded == null || !grounded.equals(sourceDocument)) {
      throw new IllegalArgumentException("evidence must point at sourceDocument");
    }
  }
}

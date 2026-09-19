package com.titleengine.domain;

import java.util.Objects;

/** Evidence grounded in a page region of a document (HLD §8). Page is 1-based. */
public record DocumentEvidence(DocumentId documentId, int page, BoundingBox region)
    implements EvidencePointer {
  public DocumentEvidence {
    Objects.requireNonNull(documentId, "documentId");
    Objects.requireNonNull(region, "region");
    if (page < 1) {
      throw new IllegalArgumentException("page must be >= 1");
    }
  }
}

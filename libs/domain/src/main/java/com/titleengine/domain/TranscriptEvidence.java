package com.titleengine.domain;

import java.util.Objects;

/** Evidence grounded in a line of a human transcript document (HLD §8). Line is 1-based. */
public record TranscriptEvidence(DocumentId documentId, int line) implements EvidencePointer {
  public TranscriptEvidence {
    Objects.requireNonNull(documentId, "documentId");
    if (line < 1) {
      throw new IllegalArgumentException("line must be >= 1");
    }
  }
}

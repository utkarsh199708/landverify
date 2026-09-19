package com.titleengine.domain;

import java.util.List;
import java.util.Objects;

/**
 * Shared validation helpers for the immutable domain records (HLD §3). Every record's compact
 * constructor routes null and range checks through here so the rules read the same everywhere.
 */
final class Guards {

  private Guards() {}

  /** Non-null, non-blank text; used for ids and free-text fields. */
  static String requireText(String value, String field) {
    Objects.requireNonNull(value, field);
    if (value.isBlank()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
    return value;
  }

  /** Defensive, unmodifiable copy that also rejects null elements (List.copyOf contract). */
  static <T> List<T> copyList(List<T> value, String field) {
    Objects.requireNonNull(value, field);
    return List.copyOf(value);
  }

  /**
   * Like {@link #copyList} but rejects an empty list; the thrown message is exactly {@code field}.
   */
  static <T> List<T> requireNonEmpty(List<T> value, String field) {
    List<T> copy = copyList(value, field);
    if (copy.isEmpty()) {
      throw new IllegalArgumentException(field);
    }
    return copy;
  }

  /** A confidence or normalised coordinate in the closed interval 0..1. */
  static double requireUnitInterval(double value, String field) {
    if (!(value >= 0.0d && value <= 1.0d)) {
      throw new IllegalArgumentException(field + " must be within 0..1");
    }
    return value;
  }

  /**
   * The document an evidence pointer is grounded in, or {@code null} for {@link RuleEvidence} which
   * has no document. Used by category/kism facts and extracted fields to prove the evidence points
   * at the expected document (HLD §3, non-negotiable 2 and 4).
   */
  static DocumentId groundedDocumentId(EvidencePointer evidence) {
    if (evidence instanceof DocumentEvidence d) {
      return d.documentId();
    }
    if (evidence instanceof TranscriptEvidence t) {
      return t.documentId();
    }
    return null;
  }
}

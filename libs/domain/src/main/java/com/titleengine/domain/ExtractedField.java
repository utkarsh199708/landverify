package com.titleengine.domain;

import java.util.Objects;

/**
 * One extracted value with its confidence and grounding (HLD §8). Grounding must be document- or
 * transcript-based; a rule firing can never ground an extracted field.
 */
public record ExtractedField(
    String name, String value, double confidence, EvidencePointer grounding) {
  public ExtractedField {
    Guards.requireText(name, "name");
    Objects.requireNonNull(value, "value");
    Guards.requireUnitInterval(confidence, "confidence");
    Objects.requireNonNull(grounding, "grounding");
    if (!(grounding instanceof DocumentEvidence || grounding instanceof TranscriptEvidence)) {
      throw new IllegalArgumentException("grounding must be document- or transcript-based");
    }
  }
}

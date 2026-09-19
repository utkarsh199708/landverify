package com.titleengine.domain;

import java.util.List;
import java.util.Objects;

/**
 * An ingested document with its extracted fields (HLD §3, §8). Immutable once created; every
 * field's grounding must reference this document's own id.
 */
public record Document(
    DocumentId id,
    String type,
    DocumentSource source,
    String rawFileRef,
    int pages,
    Script script,
    List<ExtractedField> fields,
    TrustLevel trust) {
  public Document {
    Objects.requireNonNull(id, "id");
    Guards.requireText(type, "type");
    Objects.requireNonNull(source, "source");
    Guards.requireText(rawFileRef, "rawFileRef");
    if (pages < 1) {
      throw new IllegalArgumentException("pages must be >= 1");
    }
    Objects.requireNonNull(script, "script");
    fields = Guards.copyList(fields, "fields");
    Objects.requireNonNull(trust, "trust");
    for (ExtractedField field : fields) {
      DocumentId grounded = Guards.groundedDocumentId(field.grounding());
      if (grounded == null || !grounded.equals(id)) {
        throw new IllegalArgumentException("every field grounding must reference this document");
      }
    }
  }
}

package com.titleengine.domain;

import java.util.Objects;

/** A finding subject that is a document (HLD §3). */
public record DocumentSubject(DocumentId id) implements FindingSubject {
  public DocumentSubject {
    Objects.requireNonNull(id, "id");
  }
}

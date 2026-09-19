package com.titleengine.domain;

/** Opaque non-blank identifier (DocumentId). HLD §3. */
public record DocumentId(String value) {
  public DocumentId {
    Guards.requireText(value, "value");
  }
}

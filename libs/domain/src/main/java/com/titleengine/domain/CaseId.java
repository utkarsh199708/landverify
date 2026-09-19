package com.titleengine.domain;

/** Opaque non-blank identifier (CaseId). HLD §3. */
public record CaseId(String value) {
  public CaseId {
    Guards.requireText(value, "value");
  }
}

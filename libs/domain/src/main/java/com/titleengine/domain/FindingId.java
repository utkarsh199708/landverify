package com.titleengine.domain;

/** Opaque non-blank identifier (FindingId). HLD §3. */
public record FindingId(String value) {
  public FindingId {
    Guards.requireText(value, "value");
  }
}

package com.titleengine.domain;

/** Opaque non-blank identifier (LinkId). HLD §3. */
public record LinkId(String value) {
  public LinkId {
    Guards.requireText(value, "value");
  }
}

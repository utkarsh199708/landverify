package com.titleengine.domain;

/** Opaque non-blank identifier (PartyId). HLD §3. */
public record PartyId(String value) {
  public PartyId {
    Guards.requireText(value, "value");
  }
}

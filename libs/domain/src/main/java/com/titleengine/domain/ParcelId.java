package com.titleengine.domain;

/** Opaque non-blank identifier (ParcelId). HLD §3. */
public record ParcelId(String value) {
  public ParcelId {
    Guards.requireText(value, "value");
  }
}

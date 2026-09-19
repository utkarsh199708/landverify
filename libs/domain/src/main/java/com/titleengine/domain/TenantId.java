package com.titleengine.domain;

/** Opaque non-blank identifier (TenantId). HLD §3. */
public record TenantId(String value) {
  public TenantId {
    Guards.requireText(value, "value");
  }
}

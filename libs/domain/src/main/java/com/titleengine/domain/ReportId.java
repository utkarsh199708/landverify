package com.titleengine.domain;

/** Opaque non-blank identifier (ReportId). HLD §3. */
public record ReportId(String value) {
  public ReportId {
    Guards.requireText(value, "value");
  }
}

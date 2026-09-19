package com.titleengine.domain;

import java.time.LocalDate;
import java.util.Objects;

/** The window a chain must cover (30 years); {@code from} strictly before {@code to} (HLD §6). */
public record CoverageWindow(LocalDate from, LocalDate to) {
  public CoverageWindow {
    Objects.requireNonNull(from, "from");
    Objects.requireNonNull(to, "to");
    if (!from.isBefore(to)) {
      throw new IllegalArgumentException("from must be before to");
    }
  }
}

package com.titleengine.domain;

import java.time.LocalDate;

/**
 * A first-class gap in the chain, carrying a reason and a way to close it (HLD §3, §6). {@code
 * from} must be strictly before {@code to}; both strings are non-blank.
 */
public record Gap(LocalDate from, LocalDate to, String hypothesis, String verificationInstruction) {
  public Gap {
    java.util.Objects.requireNonNull(from, "from");
    java.util.Objects.requireNonNull(to, "to");
    Guards.requireText(hypothesis, "hypothesis");
    Guards.requireText(verificationInstruction, "verificationInstruction");
    if (!from.isBefore(to)) {
      throw new IllegalArgumentException("from must be before to");
    }
  }
}

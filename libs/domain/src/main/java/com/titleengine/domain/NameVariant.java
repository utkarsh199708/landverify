package com.titleengine.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * One spelling of a party's name in a given script, with an optional father's name (HLD §3, §6).
 */
public record NameVariant(String text, Script script, Optional<String> fathersName) {
  public NameVariant {
    Guards.requireText(text, "text");
    Objects.requireNonNull(script, "script");
    Objects.requireNonNull(fathersName, "fathersName");
  }
}

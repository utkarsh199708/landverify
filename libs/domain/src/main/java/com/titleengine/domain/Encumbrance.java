package com.titleengine.domain;

import java.util.Objects;
import java.util.Optional;

/** An encumbrance over a parcel, marked released or live (HLD §3, §6). */
public record Encumbrance(
    EncumbranceType type,
    String holder,
    Optional<Money> amount,
    DocumentId source,
    boolean released) {
  public Encumbrance {
    Objects.requireNonNull(type, "type");
    Guards.requireText(holder, "holder");
    Objects.requireNonNull(amount, "amount");
    Objects.requireNonNull(source, "source");
  }
}

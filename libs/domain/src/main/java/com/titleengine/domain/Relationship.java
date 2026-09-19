package com.titleengine.domain;

import java.util.Objects;

/** A relationship from one party to another; kind is free text for now (HLD §3). */
public record Relationship(PartyId other, String kind) {
  public Relationship {
    Objects.requireNonNull(other, "other");
    Guards.requireText(kind, "kind");
  }
}

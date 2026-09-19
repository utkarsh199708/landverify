package com.titleengine.domain;

import java.util.Objects;

/** A finding subject that is a parcel (HLD §3). */
public record ParcelSubject(ParcelId id) implements FindingSubject {
  public ParcelSubject {
    Objects.requireNonNull(id, "id");
  }
}

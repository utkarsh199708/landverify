package com.titleengine.domain;

import java.util.Objects;

/** A finding subject that is a title link (HLD §3). */
public record LinkSubject(LinkId id) implements FindingSubject {
  public LinkSubject {
    Objects.requireNonNull(id, "id");
  }
}

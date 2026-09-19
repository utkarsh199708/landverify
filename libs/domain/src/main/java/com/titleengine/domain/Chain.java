package com.titleengine.domain;

import java.util.List;
import java.util.Objects;

/**
 * The ordered chain of links back to a root of title (HLD §3, §6). Ordering by date is the engine's
 * job, not this record's; here we only guarantee non-null structure and immutable lists.
 */
public record Chain(
    List<TitleLink> links, DocumentId rootOfTitle, CoverageWindow window, List<Gap> gaps) {
  public Chain {
    links = Guards.copyList(links, "links");
    Objects.requireNonNull(rootOfTitle, "rootOfTitle");
    Objects.requireNonNull(window, "window");
    gaps = Guards.copyList(gaps, "gaps");
  }
}

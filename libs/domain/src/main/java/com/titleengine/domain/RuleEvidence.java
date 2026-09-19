package com.titleengine.domain;

import java.util.Collections;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Evidence that a rule fired with a given set of facts (HLD §6, §7). The facts map is a sorted,
 * unmodifiable copy so the same firing serialises identically every time (non-negotiable 8).
 */
public record RuleEvidence(String ruleId, String packVersion, SortedMap<String, String> facts)
    implements EvidencePointer {
  public RuleEvidence {
    Guards.requireText(ruleId, "ruleId");
    Guards.requireText(packVersion, "packVersion");
    Objects.requireNonNull(facts, "facts");
    if (facts.isEmpty()) {
      throw new IllegalArgumentException("facts must not be empty");
    }
    facts = Collections.unmodifiableSortedMap(new TreeMap<>(facts));
  }
}

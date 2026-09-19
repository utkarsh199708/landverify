package com.titleengine.domain;

import java.util.SortedMap;
import java.util.TreeMap;

/** Small valid building blocks shared by the domain tests. */
final class Fixtures {

  private Fixtures() {}

  static final DocumentId DOC = new DocumentId("doc-1");

  static DocumentEvidence docEvidence() {
    return new DocumentEvidence(DOC, 1, new BoundingBox(0.1, 0.1, 0.2, 0.2));
  }

  static DocumentEvidence docEvidence(DocumentId doc) {
    return new DocumentEvidence(doc, 1, new BoundingBox(0.1, 0.1, 0.2, 0.2));
  }

  static RuleEvidence ruleEvidence() {
    SortedMap<String, String> facts = new TreeMap<>();
    facts.put("parcel.kism", "raiyati");
    return new RuleEvidence("JH-CNT-046-01", "2026.09", facts);
  }
}

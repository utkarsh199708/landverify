package com.titleengine.domain;

/** The eight case states (HLD 5). */
public enum CaseState {
  RECEIVED("Received"),
  ACQUIRING("Acquiring"),
  PROCESSING("Processing"),
  ANALYSING("Analysing"),
  IN_REVIEW("InReview"),
  BLOCKED("Blocked"),
  SIGNED("Signed"),
  DELIVERED("Delivered");

  private final String wire;

  CaseState(String wire) {
    this.wire = wire;
  }

  /** The exact wire string for this constant (HLD 3/6). */
  public String wire() {
    return wire;
  }

  /**
   * Resolves a wire string to its constant.
   *
   * @throws IllegalArgumentException if {@code wire} matches no constant
   */
  public static CaseState fromWire(String wire) {
    for (CaseState v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown CaseState wire: " + wire);
  }
}

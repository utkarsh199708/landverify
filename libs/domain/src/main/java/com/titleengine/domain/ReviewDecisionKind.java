package com.titleengine.domain;

/** Advocate decision on a finding (HLD 10). */
public enum ReviewDecisionKind {
  ACCEPT("accept"),
  OVERRIDE("override"),
  VERIFY("verify");

  private final String wire;

  ReviewDecisionKind(String wire) {
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
  public static ReviewDecisionKind fromWire(String wire) {
    for (ReviewDecisionKind v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown ReviewDecisionKind wire: " + wire);
  }
}

package com.titleengine.domain;

/** Finding severity; ordinal order is severity order, BLOCKER highest (HLD 3, 6). */
public enum Severity {
  BLOCKER("blocker"),
  MAJOR("major"),
  MINOR("minor"),
  INFO("info");

  private final String wire;

  Severity(String wire) {
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
  public static Severity fromWire(String wire) {
    for (Severity v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown Severity wire: " + wire);
  }
}

package com.titleengine.domain;

/** Ordinal marketability score; never a blended number (HLD 6). */
public enum Score {
  CLEAR("Clear"),
  CLEAR_WITH_CONDITIONS("Clear with conditions"),
  DEFECTIVE("Defective"),
  NOT_MARKETABLE("Not marketable");

  private final String wire;

  Score(String wire) {
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
  public static Score fromWire(String wire) {
    for (Score v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown Score wire: " + wire);
  }
}

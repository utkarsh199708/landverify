package com.titleengine.domain;

/** A party category; only ever stored inside a CategoryFact (HLD 3, non-negotiable 4). */
public enum Category {
  ST("ST"),
  SC("SC"),
  BC("BC"),
  GENERAL("General");

  private final String wire;

  Category(String wire) {
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
  public static Category fromWire(String wire) {
    for (Category v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown Category wire: " + wire);
  }
}

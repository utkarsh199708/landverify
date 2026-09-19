package com.titleengine.domain;

/** Writing system of a document (HLD 8). */
public enum Script {
  DEVANAGARI("devanagari"),
  ENGLISH("english"),
  KAITHI("kaithi"),
  URDU("urdu");

  private final String wire;

  Script(String wire) {
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
  public static Script fromWire(String wire) {
    for (Script v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown Script wire: " + wire);
  }
}

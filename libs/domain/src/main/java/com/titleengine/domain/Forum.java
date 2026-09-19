package com.titleengine.domain;

/** Forum a litigation hit belongs to (HLD 3). */
public enum Forum {
  CIVIL_COURT("civil court"),
  SAR("SAR"),
  DCLR("DCLR"),
  CO("CO");

  private final String wire;

  Forum(String wire) {
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
  public static Forum fromWire(String wire) {
    for (Forum v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown Forum wire: " + wire);
  }
}

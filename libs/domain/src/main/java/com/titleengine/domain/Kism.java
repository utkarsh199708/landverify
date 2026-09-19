package com.titleengine.domain;

/** Land tenure class (kism); decides which transferability rules apply (HLD 3, 6). */
public enum Kism {
  RAIYATI("raiyati"),
  KHAS_MAHAL("khas mahal"),
  GAIR_MAZARUA_KHAS("gair mazarua khas"),
  GAIR_MAZARUA_AAM("gair mazarua aam"),
  BHUINHARI("bhuinhari"),
  KHUNTKATTI("khuntkatti");

  private final String wire;

  Kism(String wire) {
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
  public static Kism fromWire(String wire) {
    for (Kism v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown Kism wire: " + wire);
  }
}

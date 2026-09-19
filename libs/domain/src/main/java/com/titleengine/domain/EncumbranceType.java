package com.titleengine.domain;

/** Type of encumbrance over a parcel (HLD 3). */
public enum EncumbranceType {
  MORTGAGE("mortgage"),
  CHARGE("charge"),
  LIS_PENDENS("lis pendens"),
  LEASE("lease"),
  S71A_RESTORATION_CLAIM("s.71A restoration claim"),
  SAR_PROCEEDING("SAR proceeding");

  private final String wire;

  EncumbranceType(String wire) {
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
  public static EncumbranceType fromWire(String wire) {
    for (EncumbranceType v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown EncumbranceType wire: " + wire);
  }
}

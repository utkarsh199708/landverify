package com.titleengine.domain;

/**
 * Type of encumbrance over a parcel (HLD 3). Wire names are generic vocabulary only; the statute
 * behind a restoration claim lives in a state rule pack as a citation, never here (non-negotiable
 * 3).
 */
public enum EncumbranceType {
  MORTGAGE("mortgage"),
  CHARGE("charge"),
  LIS_PENDENS("lis pendens"),
  LEASE("lease"),
  RESTORATION_CLAIM("restoration claim"),
  REVENUE_COURT_PROCEEDING("revenue court proceeding");

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

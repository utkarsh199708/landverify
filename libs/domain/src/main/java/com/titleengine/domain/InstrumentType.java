package com.titleengine.domain;

/** Type of transfer a TitleLink records (HLD 3). */
public enum InstrumentType {
  SALE("sale"),
  GIFT("gift"),
  RELEASE("release"),
  PARTITION("partition"),
  SUCCESSION("succession"),
  COURT_DECREE("court decree"),
  SETTLEMENT("settlement"),
  SUB_LEASE_ASSIGNMENT("sub-lease assignment");

  private final String wire;

  InstrumentType(String wire) {
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
  public static InstrumentType fromWire(String wire) {
    for (InstrumentType v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown InstrumentType wire: " + wire);
  }
}

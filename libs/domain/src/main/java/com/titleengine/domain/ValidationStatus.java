package com.titleengine.domain;

/** Outcome of the seven link-validation checks (HLD 6). */
public enum ValidationStatus {
  UNVALIDATED("unvalidated"),
  VALID("valid"),
  DEFECTIVE("defective"),
  VOID("void");

  private final String wire;

  ValidationStatus(String wire) {
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
  public static ValidationStatus fromWire(String wire) {
    for (ValidationStatus v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown ValidationStatus wire: " + wire);
  }
}

package com.titleengine.domain;

/** Role a party plays on a case or link (HLD 3). */
public enum PartyRole {
  HOLDER("holder"),
  TRANSFEROR("transferor"),
  TRANSFEREE("transferee"),
  BORROWER("borrower"),
  HEIR("heir"),
  LITIGANT("litigant"),
  OTHER("other");

  private final String wire;

  PartyRole(String wire) {
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
  public static PartyRole fromWire(String wire) {
    for (PartyRole v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown PartyRole wire: " + wire);
  }
}

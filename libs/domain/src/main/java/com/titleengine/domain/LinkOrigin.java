package com.titleengine.domain;

/** Where a TitleLink came from (HLD 6): recorded, inferred, or customary succession. */
public enum LinkOrigin {
  RECORDED("recorded"),
  INFERRED("inferred"),
  CUSTOMARY("customary");

  private final String wire;

  LinkOrigin(String wire) {
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
  public static LinkOrigin fromWire(String wire) {
    for (LinkOrigin v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown LinkOrigin wire: " + wire);
  }
}

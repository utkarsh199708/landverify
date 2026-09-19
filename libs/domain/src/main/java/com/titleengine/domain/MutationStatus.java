package com.titleengine.domain;

/** Mutation (dakhil-kharij) status of a transfer (HLD 3). */
public enum MutationStatus {
  NOT_APPLIED("not applied"),
  APPLIED("applied"),
  RECORDED("recorded"),
  OBJECTED("objected"),
  UNKNOWN("unknown");

  private final String wire;

  MutationStatus(String wire) {
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
  public static MutationStatus fromWire(String wire) {
    for (MutationStatus v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown MutationStatus wire: " + wire);
  }
}

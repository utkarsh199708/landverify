package com.titleengine.domain;

/** How much a document may be relied on (HLD 8). */
public enum TrustLevel {
  OFFICIAL_RECORD("official record"),
  CERTIFIED_COPY("certified copy"),
  HUMAN_TRANSCRIPTION("human transcription"),
  BORROWER_SUPPLIED("borrower supplied");

  private final String wire;

  TrustLevel(String wire) {
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
  public static TrustLevel fromWire(String wire) {
    for (TrustLevel v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown TrustLevel wire: " + wire);
  }
}

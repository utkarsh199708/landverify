package com.titleengine.domain;

/** How a document entered the system (HLD 3, 8). */
public enum DocumentSource {
  PORTAL("portal"),
  UPLOAD("upload"),
  RUNNER("runner"),
  TRANSCRIPTION("transcription");

  private final String wire;

  DocumentSource(String wire) {
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
  public static DocumentSource fromWire(String wire) {
    for (DocumentSource v : values()) {
      if (v.wire.equals(wire)) {
        return v;
      }
    }
    throw new IllegalArgumentException("unknown DocumentSource wire: " + wire);
  }
}

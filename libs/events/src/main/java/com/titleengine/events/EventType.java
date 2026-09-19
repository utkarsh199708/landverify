package com.titleengine.events;

/**
 * The eight cross-stage event names (HLD §5 End-to-end flow). {@link #wire} is the exact string on
 * the Kafka wire; nothing outside this enum may invent event names.
 */
public enum EventType {
  CASE_CREATED("case.created"),
  PARCEL_RESOLVED("parcel.resolved"),
  ACQUISITION_COMPLETE("acquisition.complete"),
  DOCS_GATED("docs.gated"),
  FINDINGS_EMITTED("findings.emitted"),
  REVIEW_NEEDS_INPUT("review.needs_input"),
  OPINION_SIGNED("opinion.signed"),
  REPORT_SEALED("report.sealed");

  /** The string on the wire, exactly as declared above. */
  public final String wire;

  EventType(String wire) {
    this.wire = wire;
  }

  /**
   * Resolves a wire name to its {@link EventType}.
   *
   * @throws IllegalArgumentException if {@code wire} is not one of the eight known names
   */
  public static EventType fromWire(String wire) {
    for (EventType type : values()) {
      if (type.wire.equals(wire)) {
        return type;
      }
    }
    throw new IllegalArgumentException("unknown eventType: " + wire);
  }
}

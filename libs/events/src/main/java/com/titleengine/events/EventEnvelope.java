package com.titleengine.events;

import java.time.OffsetDateTime;

/**
 * The envelope every cross-stage event carries (HLD §5, §9). Field names and order are the contract
 * for M1-T2 serialisation; do not reorder or rename. Typed payloads and JSON serialisation helpers
 * arrive in M1-T2 — this stub only guards the envelope's invariants.
 *
 * <p>The compact constructor rejects null or blank identity fields and any {@code eventType} that
 * is not one of the eight {@link EventType} wire names.
 */
public record EventEnvelope(
    String eventId,
    String eventType,
    String tenantId,
    String caseId,
    String producer,
    OffsetDateTime occurredAt,
    String idempotencyKey,
    String payloadHash,
    String payloadJson) {

  public EventEnvelope {
    requireText("eventId", eventId);
    requireText("eventType", eventType);
    requireText("tenantId", tenantId);
    requireText("caseId", caseId);
    requireText("producer", producer);
    requireText("payloadHash", payloadHash);
    // Reject any eventType not in the eight known wire names; message names the field.
    EventType.fromWire(eventType);
  }

  private static void requireText(String field, String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " must not be null or blank");
    }
  }
}

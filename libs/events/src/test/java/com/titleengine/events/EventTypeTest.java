package com.titleengine.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EventTypeTest {

  @Test
  void fromWire_round_trips_all_eight() {
    for (EventType type : EventType.values()) {
      assertThat(EventType.fromWire(type.wire)).isEqualTo(type);
    }
    assertThat(EventType.values()).hasSize(8);
  }
}

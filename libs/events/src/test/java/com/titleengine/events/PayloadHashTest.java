package com.titleengine.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PayloadHashTest {

  @Test
  void sha256_of_known_string_matches_vector() {
    assertThat(PayloadHash.sha256Hex("{}"))
        .isEqualTo("44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a");
  }
}

package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.SortedMap;
import org.junit.jupiter.api.Test;

/** Non-negotiable 8: schema generation is byte-stable and matches the committed files. */
class DeterminismTest {

  @Test
  void schema_generation_is_byte_stable() {
    SortedMap<String, String> first = DomainSchemas.generate();
    SortedMap<String, String> second = DomainSchemas.generate();
    assertThat(first).isEqualTo(second);

    Path dir = committedSchemaDir();
    for (Map.Entry<String, String> entry : first.entrySet()) {
      Path file = dir.resolve(entry.getKey() + ".schema.json");
      assertThat(Files.exists(file)).as("committed schema for " + entry.getKey()).isTrue();
      assertThat(readString(file))
          .as("committed schema matches the generator for " + entry.getKey())
          .isEqualTo(entry.getValue());
    }
  }

  static Path committedSchemaDir() {
    Path cursor = Path.of("").toAbsolutePath();
    while (cursor != null) {
      Path candidate = cursor.resolve("schemas/domain");
      if (Files.isDirectory(candidate)) {
        return candidate;
      }
      cursor = cursor.getParent();
    }
    throw new IllegalStateException("cannot locate schemas/domain");
  }

  private static String readString(Path file) {
    try {
      return Files.readString(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Non-negotiable 6: the model has no field for Aadhaar. This scans the main source and asserts zero
 * matches, guarding against a later "convenience" field. The banned word is only ever mentioned in
 * docs and in this test itself, both of which are outside the scanned tree.
 */
class AadhaarAbsenceTest {

  private static final Pattern BANNED = Pattern.compile("(?i)aadhaar");

  @Test
  void no_aadhaar_field_in_domain() {
    Path sourceRoot = mainSourceRoot();
    List<String> offenders = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(sourceRoot)) {
      for (Path file : paths.filter(p -> p.toString().endsWith(".java")).toList()) {
        String text = Files.readString(file);
        if (BANNED.matcher(text).find()) {
          offenders.add(file.toString());
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    assertThat(offenders)
        .as("no domain source file may mention the banned government id")
        .isEmpty();
  }

  private static Path mainSourceRoot() {
    Path direct = Path.of("src", "main", "java");
    if (Files.isDirectory(direct)) {
      return direct;
    }
    // Fall back to locating libs/domain/src/main/java from an ancestor of the working directory.
    Path cursor = Path.of("").toAbsolutePath();
    while (cursor != null) {
      Path candidate = cursor.resolve("libs/domain/src/main/java");
      if (Files.isDirectory(candidate)) {
        return candidate;
      }
      cursor = cursor.getParent();
    }
    throw new IllegalStateException("cannot locate libs/domain/src/main/java");
  }
}

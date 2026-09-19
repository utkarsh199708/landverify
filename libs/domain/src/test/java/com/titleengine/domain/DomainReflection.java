package com.titleengine.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Test-only helper that enumerates the compiled classes of {@code com.titleengine.domain} from the
 * main output directory, so the structural invariants (no double fields, no name→category
 * derivation, enum wire round-trips) can be checked by reflection over every real type.
 */
final class DomainReflection {

  private DomainReflection() {}

  static List<Class<?>> allDomainClasses() {
    try {
      Path root = Path.of(Guards.class.getProtectionDomain().getCodeSource().getLocation().toURI());
      Path pkgDir = root.resolve("com").resolve("titleengine").resolve("domain");
      List<Class<?>> classes = new ArrayList<>();
      try (Stream<Path> paths = Files.walk(pkgDir)) {
        List<Path> classFiles = paths.filter(p -> p.toString().endsWith(".class")).toList();
        for (Path p : classFiles) {
          String rel = root.relativize(p).toString().replace('\\', '/');
          String fqcn = rel.substring(0, rel.length() - ".class".length()).replace('/', '.');
          classes.add(Class.forName(fqcn));
        }
      }
      return classes;
    } catch (IOException | ReflectiveOperationException | java.net.URISyntaxException e) {
      throw new IllegalStateException("cannot enumerate domain classes", e);
    }
  }
}

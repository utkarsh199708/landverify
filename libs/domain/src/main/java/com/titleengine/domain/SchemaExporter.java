package com.titleengine.domain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Writes the committed JSON Schemas under {@code schemas/domain/}. Invoked by the Gradle task
 * {@code :libs:domain:exportSchemas}; running it twice on a clean tree changes nothing because
 * {@link DomainSchemas#generate()} is byte-stable (non-negotiable 8).
 */
public final class SchemaExporter {

  private SchemaExporter() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new IllegalArgumentException("usage: SchemaExporter <output-directory>");
    }
    Path outDir = Path.of(args[0]);
    Files.createDirectories(outDir);
    for (Map.Entry<String, String> entry : DomainSchemas.generate().entrySet()) {
      Path file = outDir.resolve(entry.getKey() + ".schema.json");
      Files.writeString(file, entry.getValue(), StandardCharsets.UTF_8);
    }
  }
}

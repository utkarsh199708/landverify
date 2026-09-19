package com.titleengine.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Exports one JSON Schema (draft 2020-12) per §3 entity (HLD §3, §11 "100% finding traceability
 * enforced at schema level"). Output is byte-stable: object keys are emitted in sorted order and
 * the text is written by a fixed 2-space pretty-printer, so the same domain always yields the same
 * bytes (non-negotiable 8, Determinism). No wall-clock or random enters here.
 */
public final class DomainSchemas {

  private DomainSchemas() {}

  // The ten §3 entities plus the first-class Gap. Value types (evidence, facts, ...) are pulled in
  // as $defs by the generator; they are not top-level entities.
  private static final List<Class<?>> ENTITIES =
      List.of(
          Case.class,
          Parcel.class,
          Document.class,
          Party.class,
          TitleLink.class,
          Chain.class,
          Encumbrance.class,
          LitigationHit.class,
          Finding.class,
          Report.class,
          Gap.class);

  /** Entity simple name → draft 2020-12 JSON Schema text (2-space indent, trailing newline). */
  public static SortedMap<String, String> generate() {
    SchemaGenerator generator = new SchemaGenerator(buildConfig());
    SortedMap<String, String> out = new TreeMap<>();
    for (Class<?> entity : ENTITIES) {
      JsonNode schema = generator.generateSchema(entity);
      out.put(entity.getSimpleName(), canonical(schema));
    }
    return out;
  }

  private static SchemaGeneratorConfig buildConfig() {
    SchemaGeneratorConfigBuilder builder =
        new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
    builder.with(new JacksonModule());
    builder.with(Option.FLATTENED_ENUMS);
    // Unwrap Optional<T> to T so an optional field describes its actual value type, and mark such
    // fields nullable. This keeps the schema faithful to the record shape (HLD §3).
    builder
        .forFields()
        .withTargetTypeOverridesResolver(
            field -> {
              var type = field.getType();
              if (type != null && type.getErasedType() == Optional.class) {
                var inner = field.getContext().getTypeParameterFor(type, Optional.class, 0);
                return inner == null ? null : Collections.singletonList(inner);
              }
              return null;
            });
    builder
        .forFields()
        .withNullableCheck(
            field -> {
              var type = field.getType();
              return type != null && type.getErasedType() == Optional.class ? Boolean.TRUE : null;
            });
    return builder.build();
  }

  private static String canonical(JsonNode node) {
    StringBuilder sb = new StringBuilder();
    write(node, sb, 0);
    sb.append('\n');
    return sb.toString();
  }

  private static void write(JsonNode node, StringBuilder sb, int depth) {
    if (node.isObject()) {
      List<String> names = new ArrayList<>();
      node.fieldNames().forEachRemaining(names::add);
      Collections.sort(names);
      if (names.isEmpty()) {
        sb.append("{}");
        return;
      }
      sb.append("{\n");
      for (int i = 0; i < names.size(); i++) {
        indent(sb, depth + 1);
        sb.append(new TextNode(names.get(i)).toString()).append(": ");
        write(node.get(names.get(i)), sb, depth + 1);
        sb.append(i < names.size() - 1 ? ",\n" : "\n");
      }
      indent(sb, depth);
      sb.append('}');
    } else if (node.isArray()) {
      if (node.isEmpty()) {
        sb.append("[]");
        return;
      }
      sb.append("[\n");
      for (int i = 0; i < node.size(); i++) {
        indent(sb, depth + 1);
        write(node.get(i), sb, depth + 1);
        sb.append(i < node.size() - 1 ? ",\n" : "\n");
      }
      indent(sb, depth);
      sb.append(']');
    } else {
      // Scalars (string, number, boolean, null) serialise deterministically via
      // JsonNode.toString().
      sb.append(node.toString());
    }
  }

  private static void indent(StringBuilder sb, int depth) {
    sb.append("  ".repeat(depth));
  }
}

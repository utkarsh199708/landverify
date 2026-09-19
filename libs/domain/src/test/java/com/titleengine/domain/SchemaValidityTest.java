package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Each generated schema is itself a well-formed draft 2020-12 JSON Schema. */
class SchemaValidityTest {

  @Test
  void every_schema_parses_as_draft_2020_12() throws Exception {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    ObjectMapper mapper = new ObjectMapper();
    for (Map.Entry<String, String> entry : DomainSchemas.generate().entrySet()) {
      JsonNode node = mapper.readTree(entry.getValue());
      JsonSchema schema = factory.getSchema(node);
      assertThat(schema).as("valid schema for " + entry.getKey()).isNotNull();
    }
  }
}

package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Money and area are integer types; double is allowed only for confidences and bounding boxes. */
class MoneyAndAreaTest {

  // The only fields that may legitimately be a floating-point number (HLD §8): a confidence and the
  // four normalised bounding-box coordinates and litigation match confidence.
  private static final Set<String> ALLOWED_DOUBLE_NAMES =
      Set.of("confidence", "matchConfidence", "x", "y", "w", "h");

  @Test
  void no_double_fields_in_model() {
    List<String> violations = new ArrayList<>();
    for (Class<?> type : DomainReflection.allDomainClasses()) {
      if (!type.isRecord()) {
        continue;
      }
      for (RecordComponent component : type.getRecordComponents()) {
        Class<?> t = component.getType();
        if ((t == double.class || t == float.class)
            && !ALLOWED_DOUBLE_NAMES.contains(component.getName())) {
          violations.add(type.getSimpleName() + "#" + component.getName());
        }
      }
    }
    assertThat(violations).as("money and area must be integer types, never double/float").isEmpty();
  }
}

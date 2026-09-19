package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Every enum with a wire() name round-trips through fromWire and rejects unknown names. */
class WireNamesTest {

  @Test
  void every_enum_round_trips() throws ReflectiveOperationException {
    List<Class<?>> enums = new ArrayList<>();
    for (Class<?> type : DomainReflection.allDomainClasses()) {
      if (type.isEnum() && hasWire(type)) {
        enums.add(type);
      }
    }
    assertThat(enums).as("expected the domain enums with wire()").hasSize(16);

    for (Class<?> type : enums) {
      Method wire = type.getMethod("wire");
      Method fromWire = type.getMethod("fromWire", String.class);
      for (Object constant : type.getEnumConstants()) {
        String wireValue = (String) wire.invoke(constant);
        assertThat(fromWire.invoke(null, wireValue)).isEqualTo(constant);
      }
      assertThatThrownBy(() -> invoke(fromWire, "__no_such_wire__"))
          .hasCauseInstanceOf(IllegalArgumentException.class);
    }
  }

  private static void invoke(Method fromWire, String arg) throws ReflectiveOperationException {
    fromWire.invoke(null, arg);
  }

  private static boolean hasWire(Class<?> type) {
    try {
      type.getMethod("wire");
      type.getMethod("fromWire", String.class);
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }
}

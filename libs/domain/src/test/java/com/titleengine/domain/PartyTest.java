package com.titleengine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Executable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Non-negotiable 4: category never derives from a name; PAN is stored masked (HLD §9). */
class PartyTest {

  @Test
  void pan_must_be_masked() {
    // A clear PAN (no masking Xs) is rejected; a masked one (length 10, >= 4 'X') is accepted.
    assertThatThrownBy(() -> party(Optional.of("ABCDE1234F")))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> party(Optional.of("ABCX1234X"))) // length 9
        .isInstanceOf(IllegalArgumentException.class);
    Party ok = party(Optional.of("ABCXX123XX"));
    assertThat(ok.panMasked()).contains("ABCXX123XX");
  }

  private static Party party(Optional<String> pan) {
    return new Party(
        new PartyId("p1"), "Ram Kumar", List.of(), PartyRole.HOLDER, List.of(), pan, List.of());
  }

  @Test
  void no_member_derives_category_from_name() {
    List<String> violations = new ArrayList<>();
    for (Class<?> type : DomainReflection.allDomainClasses()) {
      if (!Modifier.isPublic(type.getModifiers())) {
        continue;
      }
      for (Executable member : members(type)) {
        if (isAllowedException(member)) {
          continue;
        }
        if (takesNameLike(member) && referencesCategory(returnedType(member))) {
          violations.add(type.getName() + "#" + member.getName());
        }
      }
    }
    assertThat(violations)
        .as("no method/constructor may turn a String or NameVariant into a Category/CategoryFact")
        .isEmpty();
  }

  private static List<Executable> members(Class<?> type) {
    List<Executable> all = new ArrayList<>();
    for (Executable m : type.getDeclaredMethods()) {
      all.add(m);
    }
    for (Executable c : type.getDeclaredConstructors()) {
      all.add(c);
    }
    return all;
  }

  // Category.fromWire(String) is the one sanctioned String→Category door; valueOf(String) is the
  // intrinsic enum lookup. An enum's own private constructor merely stores its wire literal — that
  // is not deriving a category from a name, so those are exempt too.
  private static boolean isAllowedException(Executable member) {
    if (member instanceof java.lang.reflect.Constructor<?> && member.getDeclaringClass().isEnum()) {
      return true;
    }
    return member.getDeclaringClass() == Category.class
        && (member.getName().equals("fromWire") || member.getName().equals("valueOf"));
  }

  private static Type returnedType(Executable member) {
    return (member instanceof java.lang.reflect.Method method)
        ? method.getGenericReturnType()
        : member.getDeclaringClass();
  }

  private static boolean takesNameLike(Executable member) {
    for (Class<?> p : member.getParameterTypes()) {
      if (p == String.class || p == NameVariant.class) {
        return true;
      }
    }
    return false;
  }

  private static boolean referencesCategory(Type type) {
    if (type instanceof Class<?> c) {
      return c == Category.class || c == CategoryFact.class;
    }
    if (type instanceof ParameterizedType p) {
      for (Type arg : p.getActualTypeArguments()) {
        if (referencesCategory(arg)) {
          return true;
        }
      }
      return referencesCategory(p.getRawType());
    }
    if (type instanceof GenericArrayType g) {
      return referencesCategory(g.getGenericComponentType());
    }
    if (type instanceof WildcardType w) {
      for (Type b : w.getUpperBounds()) {
        if (referencesCategory(b)) {
          return true;
        }
      }
    }
    return false;
  }
}

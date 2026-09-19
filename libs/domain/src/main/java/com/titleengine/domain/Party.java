package com.titleengine.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A person or entity, kept separate from any single document because the same person is spelled
 * many ways across decades (HLD §3). Category appears only inside {@link CategoryFact} — this
 * record has no method or constructor that derives a {@link Category} from a name or any other
 * attribute (non-negotiable 4). PAN, if present, is already masked (HLD §9); no clear government-ID
 * number is ever a field here (non-negotiable 6).
 */
public record Party(
    PartyId id,
    String canonicalName,
    List<NameVariant> variants,
    PartyRole role,
    List<CategoryFact> categoryFacts,
    Optional<String> panMasked,
    List<Relationship> relationships) {
  public Party {
    Objects.requireNonNull(id, "id");
    Guards.requireText(canonicalName, "canonicalName");
    variants = Guards.copyList(variants, "variants");
    Objects.requireNonNull(role, "role");
    categoryFacts = Guards.copyList(categoryFacts, "categoryFacts");
    Objects.requireNonNull(panMasked, "panMasked");
    relationships = Guards.copyList(relationships, "relationships");
    panMasked.ifPresent(Party::requireMaskedPan);
  }

  // A masked PAN is length 10 with at least four masking 'X' characters (HLD §9). We never store
  // the
  // clear value, so this only ever sees the already-masked form.
  private static void requireMaskedPan(String value) {
    long crosses = value.chars().filter(c -> c == 'X').count();
    if (value.length() != 10 || crosses < 4) {
      throw new IllegalArgumentException("panMasked must be a masked PAN (length 10, >= 4 'X')");
    }
  }
}

package com.titleengine.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * One transfer of title from parties to parties, backed by documents (HLD §3, §6). A recorded link
 * must cite at least one supporting document; inferred and customary links may stand on reasoning.
 */
public record TitleLink(
    LinkId id,
    List<PartyId> fromParties,
    List<PartyId> toParties,
    InstrumentType instrument,
    LocalDate date,
    Optional<String> registrationRef,
    Optional<Money> stampPaid,
    Optional<Money> consideration,
    Optional<String> dcSanctionRef,
    MutationStatus mutation,
    List<DocumentId> supportingDocuments,
    ValidationStatus validation,
    LinkOrigin origin) {
  public TitleLink {
    Objects.requireNonNull(id, "id");
    fromParties = Guards.requireNonEmpty(fromParties, "fromParties");
    toParties = Guards.requireNonEmpty(toParties, "toParties");
    Objects.requireNonNull(instrument, "instrument");
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(registrationRef, "registrationRef");
    Objects.requireNonNull(stampPaid, "stampPaid");
    Objects.requireNonNull(consideration, "consideration");
    Objects.requireNonNull(dcSanctionRef, "dcSanctionRef");
    Objects.requireNonNull(mutation, "mutation");
    supportingDocuments = Guards.copyList(supportingDocuments, "supportingDocuments");
    Objects.requireNonNull(validation, "validation");
    Objects.requireNonNull(origin, "origin");
    if (origin == LinkOrigin.RECORDED && supportingDocuments.isEmpty()) {
      throw new IllegalArgumentException("recorded links need at least one supporting document");
    }
  }
}

package com.titleengine.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The case that owns everything (HLD §3, §5). {@code createdAt} is an input, never read from the
 * clock (non-negotiable 8). Tenant id is on the record because tenancy is on every record (HLD §9).
 */
public record Case(
    CaseId id,
    TenantId tenantId,
    String lenderRef,
    ParcelId parcelId,
    List<PartyId> borrowerPartyIds,
    CaseState status,
    OffsetDateTime createdAt) {
  public Case {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(tenantId, "tenantId");
    Guards.requireText(lenderRef, "lenderRef");
    Objects.requireNonNull(parcelId, "parcelId");
    borrowerPartyIds = Guards.copyList(borrowerPartyIds, "borrowerPartyIds");
    Objects.requireNonNull(status, "status");
    Objects.requireNonNull(createdAt, "createdAt");
  }
}

package com.titleengine.domain;

import java.util.List;
import java.util.Objects;

/**
 * A litigation match against a party cluster and parcel (HLD §3, §6). Always surfaced even at low
 * confidence; {@code partiesMatched} may be empty when only the parcel matched.
 */
public record LitigationHit(
    String caseNumber,
    Forum forum,
    List<PartyId> partiesMatched,
    double matchConfidence,
    String stage,
    String relevance) {
  public LitigationHit {
    Guards.requireText(caseNumber, "caseNumber");
    Objects.requireNonNull(forum, "forum");
    partiesMatched = Guards.copyList(partiesMatched, "partiesMatched");
    Guards.requireUnitInterval(matchConfidence, "matchConfidence");
    Guards.requireText(stage, "stage");
    Guards.requireText(relevance, "relevance");
  }
}

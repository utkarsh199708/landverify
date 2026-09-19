package com.titleengine.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * A parcel of land (HLD §3). Its kism is a sourced dated fact ({@link KismFact}) because tenure
 * decides transferability before ownership is even examined (HLD §6).
 */
public record Parcel(
    ParcelId id,
    String district,
    String anchal,
    String mauza,
    String thanaNumber,
    String khataNumber,
    String plotNumber,
    KismFact kism,
    Optional<String> csKhatianRef,
    Optional<String> rsKhatianRef,
    Optional<String> flatOrHoldingNumber,
    Optional<Area> area,
    Optional<String> ulpin) {
  public Parcel {
    Objects.requireNonNull(id, "id");
    Guards.requireText(district, "district");
    Guards.requireText(anchal, "anchal");
    Guards.requireText(mauza, "mauza");
    Guards.requireText(thanaNumber, "thanaNumber");
    Guards.requireText(khataNumber, "khataNumber");
    Guards.requireText(plotNumber, "plotNumber");
    Objects.requireNonNull(kism, "kism");
    Objects.requireNonNull(csKhatianRef, "csKhatianRef");
    Objects.requireNonNull(rsKhatianRef, "rsKhatianRef");
    Objects.requireNonNull(flatOrHoldingNumber, "flatOrHoldingNumber");
    Objects.requireNonNull(area, "area");
    Objects.requireNonNull(ulpin, "ulpin");
  }
}

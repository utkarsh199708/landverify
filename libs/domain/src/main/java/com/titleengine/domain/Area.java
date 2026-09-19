package com.titleengine.domain;

/** Parcel area as an integer of square metres times 100; never a float (HLD §3). */
public record Area(long squareMetresTimes100) {
  public Area {
    if (squareMetresTimes100 < 0L) {
      throw new IllegalArgumentException("squareMetresTimes100 must not be negative");
    }
  }
}

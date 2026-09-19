package com.titleengine.domain;

/** A grounding region on a page, normalised to 0..1 with positive width and height (HLD §8). */
public record BoundingBox(double x, double y, double w, double h) {
  public BoundingBox {
    Guards.requireUnitInterval(x, "x");
    Guards.requireUnitInterval(y, "y");
    Guards.requireUnitInterval(w, "w");
    Guards.requireUnitInterval(h, "h");
    if (w <= 0.0d || h <= 0.0d) {
      throw new IllegalArgumentException("w and h must be > 0");
    }
  }
}

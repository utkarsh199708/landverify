package com.titleengine.events;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Lower-case hex SHA-256 of a canonical JSON payload. Pure and deterministic: the same input always
 * yields the same digest, and nothing time- or random-dependent enters here (CLAUDE.md
 * non-negotiable 8, Determinism). Canonicalisation of the JSON itself is the caller's job (M1-T2).
 */
public final class PayloadHash {

  private PayloadHash() {}

  /** Returns the lower-case hex SHA-256 of {@code canonicalJson} (UTF-8 encoded). */
  public static String sha256Hex(String canonicalJson) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(canonicalJson.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder(bytes.length * 2);
      for (byte b : bytes) {
        hex.append(Character.forDigit((b >> 4) & 0xF, 16));
        hex.append(Character.forDigit(b & 0xF, 16));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}

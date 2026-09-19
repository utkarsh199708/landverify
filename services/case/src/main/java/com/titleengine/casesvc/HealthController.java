package com.titleengine.casesvc;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Liveness endpoint. Returns the fixed contract {@code
 * {"status":"UP","service":"case","version":…}} (M0-T4 brief). No authentication here — auth on
 * endpoints is M1-T6 / M7-T1.
 */
@RestController
public class HealthController {

  private final String service;
  private final String version;

  public HealthController(@Value("${spring.application.name}") String service) {
    this.service = service;
    // Implementation-Version is stamped into the boot jar manifest; "dev" when run from classes.
    this.version =
        Optional.ofNullable(getClass().getPackage().getImplementationVersion()).orElse("dev");
  }

  @GetMapping("/health")
  public HealthResponse health() {
    return new HealthResponse("UP", service, version);
  }

  /** Response body; record component order fixes the JSON field order. */
  public record HealthResponse(String status, String service, String version) {}
}

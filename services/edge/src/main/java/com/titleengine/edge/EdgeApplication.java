package com.titleengine.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot entry point for the edge service (HLD §4 System architecture). Only exposes
 * {@code GET /health} at this milestone; the Kafka consumer and stage logic land in later
 * milestones.
 */
@SpringBootApplication
public class EdgeApplication {

  public static void main(String[] args) {
    SpringApplication.run(EdgeApplication.class, args);
  }
}

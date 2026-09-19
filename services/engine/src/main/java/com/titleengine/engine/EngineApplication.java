package com.titleengine.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot entry point for the title engine service (HLD §4 System architecture). Only
 * exposes {@code GET /health} at this milestone; rule evaluation and the Kafka consumer land in
 * later milestones.
 */
@SpringBootApplication
public class EngineApplication {

  public static void main(String[] args) {
    SpringApplication.run(EngineApplication.class, args);
  }
}

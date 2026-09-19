package com.titleengine.casesvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot entry point for the case service (HLD §4 System architecture). Only exposes
 * {@code GET /health} at this milestone; the Kafka consumer and stage logic land in later
 * milestones. Package is {@code casesvc} because {@code case} is a Java reserved word.
 */
@SpringBootApplication
public class CaseApplication {

  public static void main(String[] args) {
    SpringApplication.run(CaseApplication.class, args);
  }
}

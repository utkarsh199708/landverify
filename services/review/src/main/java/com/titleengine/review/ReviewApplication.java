package com.titleengine.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot entry point for the review and report service (HLD §4 System architecture).
 * Only exposes {@code GET /health} at this milestone; the reviewer workflow and Kafka consumer land
 * in later milestones.
 */
@SpringBootApplication
public class ReviewApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReviewApplication.class, args);
  }
}

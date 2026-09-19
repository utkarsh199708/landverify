// services:case — Spring Boot service (com.titleengine.casesvc).
// Depends on the shared libraries only; never on another service.
plugins {
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
}

dependencies {
  implementation(project(":libs:domain"))
  implementation(project(":libs:events"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// bootJar is the only artifact; disable the plain jar so build/libs holds exactly one
// jar and the Dockerfile can copy it by wildcard.
tasks.named("jar") { enabled = false }

// services:engine — Spring Boot service (com.titleengine.engine).
// Additionally depends on libs:rules. Never depends on another service.
plugins {
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
}

dependencies {
  implementation(project(":libs:domain"))
  implementation(project(":libs:events"))
  implementation(project(":libs:rules"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

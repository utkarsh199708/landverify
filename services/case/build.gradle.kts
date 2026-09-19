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

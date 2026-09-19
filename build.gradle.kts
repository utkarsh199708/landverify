import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.plugins.quality.CheckstyleExtension

// Root build applies one shared Java convention to every subproject (HLD §4, §14).
// Module build files stay a few lines: services add Spring Boot and their library deps,
// libraries add nothing. No service depends on another service.
plugins {
  alias(libs.plugins.spring.boot) apply false
  alias(libs.plugins.spring.dependency.management) apply false
  alias(libs.plugins.spotless) apply false
}

subprojects {
  apply(plugin = "java-library")
  apply(plugin = "checkstyle")
  apply(plugin = "com.diffplug.spotless")

  repositories {
    mavenCentral()
  }

  // Java 21 toolchain everywhere; the build does not depend on the JDK on PATH.
  configure<JavaPluginExtension> {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
  }

  tasks.withType<JavaCompile>().configureEach {
    // Fail the build on any warning; surface every lint category.
    options.compilerArgs.addAll(listOf("-Werror", "-Xlint:all"))
    options.encoding = "UTF-8"
  }

  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
  }

  configure<SpotlessExtension> {
    java {
      googleJavaFormat()
      target("src/**/*.java")
      targetExclude("build/**")
    }
  }

  configure<CheckstyleExtension> {
    toolVersion = "10.18.1"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
  }

  // JUnit 5 + AssertJ for every module. The version-catalog `libs` accessor is not
  // available inside `subprojects {}`, so these coordinates are pinned inline here;
  // Spring versions stay in gradle/libs.versions.toml and are applied in each service.
  dependencies {
    "testImplementation"(platform("org.junit:junit-bom:5.10.3"))
    "testImplementation"("org.junit.jupiter:junit-jupiter")
    "testImplementation"("org.assertj:assertj-core:3.26.3")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
  }
}

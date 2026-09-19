import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.kotlin.dsl.getByType

// Root build applies one shared Java convention to every leaf subproject (HLD §4, §14).
// Module build files stay a few lines: services add Spring Boot and their library deps,
// libraries add nothing. No service depends on another service.
plugins {
  alias(libs.plugins.spring.boot) apply false
  alias(libs.plugins.spring.dependency.management) apply false
  alias(libs.plugins.spotless) apply false
}

// The version-catalog accessor `libs` is not generated inside `subprojects {}` / `configure(...)`,
// so resolve the catalog once here and look up test coordinates by name (M1-T1 Behaviour 9b).
val catalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

// Apply the convention to leaf modules only. `:libs` and `:services` are container nodes created by
// the nested include() in settings.gradle.kts; a stray src/ under them must not be silently built
// (M1-T1 Behaviour 9a, deferred from M0-T1).
configure(subprojects.filter { it.childProjects.isEmpty() }) {
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

  // JUnit 5 + AssertJ for every module, sourced from the version catalog (M1-T1 Behaviour 9b);
  // Spring versions stay in gradle/libs.versions.toml and are applied in each service.
  dependencies {
    "testImplementation"(platform(catalog.findLibrary("junit-bom").get()))
    "testImplementation"(catalog.findLibrary("junit-jupiter").get())
    "testImplementation"(catalog.findLibrary("assertj-core").get())
    "testRuntimeOnly"(catalog.findLibrary("junit-platform-launcher").get())
  }
}

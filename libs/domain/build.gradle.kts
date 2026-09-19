// libs:domain — immutable domain model + JSON Schema export (HLD §3, §11). Depends on no other
// module. Jackson + victools are used only by DomainSchemas/SchemaExporter to emit the committed
// schemas; the records themselves need nothing at runtime.

dependencies {
  implementation(libs.jackson.databind)
  implementation(libs.jackson.datatype.jdk8)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.victools.generator)
  implementation(libs.victools.module.jackson)

  testImplementation(libs.json.schema.validator)
}

// Writes schemas/domain/<Entity>.schema.json for the ten §3 entities plus Gap. Deterministic:
// running it twice on a clean tree changes nothing (non-negotiable 8). See DomainSchemas.
tasks.register<JavaExec>("exportSchemas") {
  group = "documentation"
  description = "Export the committed JSON Schemas for the domain model."
  dependsOn(tasks.named("classes"))
  mainClass.set("com.titleengine.domain.SchemaExporter")
  classpath = sourceSets["main"].runtimeClasspath
  args(rootProject.layout.projectDirectory.dir("schemas/domain").asFile.absolutePath)
}

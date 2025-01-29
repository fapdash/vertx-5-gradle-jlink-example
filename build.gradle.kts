import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.example.gradle.plugins.redeploy")
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "5.0.0.CR4"
val junitJupiterVersion = "5.9.1"

val mainVerticleModule = "com.example.module_example"
val mainVerticleClassName = "com.example.module_example.MainVerticle"
val launcherModule = "io.vertx.launcher.application"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
  mainModule.set(mainVerticleModule)
  mainClass.set(mainVerticleClassName)
}

tasks.named<JavaExec>("run") {
    args = listOf(mainVerticleClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-launcher-application")
  implementation("io.vertx:vertx-web")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "5.0.0.CR3"
val junitJupiterVersion = "5.9.1"

val launcherModule = "com.example.module_example"
val launcherClassName = "com.example.module_example.MainVerticle"

application {
  mainModule.set(launcherModule)
  mainClass.set(launcherClassName)
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


tasks.register<JavaExec>("jlink") {
    group = "build"

    dependsOn(project.tasks.first { it.name.contains("build") })

    mainClass.set("jdk.tools.jlink.internal.Main")
    mainModule.set("jdk.jlink")

    var modulePath = files()
    modulePath.from(configurations.runtimeClasspath)
    modulePath.from(tasks.jar)
    args = listOf(
            "--module-path",
            modulePath.joinToString(":"),
            "--add-modules",
            "java.base,java.compiler,java.logging,java.naming,jdk.unsupported,${application.mainModule.get()}",
            "--output",
            "build/jlink-image-test",
            "--launcher",
            "launch=${application.mainModule.get()}/${application.mainClass.get()}"
    )
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

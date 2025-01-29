plugins {
    id("java-gradle-plugin")
    id("application")
}

group = "com.example.gradle.plugins"

// plugins implemented as classes: we need to define IDs here
gradlePlugin {
    plugins.create("RedeployPlugin") {
        id = "${project.group}.redeploy"
        implementationClass = "${project.group}.${name}"
    }
}

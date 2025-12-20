plugins {
    id("build-logic.kotlin-dsl-gradle-plugin")
}

dependencies {
    implementation(project(":basics"))
    implementation(project(":build-parameters"))
    implementation("com.github.vlsi.gradle-extensions:com.github.vlsi.gradle-extensions.gradle.plugin:3.0.1")
    implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:4.3.0")
}

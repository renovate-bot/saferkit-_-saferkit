plugins {
    id("build-logic.kotlin-dsl-gradle-plugin")
}

dependencies {
    implementation(project(":basics"))
    implementation(project(":build-parameters"))
    implementation(project(":verification"))
    implementation("com.github.vlsi.crlf:com.github.vlsi.crlf.gradle.plugin:3.0.1")
    implementation("com.github.vlsi.gradle-extensions:com.github.vlsi.gradle-extensions.gradle.plugin:3.0.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
}

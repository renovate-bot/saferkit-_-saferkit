import com.github.vlsi.gradle.dsl.configureEach
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("java-library")
    id("build-logic.java")
    id("build-logic.test-base")
    id("com.github.vlsi.gradle-extensions")
    kotlin("jvm")
}

java {
    withSourcesJar()
}

val String.v: String get() = rootProject.extra["$this.version"] as String

val kotlinTarget = providers.gradleProperty("kotlin.api").map { KotlinVersion.fromVersion(it) }

tasks.configureEach<KotlinJvmCompile> {
    compilerOptions {
        if (!name.startsWith("compileTest")) {
            apiVersion = kotlinTarget
            languageVersion = kotlinTarget
        }
        freeCompilerArgs.add("-Xjvm-default=all")
        val jdkRelease = buildParameters.targetJavaVersion.let {
            when {
                it < 9 -> "1.8"
                else -> it.toString()
            }
        }
        // jdk-release requires Java 9+
        buildParameters.buildJdkVersion
            .takeIf { it > 8 }
            ?.let {
                freeCompilerArgs.add("-Xjdk-release=$jdkRelease")
            }
        jvmTarget = JvmTarget.fromTarget(jdkRelease)
    }
}

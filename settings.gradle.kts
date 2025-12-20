rootProject.name = "saferkit"

pluginManagement {
    plugins {
        kotlin("jvm") version "2.3.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

if (JavaVersion.current() < JavaVersion.VERSION_17) {
    throw UnsupportedOperationException("Please use Java 17 or 21 for launching Gradle, the current Java is ${JavaVersion.current().majorVersion}")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("build-logic")

include("safer-bom")
include("safer-paths")
include("safer-strings")

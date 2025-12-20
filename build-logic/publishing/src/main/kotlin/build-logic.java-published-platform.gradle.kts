plugins {
    id("java-platform")
    id("build-logic.reproducible-builds")
    id("build-logic.publish-to-central")
}

publishing {
    publications {
        create<MavenPublication>("mavenJavaPlatform") {
            from(components["javaPlatform"])
        }
    }
}

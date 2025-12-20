plugins {
    id("build-logic.java-published-library")
    id("build-logic.test-junit5")
}

dependencies {
    api("org.jspecify:jspecify:1.0.0")

    constraints {
        api(projects.saferBom)
    }
}

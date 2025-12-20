plugins {
    id("build-logic.java-published-library")
    id("build-logic.test-junit5")
}

dependencies {
    constraints {
        api(projects.saferBom)
    }
}

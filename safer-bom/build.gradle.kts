plugins {
    id("build-logic.java-published-platform")
}

dependencies {
    constraints {
        api(projects.saferPaths)
        api(projects.saferStrings)
    }
}

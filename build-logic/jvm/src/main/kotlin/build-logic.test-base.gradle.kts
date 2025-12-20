import com.github.vlsi.gradle.dsl.configureEach
import com.github.vlsi.gradle.properties.dsl.props

plugins {
    id("java-library")
    id("build-logic.build-params")
}

tasks.configureEach<Test> {
    buildParameters.testJdk?.let {
        javaLauncher.convention(javaToolchains.launcherFor(it))
    }
    testLogging {
        showStandardStreams = true
    }
    props.string("testExtraJvmArgs").trim().takeIf { it.isNotBlank() }?.let {
        jvmArgs(it.split(" ::: "))
    }
    // Pass the property to tests
    fun passProperty(name: String, default: String? = null) {
        val value = System.getProperty(name) ?: default
        value?.let { systemProperty(name, it) }
    }
    passProperty("user.language", "TR")
    passProperty("user.country", "tr")
    val props = System.getProperties()
    @Suppress("UNCHECKED_CAST")
    for (e in props.propertyNames() as `java.util`.Enumeration<String>) {
        if (e.startsWith("saferkit.")) {
            passProperty(e)
        }
    }
}

plugins {
    id("org.gradlex.build-parameters") version "1.4.4"
    id("com.github.vlsi.gradle-extensions") version "3.0.1"
    id("build-logic.kotlin-dsl-gradle-plugin")
}

buildParameters {
    // Other plugins can contribute parameters, so below list is not exhaustive
    enableValidation.set(false)
    pluginId("build-logic.build-params")
    integer("targetJavaVersion") {
        defaultValue.set(8)
        mandatory.set(true)
        description.set("Java version for source and target compatibility")
    }
    val projectName = "saferkit"
    integer("jdkBuildVersion") {
        defaultValue.set(21)
        mandatory.set(true)
        description.set("JDK version to use for building $projectName. If the value is 0, then the current Java is used. (see https://docs.gradle.org/8.4/userguide/toolchains.html#sec:consuming)")
    }
    string("jdkBuildVendor") {
        description.set("JDK vendor to use building $projectName (see https://docs.gradle.org/8.4/userguide/toolchains.html#sec:vendors)")
    }
    string("jdkBuildImplementation") {
        description.set("Vendor-specific virtual machine implementation to use building $projectName (see https://docs.gradle.org/8.4/userguide/toolchains.html#selecting_toolchains_by_virtual_machine_implementation)")
    }
    integer("jdkTestVersion") {
        description.set("JDK version to use for testing $projectName. If the value is 0, then the current Java is used. (see https://docs.gradle.org/current/userguide/toolchains.html#sec:vendors)")
    }
    string("jdkTestVendor") {
        description.set("JDK vendor to use testing $projectName (see https://docs.gradle.org/8.4/userguide/toolchains.html#sec:vendors)")
    }
    string("jdkTestImplementation") {
        description.set("Vendor-specific virtual machine implementation to use testing $projectName (see https://docs.gradle.org/8.4/userguide/toolchains.html#selecting_toolchains_by_virtual_machine_implementation)")
    }
    bool("enableErrorprone") {
        defaultValue.set(false)
        description.set("Run ErrorProne verifications")
    }
    bool("skipJavadoc") {
        defaultValue.set(false)
        description.set("Skip javadoc generation")
    }
    bool("failOnJavadocWarning") {
        defaultValue.set(true)
        description.set("Fail build on javadoc warnings")
    }
    bool("failOnJavacWarning") {
        defaultValue.set(true)
        description.set("Fail build on javac warnings")
    }
    bool("release") {
        defaultValue.set(false)
        description.set("Create a release version of the library (by default, -SNAPSHOT is used)")
    }
    group("centralPortal") {
        enumeration("publishingType") {
            defaultValue.set("AUTOMATIC")
            values.addAll("AUTOMATIC", "MANUAL")
            description.set("Publishing type")
        }
        integer("validationTimeout") {
            defaultValue.set(60)
            description.set("Timeout (minutes) to wait for Central Portal to validate the publication")
        }
    }
    group("signing") {
        group("pgp") {
            enumeration("enabled") {
                defaultValue.set("AUTO")
                values.addAll("AUTO", "OFF")
                description.set("Configures whether PGP signing should be enabled or not. By default (AUTO) signing is enabled for release versions")
            }
            enumeration("implementation") {
                defaultValue.set("IN_MEMORY")
                values.addAll("IN_MEMORY", "GPG_CLI")
                description.set("Configures PGP implementation to use. By default (IN_MEMORY) PGP keys are stored in memory")
            }
        }
    }
}

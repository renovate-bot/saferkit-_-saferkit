import buildparameters.signing.pgp.Enabled
import buildparameters.signing.pgp.Implementation
import com.github.vlsi.gradle.dsl.configureEach
import com.github.vlsi.gradle.publishing.dsl.simplifyXml
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("maven-publish")
    id("signing")
    id("build-logic.build-params")
    id("build-logic.publish-to-tmp-maven-repo")
    id("com.github.vlsi.gradle-extensions")
    id("com.gradleup.nmcp")
}

if (!buildParameters.release) {
    publishing {
        repositories {
            maven {
                name = "centralSnapshots"
                url = uri("https://central.sonatype.com/repository/maven-snapshots")
                credentials(PasswordCredentials::class)
            }
        }
    }
} else {
    if (buildParameters.signing.pgp.enabled == Enabled.AUTO) {
        signing {
            sign(publishing.publications)
            if (buildParameters.signing.pgp.implementation == Implementation.GPG_CLI) {
                useGpgCmd()
            } else {
                val pgpPrivateKey = System.getenv("SIGNING_PGP_PRIVATE_KEY")
                val pgpPassphrase = System.getenv("SIGNING_PGP_PASSPHRASE")
                val problems = project.serviceOf<Problems>()
                if (pgpPrivateKey.isNullOrBlank() || pgpPassphrase.isNullOrBlank()) {
                    throw problems.reporter.throwing(
                        IllegalArgumentException("PGP private key (SIGNING_PGP_PRIVATE_KEY) and passphrase (SIGNING_PGP_PASSPHRASE) must be set for signing the release artifacts"),
                        ProblemId.create(
                            "gpg_credentials_not_set",
                            "PGP private key (SIGNING_PGP_PRIVATE_KEY) and passphrase (SIGNING_PGP_PASSPHRASE) must be set for signing the release artifacts",
                            ProblemGroup.create("release_params", "Release parameters")
                        )
                    ) {
                        contextualLabel("Using in-memory PGP keys from the environment variables")
                        solution("Ensure SIGNING_PGP_PRIVATE_KEY and SIGNING_PGP_PASSPHRASE environment variables are set or use -Psigning.pgp.implementation=GPG_CLI to sign with gpg command line utility")
                        solution("Disable signing with -Psigning.pgp.enabled=OFF")
                    }
                }
                useInMemoryPgpKeys(
                    pgpPrivateKey,
                    pgpPassphrase
                )
            }
        }
    }
}

publishing {
    publications.configureEach<MavenPublication> {
        // Use the resolved versions in pom.xml
        // Gradle might have different resolution rules, so we set the versions
        // that were used in Gradle build/test.
        versionMapping {
            usage(Usage.JAVA_RUNTIME) {
                fromResolutionResult()
            }
            usage(Usage.JAVA_API) {
                fromResolutionOf("runtimeClasspath")
            }
        }
        pom {
            simplifyXml()
            name = project.findProperty("artifact.name") as? String
            description = project.description
            inceptionYear = "2025"
            url = "https://github.com/saferkit/saferkit"
            licenses {
                license {
                    name = "Apache-2.0"
                    url = "https://jdbc.postgresql.org/about/license.html"
                    comments = "Apache-2.0"
                    distribution = "repo"
                }
            }
            organization {
                name = "Saferkit"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
            developers {
                developer {
                    id = "vlsi"
                    name = "Vladimir Sitnikov"
                }
            }
            issueManagement {
                system = "GitHub issues"
                url = "https://github.com/saferkit/saferkit/issues"
            }
            scm {
                connection = "scm:git:https://github.com/saferkit/saferkit.git"
                developerConnection = "scm:git:https://github.com/saferkit/saferkit.git"
                url = "https://github.com/saferkit/saferkit"
                tag = "HEAD"
            }
        }
    }
}

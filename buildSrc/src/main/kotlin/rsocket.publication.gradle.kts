plugins {
    `maven-publish`
    signing
}

val versionSuffix: String? by project
if (!versionSuffix.isNullOrBlank()) {
    val versionString = project.version.toString()
    require(versionString.endsWith("-SNAPSHOT"))
    project.version = versionString.replace("-", "-$versionSuffix-")
    println("Current version: ${project.version}")
}

//empty javadoc for maven central
val javadocJar by tasks.registering(Jar::class) { archiveClassifier.set("javadoc") }

publishing {
    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

        pom {
            name.set(project.name)
            description.set(project.description)
            url.set("http://rsocket.io")

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("whyoleg")
                    name.set("Oleg Yukhnevich")
                    email.set("whyoleg@gmail.com")
                }
                developer {
                    id.set("OlegDokuka")
                    name.set("Oleh Dokuka")
                    email.set("oleh.dokuka@icloud.com")
                }
            }
            scm {
                connection.set("https://github.com/rsocket/rsocket-kotlin.git")
                developerConnection.set("https://github.com/rsocket/rsocket-kotlin.git")
                url.set("https://github.com/rsocket/rsocket-kotlin")
            }
        }
    }

    val githubUsername: String? by project
    val githubPassword: String? by project

    val sonatypeUsername: String? by project
    val sonatypePassword: String? by project

    val signingKey: String? by project
    val signingPassword: String? by project

    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/rsocket/rsocket-kotlin")
            credentials {
                username = githubUsername
                password = githubPassword
            }
        }
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }

    signing {
        isRequired = sonatypeUsername != null && sonatypePassword != null &&
                signingKey != null && signingPassword != null

        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publications)
    }
}

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.3/userguide/building_java_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */


// For local builds, use 0-SNAPSHOT. For CI builds, use the build number from CircleCI
val buildNumber = findProperty("buildNumber") as? String ?: "0-SNAPSHOT"
version = "1.0.$buildNumber"
group = "com.github.incept5"

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // publish to maven repositories
    `maven-publish`
}

dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.slf4j.api)

    // slf4j-simple does not contain an MDC adaptor so we need to use logback to check it works
    testImplementation(libs.logback.classic)
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest(libs.versions.kotlin.get())
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withJavadocJar()
    withSourcesJar()
}

// Configure Kotlin to target JVM 21
kotlin {
    jvmToolchain(21)

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "correlation"
            from(components["java"])
            
            // POM information is automatically included with sources and javadoc
            pom {
                name.set("Correlation Library")
                description.set("A library for correlation and trace IDs in distributed systems")
                url.set("https://github.com/incept5/correlation")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                
                developers {
                    developer {
                        id.set("incept5")
                        name.set("Incept5")
                        email.set("info@incept5.com")
                    }
                }
            }
        }
    }
    
    // Configure local Maven repository for local builds
    repositories {
        mavenLocal()
    }
}

// For JitPack compatibility
tasks.register("install") {
    dependsOn(tasks.named("publishToMavenLocal"))
}

// Always publish to local Maven repository after build for local development
tasks.named("build") {
    finalizedBy(tasks.named("publishToMavenLocal"))
}
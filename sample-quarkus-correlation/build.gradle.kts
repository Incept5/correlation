plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.quarkus)
}

dependencies {
    implementation(project(":"))
    
    implementation(libs.quarkus.resteasy)
    implementation(libs.quarkus.resteasy.jackson)
    implementation(libs.quarkus.kotlin)
    
    testImplementation(libs.quarkus.junit5)
    testImplementation(libs.rest.assured)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_21.toString()
    kotlinOptions.javaParameters = true
}

// Disable Quarkus native build
tasks.named("quarkusBuild") {
    enabled = false
}

// Disable Quarkus native tasks
tasks.matching { task -> task.name.contains("Native") }.configureEach {
    enabled = false
}

// Disable Quarkus app parts build
tasks.named("quarkusAppPartsBuild") {
    enabled = false
}

// Configure Quarkus to disable Jandex indexing
tasks.withType<JavaExec> {
    systemProperties["quarkus.jandex.index-dependency-jars"] = "false"
    systemProperties["quarkus.jandex.index-jars"] = "false"
}

// Add Quarkus configuration to disable Jandex indexing
tasks.named("processResources") {
    doFirst {
        file("src/main/resources/application.properties").appendText("""
            quarkus.index-dependency=false
            quarkus.jandex.index-dependency-jars=false
            quarkus.jandex.index-jars=false
        """.trimIndent())
    }
}

// Clean the problematic index files before tests
tasks.named("test") {
    dependsOn("cleanTestIndexFiles")
}

// Create a dedicated task to clean index files
tasks.register("cleanTestIndexFiles") {
    doLast {
        delete(fileTree("build/classes/kotlin/test") {
            include("**/*.idx")
        })
        delete(fileTree("build/classes/java/test") {
            include("**/*.idx")
        })
    }
}

// Also clean index files before compiling tests
tasks.named("compileTestKotlin") {
    dependsOn("cleanTestIndexFiles")
}

// Add Quarkus test configuration
tasks.named("test") {
    doFirst {
        System.setProperty("quarkus.jandex.index-dependency-jars", "false")
        System.setProperty("quarkus.jandex.index-jars", "false")
        System.setProperty("quarkus.index-dependency", "false")
    }
}
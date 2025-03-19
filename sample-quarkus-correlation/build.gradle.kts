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

// Clean the problematic index files before tests
tasks.named("test") {
    doFirst {
        delete(fileTree("build/classes/kotlin/test") {
            include("**/*.idx")
        })
    }
}
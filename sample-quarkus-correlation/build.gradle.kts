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

tasks.named("build") {
    dependsOn("quarkusDependencyManagement", "jar")
}
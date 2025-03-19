plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.quarkus)
}

dependencies {
    implementation(project(":"))
    implementation(enforcedPlatform(libs.quarkus.bom))


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

kotlin {
    jvmToolchain(21)
    
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        javaParameters.set(true)
    }
}








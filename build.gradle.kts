plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server core dependencies (from bundle)
    implementation(libs.bundles.ktor.server)

    // Additional Ktor features
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.caching.headers)

    // Logging
    implementation(libs.logback.classic)

    // GeoIP2
    implementation(libs.maxmind.geoip2)

    // Testing (from bundle)
    testImplementation(libs.bundles.ktor.testing)
}

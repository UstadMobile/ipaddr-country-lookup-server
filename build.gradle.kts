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
// Pass GEO_DATABASE_PATH to the run task JVM environment
tasks.named<JavaExec>("run") {
    environment("GEO_DATABASE_PATH", System.getenv("GEO_DATABASE_PATH") ?: "/home/user/databases/GeoLite2-Country.mmdb")
}
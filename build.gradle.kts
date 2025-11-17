plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.ustadmobile.countrylookupserver"
version = "0.0.1"

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.ustadmobile.countrylookupserver.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.caching.headers)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.logback.classic)
    implementation(libs.maxmind.geoip2)
    testImplementation(libs.bundles.ktor.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.ktor.client.content.negotiation)
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }

    systemProperty("file.encoding", "UTF-8")
    failFast = true
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.ustadmobile.countrylookupserver.ApplicationKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
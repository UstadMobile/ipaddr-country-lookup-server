package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*

fun Application.configureHTTP() {
    install(CallLogging) {
        level = Level.INFO
    }
}
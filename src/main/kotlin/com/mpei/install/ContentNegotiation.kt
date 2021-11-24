package com.mpei.install

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

fun Application.installContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}
package com.mpei.install

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun Application.installStatusPages() {
    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }
}
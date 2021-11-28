package com.mpei.routing

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database

fun Application.authenticateRouting(db: Database) {

    routing {

        authenticate("auth-digest") {

            get(authorization) {

                call.respond(true)

            }

            chatRouting(db = db)
        }
    }
}

private const val authorization = "/authorization"
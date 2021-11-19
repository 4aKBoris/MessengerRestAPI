package com.mpei

import com.mpei.db.DatabaseConnection
import io.ktor.application.*
import io.ktor.routing.*
import org.ktorm.database.Database

fun Application.authenticationRoutes() {
    val db = DatabaseConnection.database

    routing {
        post("register") {

        }
    }
}
package com.mpei.routing

import com.mpei.db.DatabaseConnection
import com.mpei.install.installAuthentication
import com.mpei.install.installContentNegotiation
import com.mpei.install.installStatusPages
import io.ktor.application.*

fun Application.configureRouting() {

    installStatusPages()

    installContentNegotiation()

    installAuthentication(db = db)

    noAuthorizationRouting(db = db)

    authenticateRouting(db = db)

}

private val db = DatabaseConnection.database
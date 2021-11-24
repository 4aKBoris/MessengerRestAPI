package com.mpei.routing

import com.mpei.db.DatabaseConnection
import com.mpei.db.entity.UserEntity
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.mapColumns
import org.ktorm.entity.sequenceOf

fun Application.authenticateRouting(db: Database) {

    routing {

        authenticate("auth-digest") {

            get(authorization) {

                val phoneNumber: String = call.parameters.getOrFail(PhoneNumber)

                val k = db.sequenceOf(UserEntity).filter { it.phoneNumber eq phoneNumber }.mapColumns { it.id }.first()

                call.respond(k ?: -1)
            }

            chatRouting(db = db)
        }
    }
}

private const val authorization = "/authorization"
private const val PhoneNumber = "phoneNumber"
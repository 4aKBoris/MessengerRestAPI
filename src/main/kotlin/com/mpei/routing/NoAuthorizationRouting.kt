package com.mpei.routing

import com.mpei.db.data.User
import com.mpei.db.entity.MessageEntity
import com.mpei.db.entity.UserEntity
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.count
import org.ktorm.entity.sequenceOf
import java.time.LocalDateTime

fun Application.noAuthorizationRouting(db: Database) {

    val m = MessageEntity

    routing {

        get(checkPhoneNumber) {

            val phoneNumber: String = call.parameters.getOrFail(PhoneNumber)

            call.respond(checkPhoneNumber(phoneNumber = phoneNumber, db = db))
        }

        post(registration) {
            val user = call.receive<User>()

            if (checkPhoneNumber(phoneNumber = user.phoneNumber, db = db)) call.respond(false)
            else {

                db.insert(UserEntity) {
                    set(it.phoneNumber, user.phoneNumber)
                    set(it.password, user.password)
                    set(it.firstName, user.firstName)
                    set(it.lastName, user.lastName)
                    set(it.icon, user.icon)
                }

                call.respond(true)

                val message = "Пользователь ${user.firstName} ${user.lastName} вошёл в чат"

                db.insert(m) {
                    set(m.userId, -1)
                    set(m.message, message)
                    set(m.dateTime, LocalDateTime.now())
                }
            }
        }
    }
}

private fun checkPhoneNumber(phoneNumber: String, db: Database) =
    db.sequenceOf(UserEntity).count { UserEntity.phoneNumber eq phoneNumber } != 0

private const val PhoneNumber = "phoneNumber"

const val checkPhoneNumber = "/checkPhoneNumber"

const val registration = "/registration"
package com.mpei.routing

import com.mpei.db.data.UserRegistration
import com.mpei.db.entity.Icon
import com.mpei.db.entity.MessageEntity
import com.mpei.db.entity.UserEntity
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.count
import org.ktorm.entity.sequenceOf
import java.io.File
import java.time.LocalDateTime
import java.util.*

private val m = MessageEntity

private val ue = UserEntity

fun Application.noAuthorizationRouting(db: Database) {

    routing {

        get(checkPhoneNumber) {

            val phoneNumber: String = call.parameters.getOrFail(PhoneNumber)

            call.respond(checkPhoneNumber(phoneNumber = phoneNumber, db = db))
        }

        get("/icon") {
            val id = call.parameters["id"]!!.toInt()

            val icon = db.from(Icon).select().where { (Icon.idUser eq id) }
                .map { it[Icon.icon] }.first() ?: File("avatar.jpg").readBytes()

            call.respondBytes(icon)
        }

        post(registration) {

            val user = call.receive<UserRegistration>()

            if (checkPhoneNumber(phoneNumber = user.phoneNumber, db = db)) call.respond(
                status = HttpStatusCode.Found,
                "Аккаунт на данный номер уже зарегистрирован!"
            )
            else {

                db.insert(UserEntity) {
                    set(it.phoneNumber, user.phoneNumber)
                    set(it.password, com.mpei.install.getDigest(user.phoneNumber, user.password))
                    set(it.firstName, user.firstName)
                    set(it.lastName, user.lastName)
                    set(it.registrationDate, LocalDateTime.now())
                }

                val icon = (if (user.icon == null) null else Base64.getDecoder().decode(user.icon))

                val idUser = getId(phoneNumber = user.phoneNumber, db = db)

                db.insert(Icon) {
                    set(it.idUser, idUser)
                    set(it.icon, icon)
                }

                val message = "Пользователь ${user.firstName} ${user.lastName} вошёл в чат"

                db.insert(m) {
                    set(m.userId, -1)
                    set(m.message, message)
                    set(m.dateTime, LocalDateTime.now())
                }

                call.respond(true)

            }
        }
    }
}

private fun getId(phoneNumber: String, db: Database) = db.from(ue).select(ue.id).where { ue.phoneNumber eq phoneNumber }.map { it[ue.id] }.first()!!

private fun checkPhoneNumber(phoneNumber: String, db: Database) =
    db.sequenceOf(ue).count { ue.phoneNumber eq phoneNumber } == 1

private const val PhoneNumber = "phoneNumber"

const val checkPhoneNumber = "/checkPhoneNumber"

const val registration = "/registration"

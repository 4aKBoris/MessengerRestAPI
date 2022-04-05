package com.mpei.routing

import com.mpei.db.data.*
import com.mpei.db.entity.IconEntity
import com.mpei.db.entity.MessageEntity
import com.mpei.db.entity.UserEntity
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.datetime.toKotlinLocalDateTime
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val ue = UserEntity

private val m = MessageEntity

fun Application.chatRouting(db: Database) {

    routing {

        authenticate("auth-digest") {

            get(chatUsers) {

                val users =
                    db.from(ue).select()
                        .map { User(it[ue.id]!!, it[ue.firstName]!!, it[ue.lastName]) }

                call.respond(users)
            }

            get(messages) {

                val messages = db.from(m).select().map {
                    Message(it[m.id]!!, it[m.message]!!, it[m.dateTime]!!.toKotlinLocalDateTime(), it[m.userId]!!)
                }

                call.respond(messages)
            }

            get(message) {

                val id = call.parameters["id"]!!.toInt()

                val messages = db.from(m).select().where { m.id greater id }.map {
                    Message(it[m.id]!!, it[m.message]!!, it[m.dateTime]!!.toKotlinLocalDateTime(), it[m.userId]!!)
                }

                call.respond(messages)

            }

            post(message) {

                val message = call.receive<MyMessage>()

                val phoneNumber = call.principal<UserIdPrincipal>()?.name

                val id = getId(phoneNumber!!, db)

                db.insert(m) {
                    set(it.message, message.message)
                    set(it.dateTime, LocalDateTime.now())
                    set(it.userId, id)
                }

                call.respond(true)
            }

            get(user) {

                val phoneNumber = call.principal<UserIdPrincipal>()?.name!!

                val fullUser =
                    db.from(ue).select().where { (ue.phoneNumber eq phoneNumber) }
                        .map {
                            FullUser(
                                id = it[ue.id]!!,
                                phoneNumber,
                                firstName = it[ue.firstName]!!,
                                lastName = it[ue.lastName]

                            )
                        }.first()
                call.respond(fullUser)
            }

            get(check) {
                call.respond(true)
            }

            get(password) {
                val phoneNumber = call.principal<UserIdPrincipal>()!!.name

                val password1 = call.parameters.getOrFail("password")

                val password2 =
                    db.from(ue).select(ue.password).where { ue.phoneNumber eq phoneNumber }.map { it[ue.password]!! }
                        .first()

                val password3 = com.mpei.install.getDigest(name = phoneNumber, password = password1)

                call.respond(password2.contentEquals(password3))
            }

            delete(user) {

                val phoneNumber = call.principal<UserIdPrincipal>()!!.name

                val name = db.from(ue).select(ue.firstName, ue.lastName).where { ue.phoneNumber eq phoneNumber }
                    .map { it[ue.firstName]!! to it[ue.lastName] }.first()

                db.delete(ue) {
                    (it.phoneNumber eq phoneNumber)
                }

                val message = "Пользователь ${name.first} ${name.second ?: ""} вышел из чата"

                db.insert(m) {
                    set(m.message, message)
                    set(m.userId, -1)
                    set(m.dateTime, LocalDateTime.now())
                }

                call.respond(true)
            }

            put(user) {
                val name = call.receive<Name>()

                val phoneNumber = call.principal<UserIdPrincipal>()!!.name

                db.update(ue) {
                    set(ue.firstName, name.firstName)
                    set(ue.lastName, name.lastName)
                    where { it.phoneNumber eq phoneNumber }
                }

                call.respond(true)
            }

            put(icon) {
                val icon = call.receive<Icon>()

                val phoneNumber = call.principal<UserIdPrincipal>()!!.name

                val idUser = getId(phoneNumber = phoneNumber, db = db)!!

                db.update(IconEntity) {
                    set(IconEntity.icon, icon.icon)
                    where { it.idUser eq idUser }
                }

                call.respond(true)
            }

            get("$user$data") {

                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")

                val id = call.parameters.getOrFail("id").toInt()

                val data = db.from(ue).select(ue.registrationDate).where { ue.id eq id }
                    .map { it[ue.registrationDate]!!.format(formatter) }.first()

                call.respondText(data)
            }


            put(password) {

                val password = call.receive<Password>()

                val phoneNumber = call.principal<UserIdPrincipal>()!!.name

                val newPassword = com.mpei.install.getDigest(name = phoneNumber, password = password.password)

                db.update(ue) {
                    set(ue.password, newPassword)
                    where { it.phoneNumber eq phoneNumber }
                }

                call.respond(true)
            }
        }
    }

}

private fun getId(phoneNumber: String, db: Database): Int? =
    db.from(ue).select(ue.id).where { ue.phoneNumber eq phoneNumber }.map { it[ue.id] }.first()?.toInt()

private const val chatUsers = "/chatUsers"

private const val message = "/message"

private const val messages = "/messages"

private const val user = "/user"

private const val data = "/data"

private const val icon = "/icon"

private const val password = "/password"

private const val check = "/check"

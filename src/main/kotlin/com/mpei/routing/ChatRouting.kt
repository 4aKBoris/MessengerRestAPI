package com.mpei.routing

import com.mpei.db.data.*
import com.mpei.db.entity.MessageEntity
import com.mpei.db.entity.UserEntity
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

fun Application.chatRouting(db: Database) {

    val ue = UserEntity

    val m = MessageEntity

    routing {

        get(chatUsers) {

            val users =
                db.from(ue).select()
                    .map { ChatUser(it[ue.id]!!, DataUser(it[ue.firstName]!!, it[ue.lastName], it[ue.icon])) }

            call.respond(users)
        }

        get(messages) {

            val messages = db.from(m).select().map {
                Message(it[m.message]!!, it[m.dateTime]!!.toKotlinLocalDateTime(), it[m.userId]!!)
            }

            call.respond(messages)
        }

        get(message) {

            val dateTime = LocalDateTime.now().minusSeconds(10)

            val messages = db.from(m).select().where { m.dateTime greaterEq dateTime }.map {
                Message(it[m.message]!!, it[m.dateTime]!!.toKotlinLocalDateTime(), it[m.userId]!!)
            }

            call.respond(messages)

        }

        post(message) {

            val message = call.receive<Message>()

            db.insert(m) {
                set(it.message, message.message)
                set(it.dateTime, message.dateTime.toJavaLocalDateTime())
                set(it.userId, message.userId)
            }

            call.respond(true)

        }

        delete(message) {
            val message = call.receive<String>()

            db.delete(m) {
                it.message eq message
            }
        }

        get(user) {
            val jsonString = call.parameters["data"]!!
            val data = Json.decodeFromString<LoginData>(jsonString)
            val fullUser =
                db.from(ue).select().where { (ue.phoneNumber eq data.phoneNumber) and (ue.password eq data.password) }
                    .map {
                        FullUser(
                            id = it[ue.id]!!,
                            user = User(
                                data = LoginData(phoneNumber = data.phoneNumber, password = data.password), DataUser(
                                    firstName = it[ue.firstName]!!, lastName = it[ue.lastName], icon = it[ue.icon]
                                )
                            )
                        )
                    }.first()
            call.respond(fullUser)
        }

        get(icon) {
            val jsonString = call.parameters["data"]!!
            val data = Json.decodeFromString<LoginData>(jsonString)
            val icon =
                db.from(ue).select().where { (ue.phoneNumber eq data.phoneNumber) and (ue.password eq data.password) }
                    .map { it[ue.icon] }.first()
            call.respond(icon ?: byteArrayOf())
        }

        delete(user) {
            val user = call.receive<LoginData>()

            val name = db.from(ue).select(ue.firstName, ue.lastName).where { ue.phoneNumber eq user.phoneNumber }
                .map { it[ue.firstName]!! to it[ue.lastName] }.first()

            db.delete(ue) {
                (it.phoneNumber eq user.phoneNumber) and (it.password eq user.password)
            }

            val message = "Пользователь ${name.first} ${name.second ?: ""} вышел из чата"

            db.insert(m) {
                set(m.message, message)
                set(m.userId, -1)
                set(m.dateTime, LocalDateTime.now())
            }

            call.respond(true)
        }

        put("$user$info") {
            val userInfo = call.receive<User>()

            db.update(UserEntity) {
                set(ue.firstName, userInfo.dataUser.firstName)
                set(ue.lastName, userInfo.dataUser.lastName)
                set(ue.icon, userInfo.dataUser.icon)
                where { (it.phoneNumber eq userInfo.data.phoneNumber) and (it.password eq userInfo.data.password) }
            }

            call.respond(true)
        }

        get("$user$info") {

            val users = db.from(ue).select().map {
                UserInfo(
                    registrationData = it[ue.registrationDate]!!.toKotlinLocalDateTime(), dataUser = DataUser(
                        firstName = it[ue.firstName]!!, lastName = it[ue.lastName], icon = it[ue.icon]
                    )
                )
            }

            call.respond(users)
        }


        put("$user$password") {

            val password = call.receive<ChangePassword>()

            println(password.toString())

            db.update(ue) {
                set(ue.password, password.newPassword)
                where { (it.phoneNumber eq password.data.phoneNumber) and (it.password eq password.data.password) }
            }

            call.respond(true)
        }
    }

}

private const val chatUsers = "/chatUsers"

private const val message = "/message"

private const val messages = "/messages"

private const val user = "/user"

private const val info = "/info"

private const val icon = "/icon"

private const val password = "/password"
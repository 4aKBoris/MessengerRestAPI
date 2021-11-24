package com.mpei.routing

import com.mpei.db.data.ChatUser
import com.mpei.db.data.Message
import com.mpei.db.data.User
import com.mpei.db.entity.MessageEntity
import com.mpei.db.entity.UserEntity
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import java.time.LocalDateTime

fun Application.chatRouting(db: Database) {

    val ue = UserEntity

    val m = MessageEntity
    
    val listMessages = listOf<Message>()

    routing {

        get(chatUsers) {

            val users =
                db.from(ue).select().map { ChatUser(it[ue.id]!!, it[ue.firstName]!!, it[ue.lastName], it[ue.icon]) }

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

        delete(user) {
            val user = call.receive<User>()

            db.delete(UserEntity) {
                (it.phoneNumber eq user.phoneNumber) and (it.password eq user.password)
            }

            val message = "Пользователь ${user.firstName} ${user.lastName} вышел из чата"

            db.insert(m) {
                set(m.message, message)
                set(m.userId, -1)
                set(m.dateTime, LocalDateTime.now())
            }

            db.delete(ue) {
                it.phoneNumber eq user.phoneNumber
            }
        }

        put("$user$info") {
            val userInfo = call.receive<User>()

            db.update(UserEntity) {
                set(ue.firstName, userInfo.firstName)
                set(ue.lastName, userInfo.lastName)
                set(ue.icon, userInfo.icon)
                where { it.phoneNumber eq userInfo.phoneNumber }
            }
        }

        put("$user$password") {
            val password = call.receive<User>()

            db.update(ue) {
                set(ue.password, password.password)
                where { it.phoneNumber eq password.phoneNumber }
            }
        }
            }

}

private const val DateTime = "dateTime"

private const val chatUsers = "/chatUsers"

private const val message = "/message"

private const val messages = "/messages"

private const val user = "/user"

private const val info = "/info"

private const val password = "/password"
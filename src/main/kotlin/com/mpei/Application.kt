package com.mpei

import com.example.messenger.PostRequest
import com.example.messenger.ui.screens.authentication.Test
import com.mpei.db.DatabaseConnection
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.Identity.decode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.ktorm.dsl.eq
import org.ktorm.entity.count
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

fun main() {
    embeddedServer(Netty, port = 8081, host = "192.168.1.154") {
        /* configureRouting()
         configureSecurity()
         configureHTTP()
         configureAdministration()
         configureSockets()
         configureSerialization()
         configureTemplating()
         configureMonitoring()*/

        install(StatusPages) {
            exception<Throwable> { e ->
                call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }

        install(ContentNegotiation) {
            json()
        }

        install(Authentication) {
            digest("auth-digest") {

                fun getMd5Digest(str: String): ByteArray =
                    MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

                val myRealm = "Access to the '/' path"
                val userTable: Map<String, ByteArray> = mapOf(
                    "jetbrains" to getMd5Digest("jetbrains:$myRealm:foobar"),
                    "admin" to getMd5Digest("admin:$myRealm:password")
                ) // Configure digest authentication

                realm = myRealm

                digestProvider { phone, realm ->
                    println(phone)
                    userTable[phone]
                }
            }
        }

        routing {
            /*authenticate("auth-digest") {
                get("/login") {
                    call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
                }

                post(path = "/post") {
                    call.respondText("Test")
                }

                get("") {
                    call.respond(Response(status = "OK"))
                }
            }*/

            post(path = "/post") {
                println("dwadwa")
                val k = call.receive(PostRequest::class)
                println("dwadwa")
                call.respond(Response(status = "Норм"))
            }

            get(path = "") {
                call.respond(Response(status = "OK"))
            }

            get(path = "/post") {
                call.respond(Response(status = "OK"))
            }

            get("/checkPhoneNumber") {
                //println("dwadwadwa")
                //val k = call.receive(Test::class)
                //println(k.phoneNumber)
                call.respondText("8778")
                /**if (DatabaseConnection.database.sequenceOf(UserEntity)
                .count { UserEntity.phoneNumber eq call.receiveText() } == 0
                ) call.respondText("123") else call.respondText("123")*/
                //call.respondText("123")
            }

            post("/checkPhoneNumber") {
                if (DatabaseConnection.database.sequenceOf(UserEntity)
                        .count { UserEntity.phoneNumber eq call.receiveText() } == 0
                ) call.respond(false) else call.respond(true)
            }

        }


    }.start(wait = true)
}

@Serializable
data class Request(val id: String, val quantity: Int, val isTrue: Boolean)

@Serializable
data class Response(val status: String)

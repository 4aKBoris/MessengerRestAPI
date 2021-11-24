package com.mpei

import com.mpei.db.DatabaseConnection
import com.mpei.db.data.ChatUser
import com.mpei.db.entity.UserEntity
import com.mpei.routing.configureRouting
import io.ktor.http.*
import io.ktor.server.testing.*
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }
    }

    @Test
    fun test() {
        val db = DatabaseConnection.database
        val ue = UserEntity

        val users = db.from(ue).select(ue.id, ue.firstName, ue.lastName, ue.icon)
            .map { ChatUser(it[ue.id]!!, it[ue.firstName]!!, it[ue.lastName], it[ue.icon]) }

        users.forEach {
            println(it.id)
            println(it.firstName)
            println(it.lastName)
        }
    }
}
package com.mpei;

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

public class ApplicationKtTest {

    @Test
    fun testPostPost() {
        withTestApplication({ main() }) {
            handleRequest(HttpMethod.Post, "/post").apply {
                TODO("Please write your test here")
            }
        }
    }
}
package com.mpei

import com.mpei.routing.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {

    embeddedServer(Netty, port = port, host = ipAddress) {

        configureRouting()

    }.start(wait = true)
}

private const val port = 8081

private const val ipAddress = "192.168.1.154"

package com.mpei

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.auth.ldap.*
import io.ktor.locations.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.sessions.*
import io.ktor.features.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.server.engine.*
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import java.time.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.*
import io.ktor.network.tls.*
import io.ktor.utils.io.core.*
import io.ktor.jackson.*
import com.fasterxml.jackson.databind.*
import io.ktor.gson.*
import io.ktor.serialization.*
import io.ktor.velocity.*
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.apache.velocity.runtime.RuntimeConstants

import io.ktor.thymeleaf.Thymeleaf
import io.ktor.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.pebble.*
import io.ktor.html.*
import kotlinx.html.*
import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.mustache.Mustache
import io.ktor.mustache.MustacheContent
import freemarker.cache.*
import io.ktor.freemarker.*
import kotlinx.css.*
import org.slf4j.event.*
import io.micrometer.prometheus.*
import io.ktor.metrics.micrometer.*
import io.ktor.webjars.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.mpei.plugins.*

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
}
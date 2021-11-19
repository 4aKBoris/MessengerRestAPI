package com.mpei.plugins

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
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun Application.configureTemplating() {
    install(Velocity) {
        setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
    }
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    install(Pebble) {
        loader(ClasspathLoader().apply {
            prefix = "templates"
        })
    }

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates/mustache")
    }
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }


    routing {
        get("/index") {
            val sampleUser = User(1, "John")
            call.respond(VelocityContent("templates/index.vl", mapOf("user" to sampleUser)))
        }
        get("/html-thymeleaf") {
            call.respond(ThymeleafContent("index", mapOf("user" to ThymeleafUser(1, "user1"))))
        }
        get("/pebble-index") {
            val sampleUser = User(1, "John")
            call.respond(PebbleContent("pebble-index.html", mapOf("user" to sampleUser)))
        }
        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }
        get("/html-mustache") {
            call.respond(MustacheContent("index.hbs", mapOf("user" to MustacheUser(1, "user1"))))
        }
        get("/html-freemarker") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
        }
        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.darkBlue
                    margin(0.px)
                }
                rule("h1.page-title") {
                    color = Color.white
                }
            }
        }

        get("/html-css-dsl") {
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                }
                body {
                    h1(classes = "page-title") {
                        +"Hello from Ktor!"
                    }
                }
            }
        }
    }
}

data class User(val id: Int, val name: String)
data class ThymeleafUser(val id: Int, val name: String)
data class MustacheUser(val id: Int, val name: String)
data class IndexData(val items: List<Int>)

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}


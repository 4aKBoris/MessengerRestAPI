package com.mpei.install

import com.mpei.db.DatabaseConnection
import com.mpei.db.entity.UserEntity
import io.ktor.application.*
import io.ktor.auth.*
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.mapColumns
import org.ktorm.entity.sequenceOf
import java.security.MessageDigest

fun Application.installAuthentication(db: Database) {

    install(Authentication) {
        digest("auth-digest") {

            realm = RealM

            digestProvider { phone, _ ->
               db.sequenceOf(UserEntity).filter { it.phoneNumber eq phone }.mapColumns { it.password }.first()
            }
        }
    }
}

private const val RealM = "RestAPI"

private fun getDigest(str: String): ByteArray =
    MessageDigest.getInstance("MD5").digest(str.toByteArray(Charsets.UTF_8))
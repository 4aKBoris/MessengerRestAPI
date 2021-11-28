package com.mpei.db.entity

import org.ktorm.schema.*

object UserEntity: Table<Nothing>("users") {
    val id = int("id").primaryKey()
    val phoneNumber = varchar("phone_number")
    val password = bytes("password")
    val firstName = varchar("first_name")
    val lastName = varchar("last_name")
    val icon = blob("icon")
    val registrationDate = datetime("registrationDate")
}

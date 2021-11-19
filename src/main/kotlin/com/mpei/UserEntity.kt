package com.mpei

import org.ktorm.schema.Table
import org.ktorm.schema.blob
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserEntity: Table<Nothing>("users") {
    val id = int("id").primaryKey()
    val phoneNumber = varchar("phone_number")
    val password = varchar("password")
    val firstName = varchar("first_name")
    val lastName = varchar("last_name")
    val icon = blob("icon")
}

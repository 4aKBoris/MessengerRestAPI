package com.mpei.db.entity

import org.ktorm.schema.*

object MessageEntity: Table<Nothing>("messages") {
    val id = int("id").primaryKey()
    val message = text("message")
    val dateTime = datetime("dateTime")
    val userId = int("userId")
}
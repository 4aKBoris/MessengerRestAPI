package com.mpei.db.entity

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.text

object MessageEntity: Table<Nothing>("messages") {
    val id = int("id").primaryKey()
    val message = text("message")
    val dateTime = datetime("dateTime")
    val userId = int("userId")
}
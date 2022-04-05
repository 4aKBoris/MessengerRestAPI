package com.mpei.db.entity

import org.ktorm.schema.Table
import org.ktorm.schema.blob
import org.ktorm.schema.int

object IconEntity: Table<Nothing>("icons") {
    val id = int("id").primaryKey()
    val idUser = int("idUser")
    val icon = blob("icon")
}
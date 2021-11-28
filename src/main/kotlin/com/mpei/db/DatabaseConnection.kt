package com.mpei.db

import org.ktorm.database.Database

object DatabaseConnection {
    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/restapi",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = "127485ldaLDA$$$",
    )
}
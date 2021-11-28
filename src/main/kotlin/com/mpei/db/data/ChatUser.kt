package com.mpei.db.data

import kotlinx.serialization.Serializable

@Serializable
data class ChatUser(val id: Int, val dataUser: DataUser)
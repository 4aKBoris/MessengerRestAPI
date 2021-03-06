package com.mpei.db.data

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, val firstName: String, val lastName: String?)

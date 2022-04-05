package com.mpei.db.data

import kotlinx.serialization.Serializable

@Serializable
data class Name(val firstName: String, val lastName: String?)

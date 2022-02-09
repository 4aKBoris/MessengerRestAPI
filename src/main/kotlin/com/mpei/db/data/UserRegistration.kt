package com.mpei.db.data

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistration(val phoneNumber: String, val password: String, val firstName: String, val lastName: String?, val icon: String?)
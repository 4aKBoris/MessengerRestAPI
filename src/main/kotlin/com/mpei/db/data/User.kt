@file:Suppress("DuplicatedCode")

package com.mpei.db.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val phoneNumber: String,
    val password: ByteArray,
    val firstName: String,
    val lastName: String? = null,
    val icon: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (phoneNumber != other.phoneNumber) return false
        if (!password.contentEquals(other.password)) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (icon != null) {
            if (other.icon == null) return false
            if (!icon.contentEquals(other.icon)) return false
        } else if (other.icon != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = phoneNumber.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + (lastName?.hashCode() ?: 0)
        result = 31 * result + (icon?.contentHashCode() ?: 0)
        return result
    }
}

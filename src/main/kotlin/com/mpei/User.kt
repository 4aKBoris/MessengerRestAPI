package com.mpei

import io.ktor.auth.*

public data class User(val name: String) : Principal

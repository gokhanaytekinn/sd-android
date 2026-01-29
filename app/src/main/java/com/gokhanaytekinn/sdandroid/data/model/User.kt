package com.gokhanaytekinn.sdandroid.data.model

data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val token: String? = null
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val user: User,
    val token: String
)

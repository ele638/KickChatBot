package ru.ele638.mychatbot.data

data class User(
    val userId: Int,
    val username: String,
    val clientId: String? = null,
    val clientSecret: String? = null,
    val permissionCode: String? = null
)

package ru.ele638.mychatbot.data

data class User(
    val userId: Int,
    val username: String,
    val kickClientId: String? = null,
    val kickClientSecret: String? = null,
    val kickPermissionCode: String? = null,
    val kickBroadcasterId: Int? = null
)

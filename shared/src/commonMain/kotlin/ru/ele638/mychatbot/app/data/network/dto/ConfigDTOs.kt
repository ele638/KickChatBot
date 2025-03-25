package ru.ele638.mychatbot.app.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfigUpdateRequest(
    val clientId: String,
    val clientSecret: String,
)

@Serializable
data class ConfigPermissionUpdateRequest(
    val permissionCode: String
)
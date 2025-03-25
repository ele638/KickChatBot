package ru.ele638.mychatbot.app.data.network.dto

import kotlinx.serialization.Serializable
import ru.ele638.mychatbot.app.data.kick.network.KickScopes

@Serializable
data class KickStartAuthRequest(
    val appCallbackUri: String,
    val scopes: List<KickScopes>
)
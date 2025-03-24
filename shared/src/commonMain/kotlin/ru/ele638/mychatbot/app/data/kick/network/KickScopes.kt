package ru.ele638.mychatbot.app.data.kick.network

import kotlinx.serialization.Serializable

@Serializable
enum class KickScopes(val stringKey: String) {
    USER_READ("user:read"),
    CHANNEL_READ("channel:read"),
    CHANNEL_WRITE("channel:write"),
    CHAT_WRITE("chat:write"),
    STREAMKEY_READ("streamkey:read"),
    EVENTS_SUBSCRIBE("events:subscribe")
}
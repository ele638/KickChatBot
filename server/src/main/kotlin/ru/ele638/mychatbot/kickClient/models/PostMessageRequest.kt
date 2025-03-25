package ru.ele638.mychatbot.kickClient.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PostMessageRequest(
    @SerializedName("content")
    val content: String,
    @SerializedName("type")
    val type: String
)

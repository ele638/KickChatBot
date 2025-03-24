package ru.ele638.mychatbot.data

import ru.ele638.mychatbot.utils.CodeChallenge

data class ConnectKickSession(
    val userName: String,
    val codeChallenge: CodeChallenge,
    val appCallbackUri: String
)

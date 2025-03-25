package ru.ele638.mychatbot.data

import ru.ele638.mychatbot.app.data.kick.network.KickScopes
import ru.ele638.mychatbot.utils.CodeChallenge
import kotlin.random.Random

data class ConnectKickSession(
    val id: Int,
    val clientId: String,
    val clientSecret: String,
    val userName: String,
    val codeChallenge: CodeChallenge,
    val scopes: List<KickScopes>,
    val appCallbackUri: String,
    val status: Status
) {
    enum class Status {
        WAITING_PERMISSION,
        WAITING_TOKEN,
        COMPLETED,
        ABORTED
    }

    fun buildUrl(baseKickAuthUr: String): String = baseKickAuthUr +
            "/authorize?" +
            "response_type=code&" +
            "client_id=${clientId}&" +
            "redirect_uri=${appCallbackUri}&" +
            "scope=${scopes.joinToString("+") { it.stringKey }}&" +
            "code_challenge=${codeChallenge.codeChallenge}&" +
            "code_challenge_method=S256&" +
            "state=${Random.nextInt()}"
}

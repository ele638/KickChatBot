package ru.ele638.mychatbot.app.data.kick.network
//
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.forms.FormDataContent
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.Parameters
//import io.ktor.http.contentType
//import io.ktor.serialization.kotlinx.json.json
//import ru.ele638.mychatbot.app.data.network.dto.TokenResponse
//import ru.ele638.mychatbot.app.data.storage.PrefsProvider
//import ru.ele638.mychatbot.utils.CodeChallengeGenerator
//import kotlin.random.Random
//
//class KickClientApi(
//    private val codeChallengeGenerator: CodeChallengeGenerator,
//    private val prefsProvider: PrefsProvider
//) {
//    private val redirectUrl = "http://localhost:8080/"
//
//    private val tokenRequestClient = HttpClient {
//        install(ContentNegotiation) { json() }
//    }
//
//    fun getKickAuthUrl(
//        clientId: String,
//        scopes: List<KickScopes>,
//    ): String {
//        val codeChallenge = codeChallengeGenerator.generatePair()
//        saveCodeChallenge(codeChallenge)
//        return BASE_KICK_AUTH_URL +
//                "/authorize?" +
//                "response_type=code&" +
//                "client_id=$clientId&" +
//                "redirect_uri=$redirectUrl&" +
//                "scope=${scopes.joinToString("+") { it.stringKey }}&" +
//                "code_challenge=${codeChallenge.codeChallenge}&" +
//                "code_challenge_method=S256&" +
//                "state=${Random.nextInt()}"
//    }
//
//    suspend fun requestToken(
//        callbackCode: String,
//        clientId: String,
//        clientSecret: String
//    ): TokenResponse = tokenRequestClient.post("$BASE_KICK_AUTH_URL/token") {
//        val codeVerifier = loadCodeChallenge()?.codeVerifier ?: throw IllegalStateException("No saved code challenge!")
//        contentType(ContentType.Application.FormUrlEncoded)
//        setBody(
//            FormDataContent(Parameters.build {
//                append("code", callbackCode)
//                append("client_id", clientId)
//                append("client_secret", clientSecret)
//                append("redirect_uri", redirectUrl)
//                append("grant_type", "authorization_code")
//                append("code_verifier", codeVerifier)
//            })
//        )
//    }.body()
//
//    suspend fun refreshToken(
//        refreshToken: String,
//        clientId: String,
//        clientSecret: String
//    ): TokenResponse = tokenRequestClient.post("$BASE_KICK_AUTH_URL/token") {
//        contentType(ContentType.Application.FormUrlEncoded)
//        setBody(
//            FormDataContent(Parameters.build {
//                append("refresh_token", refreshToken)
//                append("client_id", clientId)
//                append("client_secret", clientSecret)
//                append("grant_type", "refresh_token")
//            })
//        )
//    }.body()
//
//    private fun saveCodeChallenge(codeChallenge: CodeChallenge) {
//        val codeChallengeEscaped = codeChallenge.codeChallenge.replace("=", "EQL")
//        val codeVerifierEscaped = codeChallenge.codeVerifier.replace("=", "EQL")
//        prefsProvider.put(CODE_CHALLENGE_KEY to codeChallengeEscaped, CODE_VERIFIER_KEY to codeVerifierEscaped)
//    }
//
//    private fun loadCodeChallenge(): CodeChallenge? {
//        val codeChallenge = prefsProvider.get(CODE_CHALLENGE_KEY)?.replace("EQL", "=")
//        val codeVerifier = prefsProvider.get(CODE_VERIFIER_KEY)?.replace("EQL", "=")
//        return if (codeChallenge != null && codeVerifier != null) {
//            CodeChallenge(codeChallenge, codeVerifier)
//        } else {
//            null
//        }
//    }
//
//    companion object {
//        const val BASE_KICK_AUTH_URL = "https://id.kick.com/oauth"
//        const val CODE_CHALLENGE_KEY = "code_challenge"
//        const val CODE_VERIFIER_KEY = "code_verifier"
//    }
//}
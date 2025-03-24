package ru.ele638.mychatbot.utils

import org.kotlincrypto.hash.sha2.SHA256
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

@OptIn(ExperimentalEncodingApi::class)
class CodeChallengeGenerator {
    fun generatePair(): CodeChallenge {
        val codeVerifier = Base64.UrlSafe.encode(Random.nextBytes(10))
        val codeChallenge = Base64.UrlSafe.encode(SHA256().digest(codeVerifier.encodeToByteArray()))
        return CodeChallenge(
            codeChallenge = codeChallenge,
            codeVerifier = codeVerifier
        )
    }
}

data class CodeChallenge(
    val codeChallenge: String,
    val codeVerifier: String
)
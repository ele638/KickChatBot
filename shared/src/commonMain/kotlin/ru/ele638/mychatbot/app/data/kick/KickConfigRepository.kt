package ru.ele638.mychatbot.app.data.kick

import ru.ele638.mychatbot.app.data.storage.PrefsProvider

interface KickConfigRepository {
    suspend fun saveConfig(clientId: String, clientSecret: String)
    suspend fun getConfig(): KickConfig?
    suspend fun setOAuthCode(code: String)
}

class KickConfigRepositoryImpl(
    private val prefsProvider: PrefsProvider
): KickConfigRepository {
    // TODO() refactor to normal storage
    private var config: KickConfig? = null

    override suspend fun saveConfig(clientId: String, clientSecret: String) {
        prefsProvider.put(CLIENT_ID_KEY to clientId, CLIENT_SECRET_KEY to clientSecret)
    }

    override suspend fun getConfig(): KickConfig? {
        val clientId = prefsProvider.get(CLIENT_ID_KEY)
        val clientSecret = prefsProvider.get(CLIENT_SECRET_KEY)
        if (clientId != null && clientSecret != null) {
            config = KickConfig(clientId, clientSecret)
        }
        return config
    }

    override suspend fun setOAuthCode(code: String) {
        config = config?.copy(oAuthCode = code)
    }

    companion object {
        const val CLIENT_ID_KEY = "clientID"
        const val CLIENT_SECRET_KEY = "clientSecret"
    }
}

data class KickConfig(
    val clientId: String,
    val clientSecret: String,
    val oAuthCode: String? = null
)
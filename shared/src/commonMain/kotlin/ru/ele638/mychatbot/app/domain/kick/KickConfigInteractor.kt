package ru.ele638.mychatbot.app.domain.kick

import kotlinx.coroutines.withContext
import ru.ele638.mychatbot.app.data.kick.KickConfig
import ru.ele638.mychatbot.app.data.kick.KickConfigRepository
import ru.ele638.mychatbot.app.util.DispatchersProvider

interface KickConfigInteractor {
    suspend fun getSavedConfig(): KickConfig?
    suspend fun requestOauthCode(clientId: String, clientSecret: String)
    suspend fun requestToken(
        callbackCode: String,
        clientId: String,
        clientSecret: String,
    ): KickToken
    suspend fun refreshToken(
        refreshToken: String,
        clientId: String,
        clientSecret: String
    ): KickToken
}

class KickConfigInteractorImpl(
    private val repository: KickConfigRepository,
    private val dispatchersProvider: DispatchersProvider,
//    private val kickClientApi: KickClientApi,
) : KickConfigInteractor {

    override suspend fun getSavedConfig(): KickConfig? {
        return repository.getConfig()
    }

    override suspend fun requestOauthCode(clientId: String, clientSecret: String) {
        withContext(dispatchersProvider.IO) {
            repository.saveConfig(clientId, clientSecret)
        }

        withContext(dispatchersProvider.Main) {
//            urlOpener.openUrl(
//                kickClientApi.getKickAuthUrl(
//                    clientId = clientId,
//                    scopes = KickScopes.entries.toList(),
//                )
//            )
        }
    }

    override suspend fun requestToken(
        callbackCode: String,
        clientId: String,
        clientSecret: String,
    ): KickToken = withContext(dispatchersProvider.IO) {
//        val response = kickClientApi.requestToken(callbackCode, clientId, clientSecret)
//        KickToken(
//            token = response.accessToken,
//            refreshToken = response.refreshToken,
//            expiresIn = response.expiresIn
//        )
        TODO()
    }

    override suspend fun refreshToken(
        refreshToken: String,
        clientId: String,
        clientSecret: String
    ): KickToken = withContext(dispatchersProvider.IO) {
//        val response = kickClientApi.refreshToken(refreshToken, clientId, clientSecret)
//        KickToken(
//            token = response.accessToken,
//            refreshToken = response.refreshToken,
//            expiresIn = response.expiresIn
//        )
        TODO()
    }

}

data class KickToken(
    val token: String,
    val refreshToken: String,
    val expiresIn: String
)
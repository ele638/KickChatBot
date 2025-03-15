package ru.ele638.mychatbot.app.data

import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import ru.ele638.mychatbot.app.util.ClockProvider
import ru.ele638.mychatbot.app.util.DispatchersProvider

interface SessionRepository {
    suspend fun getSavedSession(): Session?
    suspend fun saveSession(userName: String)
    suspend fun clearSession()
}

class SessionRepositoryImpl(
    private val prefsProvider: PrefsProvider,
    private val clockProvider: ClockProvider,
    private val dispatchersProvider: DispatchersProvider
) : SessionRepository {
    override suspend fun getSavedSession(): Session? {
        return withContext(dispatchersProvider.IO) {
            val userName = prefsProvider.get(USERNAME_KEY)
                .also { Napier.v("Saved session user: $it") }
                ?: return@withContext null
            val expiresAt = prefsProvider.get(EXPIRES_AT_KEY)
                ?.let { clockProvider.parse(it) }
                ?.also { Napier.v("Saved session expiration: $it") }
                ?: return@withContext null
            if (expiresAt < clockProvider.now()) return@withContext null

            Napier.v("Valid session found")
            Session(userName, expiresAt)
        }
    }

    override suspend fun saveSession(userName: String) {
        withContext(dispatchersProvider.IO) {
            val expiresAt = clockProvider.now()
                .plus(SESSION_TTL_HOURS, DateTimeUnit.HOUR)
                .toString()
            Napier.i("Saving session: $USERNAME_KEY = $userName, $EXPIRES_AT_KEY = $expiresAt")
            prefsProvider.put(USERNAME_KEY to userName, EXPIRES_AT_KEY to expiresAt)
        }
    }

    override suspend fun clearSession() {
        prefsProvider.clear()
    }

    companion object {
        private const val SESSION_TTL_HOURS = 2L
        private const val USERNAME_KEY = "userName"
        private const val EXPIRES_AT_KEY = "expires"
    }
}

data class Session(
    val userName: String,
    val expiresAt: Instant
)
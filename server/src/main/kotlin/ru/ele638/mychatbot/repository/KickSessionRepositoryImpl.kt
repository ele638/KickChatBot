package ru.ele638.mychatbot.repository

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.ele638.mychatbot.app.data.kick.network.KickScopes
import ru.ele638.mychatbot.data.ConnectKickSession
import ru.ele638.mychatbot.database.tables.KickAuthSessions
import ru.ele638.mychatbot.database.tables.Users
import ru.ele638.mychatbot.utils.CodeChallenge
import ru.ele638.mychatbot.utils.CodeChallengeGenerator

interface KickSessionRepository {
    fun createSession(
        username: String,
        requestAppCallbackUri: String,
        requestScopes: List<KickScopes>
    ): ConnectKickSession?

    fun getPendingSession(username: String): ConnectKickSession?
    fun updatePendingSession(username: String, status: ConnectKickSession.Status): Boolean
}

class KickSessionRepositoryImpl(
    private val codeChallengeGenerator: CodeChallengeGenerator,
    private val userRepository: UserRepository
) : KickSessionRepository {
    override fun createSession(
        username: String,
        requestAppCallbackUri: String,
        requestScopes: List<KickScopes>
    ): ConnectKickSession? {
        return transaction {
            val user = userRepository.getUser(username) ?: return@transaction null
            val challenge = codeChallengeGenerator.generatePair()
            KickAuthSessions.update(
                {
                    KickAuthSessions.userId.eq(user.userId) and
                            KickAuthSessions.status.neq(ConnectKickSession.Status.COMPLETED.name)
                }
            ) {
                it[status] = ConnectKickSession.Status.ABORTED.name
            }
            if (user.kickClientId == null || user.kickClientSecret == null) {
                return@transaction null
            }
            val insertedId = KickAuthSessions.insertAndGetId {
                it[userId] = user.userId
                it[codeChallenge] = challenge.codeChallenge
                it[codeVerifier] = challenge.codeVerifier
                it[appCallbackUri] = requestAppCallbackUri
                it[clientId] = user.kickClientId
                it[clientSecret] = user.kickClientSecret
                it[scopes] = requestScopes.joinToString(";")
                it[status] = ConnectKickSession.Status.WAITING_PERMISSION.name
            }

            ConnectKickSession(
                id = insertedId.value,
                userName = username,
                codeChallenge = CodeChallenge(
                    codeChallenge = challenge.codeChallenge,
                    codeVerifier = challenge.codeVerifier
                ),
                appCallbackUri = requestAppCallbackUri,
                clientId = user.kickClientId,
                clientSecret = user.kickClientSecret,
                scopes = requestScopes,
                status = ConnectKickSession.Status.WAITING_PERMISSION
            )
        }
    }

    override fun getPendingSession(
        username: String
    ): ConnectKickSession? {
        return transaction {
            val user = Users.select { Users.username eq username }.singleOrNull()
            if (user == null) return@transaction null

            val session = KickAuthSessions.select {
                KickAuthSessions.userId.eq(user[Users.id].value) and
                        KickAuthSessions.status.neq(ConnectKickSession.Status.ABORTED.name) and
                        KickAuthSessions.status.neq(ConnectKickSession.Status.COMPLETED.name)
            }.singleOrNull()
            if (session == null) return@transaction null

            ConnectKickSession(
                id = session[KickAuthSessions.id].value,
                userName = username,
                codeChallenge = CodeChallenge(
                    codeChallenge = session[KickAuthSessions.codeChallenge],
                    codeVerifier = session[KickAuthSessions.codeVerifier]
                ),
                appCallbackUri = session[KickAuthSessions.appCallbackUri],
                clientId = session[KickAuthSessions.clientId],
                clientSecret = session[KickAuthSessions.clientSecret],
                scopes = session[KickAuthSessions.scopes].split(";").map { KickScopes.valueOf(it) },
                status = ConnectKickSession.Status.valueOf(session[KickAuthSessions.status])
            )
        }
    }

    override fun updatePendingSession(
        username: String,
        status: ConnectKickSession.Status
    ): Boolean {
        return transaction {
            val user = userRepository.getUser(username) ?: return@transaction false
            KickAuthSessions.update(
                {
                    KickAuthSessions.userId.eq(user.userId) and
                            KickAuthSessions.status.neq(ConnectKickSession.Status.ABORTED.name) and
                            KickAuthSessions.status.neq(ConnectKickSession.Status.COMPLETED.name)
                }
            ) {
                it[KickAuthSessions.status] = status.name
            } > 0
        }
    }
}
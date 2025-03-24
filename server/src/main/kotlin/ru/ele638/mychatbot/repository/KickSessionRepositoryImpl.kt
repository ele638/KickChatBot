package ru.ele638.mychatbot.repository

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.ele638.mychatbot.data.ConnectKickSession
import ru.ele638.mychatbot.database.tables.KickAuthSessions
import ru.ele638.mychatbot.database.tables.Users
import ru.ele638.mychatbot.utils.CodeChallenge

interface KickSessionRepository {
    fun createSession(session: ConnectKickSession): Boolean
    fun getSession(username: String): ConnectKickSession?
}

class KickSessionRepositoryImpl : KickSessionRepository {
    override fun createSession(
        session: ConnectKickSession
    ): Boolean {
        return transaction {
            val user = Users.select { Users.username eq session.userName }.singleOrNull()
            if (user == null) return@transaction false
            KickAuthSessions.upsert(
                KickAuthSessions.userId,
                onUpdate = mutableListOf(
                    KickAuthSessions.codeChallenge to stringLiteral(session.codeChallenge.codeChallenge),
                    KickAuthSessions.codeVerifier to stringLiteral(session.codeChallenge.codeVerifier),
                    KickAuthSessions.appCallbackUri to stringLiteral(session.appCallbackUri),
                )
            ) {
                it[codeChallenge] = codeChallenge
                it[codeVerifier] = codeVerifier
                it[appCallbackUri] = appCallbackUri
            }
            true
        }
    }

    override fun getSession(
        username: String
    ) : ConnectKickSession? {
        return transaction {
            val user = Users.select { Users.username eq username }.singleOrNull()
            if (user == null) return@transaction null

            val session = KickAuthSessions.select { KickAuthSessions.userId eq user[Users.id].value }.singleOrNull()
            if (session == null) return@transaction null

            ConnectKickSession(
                userName = username,
                codeChallenge = CodeChallenge(
                    codeChallenge = session[KickAuthSessions.codeChallenge],
                    codeVerifier = session[KickAuthSessions.codeVerifier]
                ),
                appCallbackUri = session[KickAuthSessions.appCallbackUri]
            )
        }
    }
}
package ru.ele638.mychatbot.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ele638.mychatbot.data.KickToken
import ru.ele638.mychatbot.database.tables.KickTokens
import java.time.LocalDateTime

interface KickTokenRepository {
    fun saveToken(
        username: String,
        newAuthToken: String,
        newRefreshToken: String,
        expiresIn: String
    ): KickToken?

    fun getSavedToken(
        username: String
    ): KickToken?
}

class KickTokenRepositoryImpl(
    private val userRepository: UserRepository
) : KickTokenRepository {
    override fun saveToken(
        username: String,
        newAuthToken: String,
        newRefreshToken: String,
        expiresIn: String
    ): KickToken? {
        return transaction {
            val user = userRepository.getUser(username)
                ?: return@transaction null

            KickTokens.deleteWhere {
                userId eq user.userId
            }
            val expirationTime = LocalDateTime.now().plusSeconds(expiresIn.toLong())
            val tokenId = KickTokens.insertAndGetId {
                it[userId] = user.userId
                it[accessToken] = newAuthToken
                it[refreshToken] = newRefreshToken
                it[expiresAt] = expirationTime
            }

            KickToken(
                tokenId = tokenId.value,
                accessToken = newAuthToken,
                refreshToken = newRefreshToken,
                expires = expirationTime
            )
        }
    }

    override fun getSavedToken(username: String): KickToken? {
        return transaction {
            val user = userRepository.getUser(username)
                ?: return@transaction null

            val storedToken = KickTokens.select {
                KickTokens.userId eq user.userId
            }.singleOrNull()

            storedToken?.let {
                KickToken(
                    tokenId = it[KickTokens.id].value,
                    accessToken = it[KickTokens.accessToken],
                    refreshToken = it[KickTokens.refreshToken],
                    expires = it[KickTokens.expiresAt]
                )
            }
        }
    }
}
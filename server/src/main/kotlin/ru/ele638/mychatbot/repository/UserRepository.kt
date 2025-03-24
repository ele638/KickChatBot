package ru.ele638.mychatbot.repository

import JwtConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Op.Companion.nullOp
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import ru.ele638.mychatbot.data.User
import ru.ele638.mychatbot.database.DatabaseFactory
import ru.ele638.mychatbot.database.tables.RefreshTokens
import ru.ele638.mychatbot.database.tables.UserConfigs
import ru.ele638.mychatbot.database.tables.Users
import ru.ele638.mychatbot.utils.PasswordUtil
import java.time.LocalDateTime

interface UserRepository {
    fun createUser(username: String, password: String): Boolean
    fun getUser(username: String): User?
    fun isUserPasswordCorrect(username: String, password: String): Boolean
    fun verifyUserRefreshToken(username: String, userRefreshToken: String): Boolean
    fun updateUserRefreshToken(username: String, newRefreshJWT: String): Boolean
    fun verifyRefreshTokenExists(userRefreshToken: String): Boolean
}

class UserRepositoryImpl(
    private val passwordUtil: PasswordUtil,
): UserRepository {
    // User
    override fun createUser(username: String, password: String): Boolean {
        return transaction {
            val existingUser = Users.select { Users.username eq username }.singleOrNull()
            if (existingUser != null) {
                return@transaction false
            }
            Users.insert {
                it[this.username] = username
                it[this.password] = passwordUtil.hashPassword(password)
            }.insertedCount > 0
        }
    }

    override fun getUser(username: String): User? {
        return transaction {
            val user = Users.select { Users.username eq username }.singleOrNull()
            if (user == null) return@transaction null
            val config =
                UserConfigs.select { UserConfigs.userId eq user[Users.id].value }.singleOrNull()
            if (config == null) return@transaction User(
                username = username,
                userId = user[Users.id].value
            )
            User(
                username = username,
                userId = user[Users.id].value,
                clientId = config[UserConfigs.kickClientId],
                clientSecret = config[UserConfigs.kickClientSecret],
                permissionCode = config[UserConfigs.kickPermissionCode]
            )
        }
    }

    override fun isUserPasswordCorrect(username: String, password: String): Boolean {
        return transaction {
            val user = Users.select { Users.username eq username }.singleOrNull()
            !(user == null || !BCrypt.checkpw(password, user[Users.password]))
        }
    }

    // Token
    override fun verifyUserRefreshToken(username: String, userRefreshToken: String): Boolean {
        return transaction {
            val user = Users.select { Users.username eq username }.singleOrNull()
            if (user == null) {
                return@transaction false
            }
            val existingToken =
                RefreshTokens.select { RefreshTokens.userId eq user[Users.id].value }.singleOrNull()
            existingToken != null && userRefreshToken == existingToken[RefreshTokens.refreshToken]
        }
    }

    override fun verifyRefreshTokenExists(userRefreshToken: String): Boolean {
        return transaction {
            val existingToken =
                RefreshTokens.select { RefreshTokens.refreshToken eq userRefreshToken }
                    .singleOrNull()
            existingToken != null
        }
    }

    override fun updateUserRefreshToken(username: String, newRefreshJWT: String): Boolean {
        return transaction {
            val user = Users
                .select { Users.username eq username }
                .singleOrNull()

            if (user == null) {
                return@transaction false
            }
            RefreshTokens.insert {
                it[userId] = user[Users.id].value
                it[refreshToken] = newRefreshJWT
                it[expiresAt] = LocalDateTime.now().plusDays(JwtConfig.REFRESH_TOKEN_TTL_DAYS)
                it[createdAt] = LocalDateTime.now()
            }
            true
        }
    }
}
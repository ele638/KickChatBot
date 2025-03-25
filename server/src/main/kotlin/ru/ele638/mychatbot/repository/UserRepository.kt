package ru.ele638.mychatbot.repository

import JwtConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import ru.ele638.mychatbot.data.User
import ru.ele638.mychatbot.database.tables.RefreshTokens
import ru.ele638.mychatbot.database.tables.Users
import ru.ele638.mychatbot.utils.PasswordUtil
import java.time.LocalDateTime

interface UserRepository {
    fun createUser(username: String, password: String): Result<User>
    fun getUser(username: String): User?
    fun updateUserKickConfig(
        username: String,
        kickClientId: String,
        kickClientSecret: String
    ): Boolean
    fun updateUserKickPermissionCode(
        username: String,
        kickPermissionCode: String
    ): Boolean

    fun isUserPasswordCorrect(username: String, password: String): Boolean
    fun verifyUserRefreshToken(username: String, userRefreshToken: String): Boolean
    fun updateUserRefreshToken(username: String, newRefreshJWT: String): Boolean
    fun verifyRefreshTokenExists(userRefreshToken: String): Boolean
}

class UserRepositoryImpl(
    private val passwordUtil: PasswordUtil,
) : UserRepository {
    // User
    override fun createUser(username: String, password: String): Result<User> {
        return transaction {
            val existingUser = Users.select { Users.username eq username }.singleOrNull()
            existingUser?.let {
                return@transaction Result.failure(IllegalStateException("User exist"))
            }
            Users.insertAndGetId {
                it[this.username] = username
                it[this.password] = passwordUtil.hashPassword(password)
            }.run {
                Result.success(
                    User(
                        userId = this.value,
                        username = username,
                        kickClientId = null,
                        kickClientSecret = null,
                    )
                )
            }
        }
    }

    override fun getUser(username: String): User? {
        return transaction {
            val user = Users.select { Users.username eq username }.singleOrNull()
            if (user == null) return@transaction null
            User(
                username = username,
                userId = user[Users.id].value,
                kickClientId = user[Users.kickClientId],
                kickClientSecret = user[Users.kickClientSecret],
                kickPermissionCode = user[Users.kickPermissionCode]
            )
        }
    }

    override fun updateUserKickConfig(username: String, kickClientId: String, kickClientSecret: String): Boolean {
        return transaction {
            Users.update(
                { Users.username eq username}
            ) {
                it[Users.kickClientId] = kickClientId
                it[Users.kickClientSecret] = kickClientSecret
            } > 0
        }
    }

    override fun updateUserKickPermissionCode(username: String, kickPermissionCode: String): Boolean {
        return transaction {
            Users.update(
                { Users.username eq username}
            ) {
                it[Users.kickPermissionCode] = kickPermissionCode
            } > 0
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
                RefreshTokens.select {
                    RefreshTokens.userId eq user[Users.id].value
                }.singleOrNull()
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
            RefreshTokens.deleteWhere { userId eq user[Users.id].value }
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
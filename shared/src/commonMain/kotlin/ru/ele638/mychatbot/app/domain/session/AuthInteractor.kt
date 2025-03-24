package ru.ele638.mychatbot.app.domain.session

import ru.ele638.mychatbot.app.data.network.AuthApi
import ru.ele638.mychatbot.app.data.network.dto.TokenVerificationResult

interface AuthInteractor {
    suspend fun isLoginRequired(): Boolean
}

class AuthInteractorImpl(
    private val authApi: AuthApi
) : AuthInteractor {
    override suspend fun isLoginRequired(): Boolean {
        val tokenVerificationResult = authApi.verifyTokens()
        return if (tokenVerificationResult.isSuccess) {
            val result = tokenVerificationResult.getOrThrow()
            when (result) {
                TokenVerificationResult.TOKEN_VALID -> false
                TokenVerificationResult.NEED_REFRESH -> authApi.refreshToken().isSuccess
                TokenVerificationResult.TOKEN_INVALID -> true
            }
        } else {
             true
        }
    }
}
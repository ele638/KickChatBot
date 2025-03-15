package ru.ele638.mychatbot.domain.session

import ru.ele638.mychatbot.data.Session
import ru.ele638.mychatbot.data.SessionRepository

interface SessionInteractor {
    suspend fun getSession(): Session?
    suspend fun saveSession(userName: String)
}

class SessionInteractorImpl(
    private val sessionRepository: SessionRepository
) : SessionInteractor {
    override suspend fun getSession(): Session? {
        val savedSession = sessionRepository.getSavedSession()
        if (savedSession == null) {
            sessionRepository.clearSession()
        }
        return savedSession
    }

    override suspend fun saveSession(userName: String) {
        sessionRepository.saveSession(userName)
    }
}
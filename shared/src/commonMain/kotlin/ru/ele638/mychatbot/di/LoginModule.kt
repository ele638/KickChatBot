package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.app.data.SessionRepository
import ru.ele638.mychatbot.app.data.SessionRepositoryImpl
import ru.ele638.mychatbot.app.domain.session.SessionInteractor
import ru.ele638.mychatbot.app.domain.session.SessionInteractorImpl

val loginModule = module {
    includes(sharedModule)
    single<SessionRepository> { SessionRepositoryImpl(get(), get(), get()) }
    single<SessionInteractor> { SessionInteractorImpl(get()) }
}
package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.data.SessionRepository
import ru.ele638.mychatbot.data.SessionRepositoryImpl
import ru.ele638.mychatbot.domain.session.SessionInteractor
import ru.ele638.mychatbot.domain.session.SessionInteractorImpl

val loginModule = module {
    includes(sharedModule)
    single<SessionRepository> { SessionRepositoryImpl(get(), get(), get()) }
    single<SessionInteractor> { SessionInteractorImpl(get()) }
}
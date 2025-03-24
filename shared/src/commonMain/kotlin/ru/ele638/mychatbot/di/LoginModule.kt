package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.app.data.network.AuthApi
import ru.ele638.mychatbot.app.data.network.AuthApiImpl
import ru.ele638.mychatbot.app.domain.session.AuthInteractor
import ru.ele638.mychatbot.app.domain.session.AuthInteractorImpl

val loginModule = module {
    single<AuthApi> { AuthApiImpl(get()) }
    single<AuthInteractor> { AuthInteractorImpl(get()) }
}
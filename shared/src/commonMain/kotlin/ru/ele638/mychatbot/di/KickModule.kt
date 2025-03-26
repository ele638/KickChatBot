package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.app.data.kick.KickConfigRepository
import ru.ele638.mychatbot.app.data.kick.KickConfigRepositoryImpl
import ru.ele638.mychatbot.app.domain.kick.KickConfigInteractor
import ru.ele638.mychatbot.app.domain.kick.KickConfigInteractorImpl

val kickModule = module {
    single<KickConfigRepository> { KickConfigRepositoryImpl(get()) }
    single<KickConfigInteractor> { KickConfigInteractorImpl(get(), get()) }
}
package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.kickClient.KickClient
import ru.ele638.mychatbot.kickClient.KickClientImpl
import ru.ele638.mychatbot.kickClient.KickTokenManager
import ru.ele638.mychatbot.kickClient.KickTokenManagerImpl

val kickModule = module {
    single<KickTokenManager> { KickTokenManagerImpl(get(), get(), get()) }
    single<KickClient> { KickClientImpl(get()) }
}
package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.Platform
import ru.ele638.mychatbot.domain.storage.PrefsProvider
import ru.ele638.mychatbot.domain.storage.getPrefsProvider
import ru.ele638.mychatbot.getPlatform
import ru.ele638.mychatbot.util.ClockProvider
import ru.ele638.mychatbot.util.ClockProviderImpl
import ru.ele638.mychatbot.util.DispatchersProvider
import ru.ele638.mychatbot.util.getDispatchersProvider

val sharedModule = module {
    single<Platform> { getPlatform() }
    single<PrefsProvider> { getPrefsProvider() }
    single<ClockProvider> { ClockProviderImpl() }
    single<DispatchersProvider> { getDispatchersProvider() }
}
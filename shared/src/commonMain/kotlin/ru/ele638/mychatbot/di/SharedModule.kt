package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import ru.ele638.mychatbot.app.util.ClockProvider
import ru.ele638.mychatbot.app.util.ClockProviderImpl
import ru.ele638.mychatbot.app.util.DispatchersProvider
import ru.ele638.mychatbot.app.util.UrlOpener
import ru.ele638.mychatbot.platformSpecific.getDispatchersProvider
import ru.ele638.mychatbot.platformSpecific.getPlatform
import ru.ele638.mychatbot.platformSpecific.getPrefsProvider
import ru.ele638.mychatbot.platformSpecific.getUrlOpener


val sharedModule = module {
    single<Platform> { getPlatform() }
    single<PrefsProvider> { getPrefsProvider() }
    single<ClockProvider> { ClockProviderImpl() }
    single<DispatchersProvider> { getDispatchersProvider() }
    single<UrlOpener> { getUrlOpener() }

}
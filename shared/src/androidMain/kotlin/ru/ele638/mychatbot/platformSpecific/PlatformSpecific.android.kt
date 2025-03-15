package ru.ele638.mychatbot.platformSpecific

import ru.ele638.mychatbot.app.data.AndroidPlatform
import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import ru.ele638.mychatbot.app.util.AndroidDispatchersProvider
import ru.ele638.mychatbot.app.util.DispatchersProvider

actual fun getPrefsProvider(): PrefsProvider {
    TODO("Not yet implemented")
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun getDispatchersProvider(): DispatchersProvider = AndroidDispatchersProvider()
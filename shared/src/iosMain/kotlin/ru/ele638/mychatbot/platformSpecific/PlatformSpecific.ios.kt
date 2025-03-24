package ru.ele638.mychatbot.platformSpecific

import ru.ele638.mychatbot.app.data.IOSPlatform
import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import ru.ele638.mychatbot.app.util.DispatchersProvider
import ru.ele638.mychatbot.app.util.IOSDispatchersProvider
import ru.ele638.mychatbot.app.util.UrlOpener

actual fun getPrefsProvider(): PrefsProvider {
    TODO("Not yet implemented")
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun getDispatchersProvider(): DispatchersProvider = IOSDispatchersProvider()
actual fun getUrlOpener(): UrlOpener {
    TODO("Not yet implemented")
}
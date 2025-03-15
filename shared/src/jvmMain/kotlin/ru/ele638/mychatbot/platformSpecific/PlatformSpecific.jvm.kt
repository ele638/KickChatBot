package ru.ele638.mychatbot.platformSpecific

import ru.ele638.mychatbot.app.data.JwmPlatform
import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import ru.ele638.mychatbot.app.util.DispatchersProvider
import ru.ele638.mychatbot.app.util.JVMDispatchersProvider

actual fun getPrefsProvider(): PrefsProvider {
    TODO("Not yet implemented")
}

actual fun getPlatform(): Platform = JwmPlatform()
actual fun getDispatchersProvider(): DispatchersProvider = JVMDispatchersProvider()
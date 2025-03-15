package ru.ele638.mychatbot.platformSpecific

import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.app.data.WasmPlatform
import ru.ele638.mychatbot.app.data.storage.CookiesProvider
import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import ru.ele638.mychatbot.app.util.DispatchersProvider
import ru.ele638.mychatbot.app.util.WasmDispatchersProvider

actual fun getPrefsProvider(): PrefsProvider = CookiesProvider()
actual fun getPlatform(): Platform = WasmPlatform()
actual fun getDispatchersProvider(): DispatchersProvider = WasmDispatchersProvider()
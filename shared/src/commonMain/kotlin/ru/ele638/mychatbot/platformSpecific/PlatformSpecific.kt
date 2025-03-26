package ru.ele638.mychatbot.platformSpecific

import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import ru.ele638.mychatbot.app.util.DispatchersProvider

expect fun getPlatform(): Platform
expect fun getPrefsProvider(): PrefsProvider
expect fun getDispatchersProvider(): DispatchersProvider
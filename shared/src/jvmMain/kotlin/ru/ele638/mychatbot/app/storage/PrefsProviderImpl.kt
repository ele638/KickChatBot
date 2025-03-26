package ru.ele638.mychatbot.app.storage

import ru.ele638.mychatbot.app.data.storage.PrefsProvider
import java.util.prefs.Preferences

class PrefsProviderImpl : PrefsProvider {
    private val prefs: Preferences = Preferences.userRoot().node("AppSettings")
    override fun get(key: String): String? {
        return prefs.get(key, EMPTY).takeIf { it != EMPTY }
    }

    override fun put(key: String, value: String) {
        prefs.put(key, value)
    }

    override fun put(vararg pairs: Pair<String, String>) {
        pairs.forEach {
            prefs.put(it.first, it.second)
        }
    }

    override fun clear() {
        prefs.clear()
    }

    companion object {
        const val EMPTY = "[EMPTY]"
    }
}
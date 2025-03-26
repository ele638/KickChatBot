package ru.ele638.mychatbot.storage

import platform.Foundation.NSUserDefaults
import ru.ele638.mychatbot.app.data.storage.PrefsProvider

class PrefsProviderImpl : PrefsProvider {
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults
    override fun get(key: String): String? {
        return defaults.stringForKey(key)
    }

    override fun put(key: String, value: String) {
        defaults.setObject(value, key)
    }

    override fun put(vararg pairs: Pair<String, String>) {
        pairs.forEach {
            put(it.first, it.second)
        }
    }

    override fun clear() {
        defaults.dictionaryRepresentation().forEach {
            defaults.removeObjectForKey(it.key.toString())
        }
    }
}
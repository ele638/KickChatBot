package ru.ele638.mychatbot.domain.storage

interface PrefsProvider {
    fun get(key: String): String?
    fun put(key: String, value: String)
    fun put(vararg pairs: Pair<String, String>)
    fun clear()
}

expect fun getPrefsProvider(): PrefsProvider
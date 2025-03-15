package ru.ele638.mychatbot.app.data.storage

interface PrefsProvider {
    fun get(key: String): String?
    fun put(key: String, value: String)
    fun put(vararg pairs: Pair<String, String>)
    fun clear()
}
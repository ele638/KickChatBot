package ru.ele638.mychatbot.app.data.storage

import io.github.aakira.napier.Napier
import kotlinx.browser.document

class CookiesProvider : PrefsProvider {
    override fun get(key: String): String? {
        Napier.v("Raw cookie: ${document.cookie}")
        return deserializeCookie(document.cookie)[key]
    }

    override fun put(key: String, value: String) {
        val oldCookieMap = deserializeCookie(document.cookie)
        val newCookieMap = oldCookieMap.plus(key to value)
        document.cookie = serializeCookie(newCookieMap)
    }

    override fun clear() {
        document.cookie = ""
    }

    override fun put(vararg pairs: Pair<String, String>) {
        pairs.forEach {
            document.cookie = serializeCookie(mapOf(it))
        }
    }

    private fun deserializeCookie(cookie: String): Map<String, String> =
        cookie.split(regex = Regex(";\\s*"))
            .asSequence()
            .onEach { Napier.v("split: $it") }
            .filter { key -> key.contains("=") }
            .map { key -> key.split("=") }
            .onEach { Napier.v("filtered and mapped: $it") }
            .filter { sublist -> sublist.size == 2 }
            .associate { sublist -> sublist[0] to sublist[1] }
            .also { Napier.v("Deserialized cookie: $it") }

    private fun serializeCookie(map: Map<String, String>) =
        map.entries
            .joinToString("; ") { entry -> "${entry.key}=${entry.value}" }
            .also { Napier.v("Serialized cookie: $it") }
}


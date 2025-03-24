package ru.ele638.mychatbot.app.util

import kotlinx.browser.window

class WasmUrlOpener : UrlOpener {
    override fun openUrl(url: String) {
        window.location.href = url
    }

    override fun getDeepLink(): String {
        return window.location.href
    }

    override fun clearDeepLink() {
        window.history.replaceState(null, "", window.location.pathname)
    }
}
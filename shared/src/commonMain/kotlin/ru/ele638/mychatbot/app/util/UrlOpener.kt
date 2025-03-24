package ru.ele638.mychatbot.app.util

interface UrlOpener {
    fun openUrl(url: String)
    fun getDeepLink(): String?
    fun clearDeepLink()
}
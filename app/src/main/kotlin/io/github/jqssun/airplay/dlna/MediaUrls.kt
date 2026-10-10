package io.github.jqssun.airplay.dlna

import java.net.URI

internal object MediaUrls {
    fun allowed(value: String): Boolean = try {
        val uri = URI(value)
        uri.scheme?.lowercase() in setOf("http", "https") &&
            !uri.host.isNullOrBlank() && uri.rawUserInfo == null && value.length <= 8192
    } catch (_: Exception) { false }
}

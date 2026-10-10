package io.github.jqssun.airplay.dlna

import java.io.InputStream
import java.io.ByteArrayOutputStream
import java.io.EOFException

/** Small, bounded HTTP/1 request reader; Content-Length counts bytes, not characters. */
internal object BoundedHttp {
    data class Request(val method: String, val path: String, val headers: Map<String, String>, val body: String)
    fun read(input: InputStream): Request {
        fun line(): String {
            val bytes = ByteArrayOutputStream()
            while (true) {
                val c = input.read()
                if (c < 0) throw EOFException()
                if (c == 10) return bytes.toString("US-ASCII").trimEnd('\r')
                require(bytes.size() < 8192) { "Line too long" }
                bytes.write(c)
            }
        }
        val parts = line().split(' ')
        require(parts.size == 3 && parts[2].startsWith("HTTP/1."))
        val headers = linkedMapOf<String, String>()
        var size = 0
        var count = 0
        while (true) {
            val value = line()
            if (value.isEmpty()) break
            size += value.length
            require(++count <= 64 && size <= 32768) { "Headers too large" }
            val split = value.indexOf(':')
            require(split > 0)
            val key = value.substring(0, split).trim().lowercase(java.util.Locale.ROOT)
            require(!headers.containsKey(key)) { "Duplicate header" }
            headers[key] = value.substring(split + 1).trim()
        }
        require(!headers.containsKey("transfer-encoding")) { "Chunked requests unsupported" }
        val length = headers["content-length"]?.toInt() ?: 0
        require(length in 0..262144) { "Body too large" }
        val bytes = ByteArray(length)
        var pos = 0
        while (pos < length) {
            val n = input.read(bytes, pos, length - pos)
            if (n < 0) throw EOFException()
            pos += n
        }
        return Request(parts[0].uppercase(java.util.Locale.ROOT), parts[1], headers, bytes.toString(Charsets.UTF_8))
    }
}

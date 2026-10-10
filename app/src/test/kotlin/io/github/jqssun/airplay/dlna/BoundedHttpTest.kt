package io.github.jqssun.airplay.dlna
import org.junit.Assert.*
import org.junit.Test

class BoundedHttpTest {
    private fun parse(s: String) = BoundedHttp.read(s.byteInputStream())
    @Test fun utf8BodyUsesByteLength() {
        val body = "<title>छवि 📷</title>"
        val request = parse("POST /ctrl/AVTransport HTTP/1.1\r\nContent-Length: ${body.toByteArray().size}\r\n\r\n$body")
        assertEquals(body, request.body)
    }
    @Test fun boundedMalformedRequests() {
        for (request in listOf(
            "GET / HTTP/1.1\r\nX: ${"a".repeat(8193)}\r\n\r\n",
            "POST / HTTP/1.1\r\nContent-Length: 262145\r\n\r\n",
            "POST / HTTP/1.1\r\nContent-Length: -1\r\n\r\n",
            "POST / HTTP/1.1\r\nContent-Length: 8\r\n\r\nshort",
            "GET / HTTP/1.1\r\n" + (1..65).joinToString("") { "X-$it: a\r\n" } + "\r\n",
            "POST / HTTP/1.1\r\nContent-Length: 1\r\nContent-Length: 2\r\n\r\nx",
            "POST / HTTP/1.1\r\nTransfer-Encoding: chunked\r\n\r\n"
        )) {
            try { parse(request); fail("Accepted malformed or oversized input") } catch (_: Exception) {}
        }
    }
    @Test fun mediaSchemes() {
        assertTrue(MediaUrls.allowed("http://192.168.0.2:8080/photo.jpg"))
        assertTrue(MediaUrls.allowed("https://example.org/movie.mp4"))
        for (uri in listOf("file:///data/user/0/private", "content://settings/system", "ftp://host/photo", "http://user:pass@host/file", "http:///file", "bad")) assertFalse(MediaUrls.allowed(uri))
    }
}

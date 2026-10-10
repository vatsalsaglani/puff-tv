package io.github.jqssun.airplay.renderer

/** Inspect every Annex-B NAL, including units preceded by SEI or access-unit delimiters. */
internal object AnnexB {
    fun isKeyframe(data: ByteArray, h265: Boolean): Boolean {
        var i = 0
        while (i + 3 < data.size) {
            if (data[i] == 0.toByte() && data[i + 1] == 0.toByte()) {
                val start = when {
                    data[i + 2] == 1.toByte() -> i + 3
                    i + 4 < data.size && data[i + 2] == 0.toByte() && data[i + 3] == 1.toByte() -> i + 4
                    else -> -1
                }
                if (start >= 0) {
                    val type = if (h265) (data[start].toInt() shr 1) and 63 else data[start].toInt() and 31
                    if (if (h265) type in setOf(19, 20, 21, 32, 33) else type == 5 || type == 7) return true
                    i = start
                }
            }
            i++
        }
        return false
    }
}

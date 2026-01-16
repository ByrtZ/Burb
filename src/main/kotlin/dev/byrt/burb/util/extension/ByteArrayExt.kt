package dev.byrt.burb.util.extension

/**
 * Reads a long from a byte array at a specific offset.
 */
fun ByteArray.longAt(offset: Int): Long {
    require(offset + 8 <= size) { "Offset $offset is out of bounds for array of size $size" }
    var out = 0L
    for (i in 0 until 8) {
        out = (out shl 8) or (this[offset + i].toLong() and 0xFF)
    }
    return out
}
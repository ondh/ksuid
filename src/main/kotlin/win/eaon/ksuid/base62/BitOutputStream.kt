package win.eaon.ksuid.base62

import java.util.*
import kotlin.experimental.or

class BitOutputStream(capacity: Int) {
    private val buffer: ByteArray = ByteArray(capacity / 8)
    private var offset = 0

    fun writeBits(bitsCount: Int, bits: Int) {
        val bitNum = offset % 8
        val byteNum = offset / 8

        val firstWrite = Math.min(8 - bitNum, bitsCount)
        val secondWrite = bitsCount - firstWrite

        buffer[byteNum] = buffer[byteNum] or (bits and (1 shl firstWrite) - 1 shl bitNum).toByte()
        if (secondWrite > 0) {
            buffer[byteNum + 1] = buffer[byteNum + 1] or (bits.ushr(firstWrite) and (1 shl secondWrite) - 1).toByte()
        }

        offset += bitsCount
    }

    fun toArray(): ByteArray {
        val newLength = offset / 8
        return if (newLength == buffer.size) buffer else Arrays.copyOf(buffer, newLength)
    }

    val bitsCountUpToByte: Int
        get() {
            val currentBit = offset % 8
            return if (currentBit == 0) 0 else 8 - currentBit
        }
}
package win.eaon.ksuid.base62

class BitInputStream(private val buffer: ByteArray) {
    private var offset = 0

    fun seekBit(pos: Int) {
        offset += pos
        if (offset < 0 || offset > buffer.size * 8) {
            throw IndexOutOfBoundsException()
        }
    }

    fun readBits(bitsCount: Int): Int {
        if (bitsCount < 0 || bitsCount > 7) {
            throw IndexOutOfBoundsException()
        }

        val bitNum = offset % 8
        val byteNum = offset / 8

        val firstRead = Math.min(8 - bitNum, bitsCount)
        val secondRead = bitsCount - firstRead

        var result = (buffer[byteNum].toInt() and ((1 shl firstRead) - 1 shl bitNum)).ushr(bitNum)
        if (secondRead > 0 && byteNum + 1 < buffer.size) {
            result = result or (buffer[byteNum + 1].toInt() and ((1 shl secondRead) - 1) shl firstRead)
        }
        offset += bitsCount
        return result
    }

    fun hasMore(): Boolean {
        return offset < buffer.size * 8
    }
}
package win.eaon.ksuid.base62

class Base62 {
    /**
     * This array is a lookup table that translates 6-bit positive integer index values into their "Base62 Alphabet"
     * equivalents as specified in Table 1 of RFC 2045 excepting special characters for 62 and 63 values.
     *
     * Thanks to "commons" project in ws.apache.org for this code.
     * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
     */
    private val ENCODE_TABLE = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    /**
     * This array is a lookup table that translates Unicode characters drawn from the "Base64 Alphabet" (as specified in
     * Table 1 of RFC 2045) into their 6-bit positive integer equivalents. Characters that are not in the Base62
     * alphabet but fall within the bounds of the array are translated to -1.
     *
     * Note that there is no special characters in Base62 alphabet that could represent 62 and 63 values, so they both
     * is absent in this decode table.
     *
     * Thanks to "commons" project in ws.apache.org for this code.
     * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
     */
    private val DECODE_TABLE = byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51)

    /**
     * Special mask for the data that should be written in compact 5-bits form
     */
    private val COMPACT_MASK = 0x1E // 00011110

    /**
     * Mask for extracting 5 bits of the data
     */
    private val MASK_5BITS = 0x1F // 00011111

    /**
     * Encodes binary data using a Base62 algorithm.
     * @param data binary data to encode
     * @return String containing Base62 characters
     */
    fun encode(data: ByteArray): String {
        // Reserving capacity for the worst case when each output character represents compacted 5-bits data
        val sb = StringBuilder(data.size * 8 / 5 + 1)

        val inputStream = BitInputStream(data)
        while (inputStream.hasMore()) {
            // Read not greater than 6 bits from the stream
            val rawBits = inputStream.readBits(6)

            // For some cases special processing is needed, so _bits_ will contain final data representation needed to
            // form next output character
            val bits: Int
            if (rawBits and COMPACT_MASK == COMPACT_MASK) {
                // We can't represent all 6 bits of the data, so extract only least significant 5 bits and return for
                // one bit back in the stream
                bits = rawBits and MASK_5BITS
                inputStream.seekBit(-1)
            } else {
                // In most cases all 6 bits used to form output character
                bits = rawBits
            }
            // Look up next character in the encoding table and append it to the output StringBuilder
            sb.append(ENCODE_TABLE[bits])
        }
        return sb.toString()
    }

    /**
     * Decodes a Base62 String into byte array.
     * @param base62String String containing Base62 data
     * @return Array containing decoded data.
     */
    fun decode(base62String: String): ByteArray {
        val length = base62String.length

        // Create stream with capacity enough to fit
        val out = BitOutputStream(length * 6)

        val lastCharPos = length - 1
        for (i in 0..length - 1) {
            // Obtain data bits from decoding table for the next character
            val bits = decodedBitsForCharacter(base62String[i])

            // Determine bits count needed to write to the stream
            val bitsCount: Int
            if (bits and COMPACT_MASK == COMPACT_MASK) {
                // Compact form detected, write down only 5 bits
                bitsCount = 5
            } else if (i >= lastCharPos) {
                // For the last character write down all bits that needed for the completion of the stream
                bitsCount = out.bitsCountUpToByte
            } else {
                // In most cases the full 6-bits form will be used
                bitsCount = 6
            }
            out.writeBits(bitsCount, bits)
        }
        return out.toArray()
    }

    private fun decodedBitsForCharacter(character: Char): Int {
        val result: Int = DECODE_TABLE[character.toInt()].toInt()
        if (character.toInt() >= DECODE_TABLE.size || result < 0) {
            throw IllegalArgumentException("Wrong Base62 symbol found: " + character)
        }
        return result
    }
}
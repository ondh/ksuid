package win.eaon.ksuid

import win.eaon.ksuid.base62.Base62
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class Ksuid {
    private val EPOCH = 1400000000
    private val TIMESTAMP_LENGTH = 4
    private val PAYLOAD_LENGTH = 16
    private val MAX_ENCODED_LENGTH = 27

    companion object {
        /**
         * Generate a new KSUID.
         *
         * @return New KSUID string.
         */
        @JvmStatic fun generateUid():String{
            return Ksuid().generate()
        }

        /**
         * Generate a new KSUID from date.
         *
         * @param date date for timestamp
         * @return New KSUID string.
         */
        @JvmStatic fun generateUid(date: Date):String{
            return Ksuid().generate(date)
        }

        /**
         * Parse a KSUID string to show information.
         *
         * @param ksuid KSUID string
         * @return KSUID detail.
         */
        @JvmStatic fun parseUid(ksuid: String): String {
            return Ksuid().parse(ksuid)
        }

        /**
         * Get the timestamp of the KSUID string.
         *
         * @param ksuid KSUID string
         * @return timestamp in millisecond.
         */
        @JvmStatic fun getTimestampFromUid(ksuid: String): Long {
            return Ksuid().getTimestamp(ksuid)
        }
    }

    /**
     * Generate a new KSUID.
     *
     * @return New KSUID string.
     */
    fun generate(): String {
        val timestamp = generateTimestamp()
        return generate(timestamp)
    }

    /**
     * Generate a new KSUID from date.
     *
     * @param date date for timestamp
     * @return New KSUID string.
     */
    fun generate(date: Date): String {
        val timestamp = generateTimestamp(date)
        return generate(timestamp)
    }

    private fun generate(timestamp:ByteArray): String {
        val random = SecureRandom()
        val payload = generatePayload(random)

        val output = ByteArrayOutputStream()
        try {
            output.write(timestamp)
            output.write(payload)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val uid = Base62().encode(output.toByteArray())
        if (uid.length > MAX_ENCODED_LENGTH) {
            return uid.substring(0, MAX_ENCODED_LENGTH)
        }
        return uid
    }

    /**
     * Parse a KSUID string to show information.
     *
     * @param ksuid KSUID string
     * @return KSUID detail.
     */
    fun parse(ksuid: String): String {
        val bytes = Base62().decode(ksuid)

        val timestamp = decodeTimestamp(bytes)
        val payload = decodePayload(bytes)

        val utcTimeString = Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("UTC"))
        return String.format("Time: %s\nTimestamp: %d\nPayload: %s", utcTimeString, timestamp * 1000, payload)
    }

    /**
     * Get the timestamp of the KSUID string.
     *
     * @param ksuid KSUID string
     * @return timestamp in millisecond.
     */
    fun getTimestamp(ksuid: String): Long {
        val bytes = Base62().decode(ksuid)
        return decodeTimestamp(bytes) * 1000
    }

    private fun generateTimestamp(): ByteArray {
        val utc = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000
        val timestamp = (utc - EPOCH).toInt()
        return ByteBuffer.allocate(TIMESTAMP_LENGTH).putInt(timestamp).array()
    }

    private fun generateTimestamp(date: Date): ByteArray {
        val utc = date.toInstant().toEpochMilli() / 1000
        val timestamp = (utc - EPOCH).toInt()
        return ByteBuffer.allocate(TIMESTAMP_LENGTH).putInt(timestamp).array()
    }

    private fun generatePayload(random: SecureRandom): ByteArray {
        val bytes = ByteArray(PAYLOAD_LENGTH)
        random.nextBytes(bytes)
        return bytes
    }

    private fun decodeTimestamp(decodedKsuid: ByteArray): Long {
        val timestamp = ByteArray(TIMESTAMP_LENGTH)
        System.arraycopy(decodedKsuid, 0, timestamp, 0, TIMESTAMP_LENGTH)
        return ByteBuffer.wrap(timestamp).int.toLong() + EPOCH
    }

    private fun decodePayload(decodedKsuid: ByteArray): String {
        val payload = ByteArray(PAYLOAD_LENGTH)
        System.arraycopy(decodedKsuid, TIMESTAMP_LENGTH, payload, 0, decodedKsuid.size - TIMESTAMP_LENGTH)
        return Arrays.toString(payload)
    }
}
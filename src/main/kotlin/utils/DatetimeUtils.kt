package utils

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    private val pubDateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    private val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun convertPubDateToLocalDateString(pubDate: String): String {
        // GMT → +0000 으로 치환
        val correctedDate = pubDate.replace("GMT", "+0000")
        val zonedDateTime = ZonedDateTime.parse(correctedDate, pubDateFormatter)
        val localDateTime = zonedDateTime.toLocalDateTime()
        return localDateTime.format(outputFormatter)
    }

    fun convertPubDateToLocalDateTime(pubDate: String): LocalDateTime {
        val correctedDate = pubDate.replace("GMT", "+0000")
        val zonedDateTime = ZonedDateTime.parse(correctedDate, pubDateFormatter)
        return zonedDateTime.toLocalDateTime()
    }
}

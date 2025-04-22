package utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    fun parsePubDate(pubDate: String): ZonedDateTime = ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME)
}

package model

import java.time.ZonedDateTime

data class Item(
    val title: String,
    val link: String,
    val pubDate: ZonedDateTime,
) {
    override fun toString(): String {
        val date = pubDate.toLocalDate() // 또는 pubDate.format(...)
        return "$title ($date) - $link"
    }
}

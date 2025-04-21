package model

import utils.DateTimeUtils

data class Item(
    val title: String,
    val link: String,
    val pubDate: String,
) {
    override fun toString(): String {
        val date = DateTimeUtils.convertPubDateToLocalDateString(pubDate)
        return "$title ($date) - $link"
    }
}

package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import model.Item
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

suspend fun BlogRss.parseRss(url: String): List<Item> {
    val itemList = mutableListOf<Item>()
    val factory = DocumentBuilderFactory.newInstance()
    val xml =
        withContext(Dispatchers.IO) {
            async { factory.newDocumentBuilder().parse(URL(url).openStream()) }
        }.await()

    val items: NodeList = xml.getElementsByTagName("item")

    withContext(Dispatchers.Default) {
        for (i in 0 until items.length) {
            val node: Node = items.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val elem = node as Element

                val title = elem.getElementsByTagName("title").item(0)?.textContent ?: ""
                val link = elem.getElementsByTagName("link").item(0)?.textContent ?: ""
                val pubDateRaw = elem.getElementsByTagName("pubDate").item(0)?.textContent ?: ""

                val pubDate = DateTimeUtils.parsePubDate(pubDateRaw)

                itemList.add(Item(title, link, pubDate))
            }
        }
    }

    return itemList
}

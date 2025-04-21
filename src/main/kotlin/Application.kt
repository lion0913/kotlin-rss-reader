import model.Channel
import model.Item
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import utils.DateTimeUtils
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

fun main() {
    val factory = DocumentBuilderFactory.newInstance()
    val xml =
        factory
            .newDocumentBuilder()
            .parse(URL("https://developers.hyundaimotorgroup.com/blog/rss").openStream())

    xml.documentElement.normalize()

    val items: NodeList = xml.getElementsByTagName("item")
    val itemList = mutableListOf<Item>()

    for (i in 0 until items.length) {
        val node: Node = items.item(i)
        if (node.nodeType == Node.ELEMENT_NODE) {
            val elem = node as Element

            val title = elem.getElementsByTagName("title").item(0)?.textContent ?: ""
            val link = elem.getElementsByTagName("link").item(0)?.textContent ?: ""
            val pubDateRaw = elem.getElementsByTagName("pubDate").item(0)?.textContent.toString()
            val pubDate = DateTimeUtils.convertPubDateToLocalDateString(pubDateRaw)
            itemList.add(Item(title, link, pubDate))
        }
    }

    val channel = Channel(itemList)
    val list = channel.items

    while (true) {
        println()
        print("검색어를 입력하세요 (없으면 전체 출력): ")
        val keyword = readLine()?.trim()

        val filtered =
            if (keyword.isNullOrEmpty()) {
                list.sortedByDescending { it.pubDate }.take(10)
            } else {
                list.filter { it.title.contains(keyword, ignoreCase = true) }
                    .sortedByDescending { it.pubDate }
                    .take(10)
            }

        println()
        if (filtered.isEmpty()) {
            println("검색 결과가 없습니다.")
        } else {
            filtered.forEachIndexed { index, item ->
                println("[${index + 1}] ${item.title} (${item.pubDate}) - ${item.link}")
            }
        }
    }
}

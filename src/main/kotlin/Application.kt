import model.Item
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import utils.BlogRss
import utils.DateTimeUtils
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

suspend fun main() {
    while (true) {
        println("\n검색어를 입력하세요 (없으면 전체 출력, 종료하려면 exit):")
        val input = readlnOrNull()?.trim()

        if (input.equals("exit", ignoreCase = true)) {
            println("종료합니다.")
            break
        }

        val allItems =
            BlogRss.entries.flatMap { blog ->
//                println("\n🔔 ${blog.title}")
//                println("─────────────────────────────────────")

                val items =
                    try {
                        parseRss(blog.rssUrl)
                    } catch (e: Exception) {
                        println("⚠️  ${blog.title} RSS 파싱 실패: ${e.message}")
                        return@flatMap emptyList<Item>()
                    }

                items
            }

        val filtered =
            if (input.isNullOrBlank()) {
                allItems
            } else {
                allItems.filter { it.title.contains(input, ignoreCase = true) }
            }.sortedByDescending { it.pubDate } // ZonedDateTime이라 바로 정렬 가능
                .take(10)

        println()
        filtered.forEachIndexed { index, item ->
            println("[${index + 1}] $item")
        }

        if (filtered.isEmpty()) {
            println("🔍 검색 결과가 없습니다.")
        }
    }
}

suspend fun parseRss(url: String): List<Item> {
    val factory = DocumentBuilderFactory.newInstance()
    val xml = factory.newDocumentBuilder().parse(URL(url).openStream())
    xml.documentElement.normalize()

    val items: NodeList = xml.getElementsByTagName("item")
    val itemList = mutableListOf<Item>()

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

    return itemList.sortedByDescending { it.pubDate }
}

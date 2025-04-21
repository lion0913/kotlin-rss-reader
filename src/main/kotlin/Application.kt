import model.Channel
import model.Item
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


fun main() {
    val factory = DocumentBuilderFactory.newInstance()
    val xml = factory
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
            val pubDate = elem.getElementsByTagName("pubDate").item(0)?.textContent ?: ""

            itemList.add(Item(title, link, pubDate))
        }
    }

    val channel = Channel(itemList)

    // 결과 출력
    channel.items.forEachIndexed { index, item ->
        println("=========${index + 1}=========")
        println("제목: ${item.title}")
        println("링크: ${item.link}")
        println("발행일: ${item.pubDate}")
    }
}

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Item
import utils.BlogRss
import utils.parseRss
import java.time.ZonedDateTime
import kotlin.system.measureTimeMillis

suspend fun main() {
    startAutoRefresh()

    while (true) {
        println("\n검색어를 입력하세요 (없으면 전체 출력, 종료하려면 exit):")
        val input = readlnOrNull()?.trim()

        if (input.equals("exit", ignoreCase = true)) {
            println("종료합니다.")
            break
        }

        val allItems = mutableListOf<Item>()
        val time =
            measureTimeMillis {
                getBlogItems(allItems)
            }

        val filtered =
            if (input.isNullOrBlank()) {
                allItems
            } else {
                allItems.filter { it.title.contains(input, ignoreCase = true) }
            }.sortedByDescending { it.pubDate }.take(10)

        println()
        filtered.forEachIndexed { index, item ->
            println("[${index + 1}] $item")
        }

        if (filtered.isEmpty()) {
            println("🔍 검색 결과가 없습니다.")
        }

        println("⏱ 실행 시간: $time ms")
    }
}

private suspend fun getBlogItems(allItems: MutableList<Item>) {
    withContext(Dispatchers.IO) {
        BlogRss.entries
            .map { rss ->
                async {
                    allItems += rss.parseRss(rss.rssUrl)
                }
            }.awaitAll()
    }
}

private fun startAutoRefresh() {
    // 10분 주기로 신규 게시글 체크
    GlobalScope.launch(Dispatchers.Default) {
        while (true) {
            delay(1000 * 60 * 10L)

            val newItems = withContext(Dispatchers.IO) {
                BlogRss.entries.map { async { it.parseRss(it.rssUrl) } }.awaitAll()
            }.flatten()
                .filter { it.pubDate.isAfter(ZonedDateTime.now().minusMinutes(10)) }

            if (newItems.isNotEmpty()) {
                println("[신규 게시글]")
                newItems.forEach { println("[NEW] $it") }
            }
        }
    }
}

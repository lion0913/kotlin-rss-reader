import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Item
import utils.BlogRss
import utils.parseRss
import java.time.ZonedDateTime
import kotlin.system.measureTimeMillis

suspend fun main() {
    while (true) {
        /**
         * 입력부
         */
        println("\n검색어를 입력하세요 (없으면 전체 출력, 종료하려면 exit):")
        val input = readlnOrNull()?.trim()

        if (input.equals("exit", ignoreCase = true)) {
            println("종료합니다.")
            break
        }
        val refreshItems = mutableListOf<Item>()
        val allItems = mutableListOf<Item>()

        // 10분주기
        GlobalScope.launch(Dispatchers.Default) {
            delay(1000 * 60 * 10L)
            withContext(Dispatchers.IO) {
                BlogRss.entries.map {
                    async { refreshItems += it.parseRss(it.rssUrl) }
                }
            }

            val filtered = refreshItems.filter { it.pubDate.isAfter(ZonedDateTime.now().minusMinutes(10L)) }
            if (filtered.isNotEmpty()) {
                println("신규 글이 올라왔습니다.")
                filtered.forEachIndexed { index, item ->
                    println("[NEW] $item")
                }
            }
        }

        // 파싱
        val time =
            measureTimeMillis {
                // 1. 블로그 게시글 가져오기
                withContext(Dispatchers.IO) {
                    BlogRss.entries.map {
                        async { allItems += it.parseRss(it.rssUrl) }
                    }
                }

                // 2. 검색어 필터링
                val filtered =
                    if (input.isNullOrBlank()) {
                        allItems
                    } else {
                        allItems.filter { it.title.contains(input, ignoreCase = true) }
                    }.sortedByDescending { it.pubDate }
                        .take(10)

                println()

                // 3. 출력
                filtered.forEachIndexed { index, item ->
                    println("[${index + 1}] $item")
                }

                if (filtered.isEmpty()) {
                    println("🔍 검색 결과가 없습니다.")
                }
            }

        println("Execution took $time ms")
    }
}

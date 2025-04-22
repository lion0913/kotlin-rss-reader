import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import model.Item
import utils.BlogRss
import utils.parseRss
import kotlin.system.measureTimeMillis

suspend fun main() {
    while (true) {
        println("\n검색어를 입력하세요 (없으면 전체 출력, 종료하려면 exit):")
        val input = readlnOrNull()?.trim()

        if (input.equals("exit", ignoreCase = true)) {
            println("종료합니다.")
            break
        }

        val time =
            measureTimeMillis {
                // 1. 블로그 게시글 가져오기
                val allItems = mutableListOf<Item>()

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

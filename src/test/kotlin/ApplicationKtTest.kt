import io.mockk.coEvery
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import model.Item
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utils.BlogRss
import utils.DateTimeUtils
import utils.parseRss
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class ApplicationKtTest {
    @Test
    fun `RSS 파싱이 정상적으로 동작해야 한다`() =
        runBlocking {
            val url = "https://tech.kakao.com/feed143/"
            val result = BlogRss.KAKAO.parseRss(url)

            assertTrue(result.isNotEmpty())
            assertTrue(result.all { it.title.isNotBlank() })
            assertTrue(result.all { it.link.startsWith("http") })
        }

    @Test
    fun `PubDate 파싱이 정상 동작해야 한다`() {
        // given
        val input = "2025-04-22" // 포맷이 잘못된 pubDate

        // when
        val result = DateTimeUtils.parsePubDate(input)

        // then
        assertEquals(2025, result.year)
        assertEquals(22, result.dayOfMonth)
        assertEquals(4, result.hour)
        assertEquals(43, result.minute)
        assertEquals(23, result.second)
        assertEquals("Z", result.offset.toString()) // GMT == UTC == Z
    }

    @Test
    fun `블로그 파싱 중에 에러가 발생해도 어플리케이션이 종료되지 않는다`() =
        runBlocking {
            val allItems = mutableListOf<Item>()
            getBlogItems(allItems)
            assertTrue(allItems.isNotEmpty())
        }

    @Test
    fun `BlogRss 중 일부 parseRss 실패해도 전체 앱이 멈추지 않는다`() =
        runBlocking {
            val allItems = mutableListOf<Item>()
            val url = "http://hyundai.com"

            mockkObject(BlogRss.HYUNDAI)
            mockkObject(BlogRss.KAKAO)

            coEvery { BlogRss.HYUNDAI.parseRss(url) } returns
                listOf(
                    Item("정상 포스트", url, ZonedDateTime.now()),
                )

            coEvery { BlogRss.KAKAO.parseRss(any()) } throws RuntimeException("파싱 실패")

            // when
            withContext(Dispatchers.IO) {
                BlogRss.entries.map { rss ->
                    async {
                        try {
                            allItems += rss.parseRss(rss.rssUrl)
                        } catch (e: Exception) {
                            println("error: ${rss.name}, message :  ${e.message}")
                        }
                    }
                }.awaitAll()
            }

            // then
            assertEquals(1, allItems.size)
            assertEquals("정상 포스트", allItems.first().title)
        }
}

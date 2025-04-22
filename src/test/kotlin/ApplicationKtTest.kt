import kotlinx.coroutines.runBlocking
import model.Item
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utils.BlogRss
import utils.DateTimeUtils
import utils.parseRss
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
//        val input = "Tue, 22 Apr 2025 04:43:23 GMT"

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
}

import kotlinx.coroutines.runBlocking
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
            val url = "https://tech.kakao.com/feed/"
            val result = BlogRss.KAKAO.parseRss(url)

            assertTrue(result.isNotEmpty())
            assertTrue(result.all { it.title.isNotBlank() })
            assertTrue(result.all { it.link.startsWith("http") })
        }

    @Test
    fun `PubDate 파싱이 정상 동작해야 한다`() {
        // given
        val input = "Tue, 22 Apr 2025 04:43:23 GMT"

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
}

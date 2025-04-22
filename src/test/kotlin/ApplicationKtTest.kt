import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utils.BlogRss
import utils.parseRss

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
}

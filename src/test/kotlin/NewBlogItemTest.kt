import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.random.Random

class NewBlogItemTest {
    @Test
    fun `일정 주기마다 신규 게시글이 노출된다`() =
        runTest {
            val randomSecond = Random.nextInt(10) * 1000L
            startAutoRefresh(randomSecond)
        }
}

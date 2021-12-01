package starter

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import starter.books.enrichment.BookInformation
import starter.books.enrichment.BookInformationSource

@SpringBootTest
internal class ApplicationTest {

    @Test
    fun `application context can be loaded`() {
        // nothing to do
    }

}

@Component
class DummyBookInformationSource : BookInformationSource {
    override fun getBookInformation(isbn: String): BookInformation? = null
}

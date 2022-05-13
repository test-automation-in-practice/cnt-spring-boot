package advanced.unit

import advanced.unit.admin.AdminMediaRepository
import advanced.unit.api.MediaCollectionController
import advanced.unit.domain.Book
import advanced.unit.domain.MediaCollection
import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.logback.junit5.RecordLoggers
import io.restassured.RestAssured
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.fromString

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationTests(
    @Autowired val repository: AdminMediaRepository
) {

    @BeforeEach
    fun setup(@LocalServerPort port: Int) {
        RestAssured.port = port
    }

    @Test
    @RecordLoggers(MediaCollectionController::class, MediaCollection::class)
    fun `registering a book creates correct log entries`(log: LogRecord) {
        Given {
            contentType(JSON)
            body(
                """
                {
                  "type": "BOOK",
                  "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                  "label": "We Are Legion (We Are Bob)"
                }
                """
            )
        }.When {
            post("/api/media")
        } Then {
            statusCode(202)
        }

        waitForAsyncProcessingToBeDone()

        assertThat(log) containsExactly {
            any("received registration of new media item: MediaRegistration(type=BOOK, id=b3fc0be8-463e-4875-9629-67921a1e00f4, label=We Are Legion (We Are Bob))")
            any("registering new Book: b3fc0be8-463e-4875-9629-67921a1e00f4")
            any(matches("""registered Book b3fc0be8-463e-4875-9629-67921a1e00f4 with media ID [a-z0-9-]+?\.""")) // only pattern matching possible
        }
    }

    @Test
    fun `registering a book adds a new media item`() {
        assertThat(repositoryContainsBookWithId("aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")).isFalse()

        Given {
            contentType(JSON)
            body(
                """
                {
                  "type": "BOOK",
                  "id": "aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2",
                  "label": "For We Are Many"
                }
                """
            )
        }.When {
            post("/api/media")
        } Then {
            statusCode(202)
        }

        waitForAsyncProcessingToBeDone()

        assertThat(repositoryContainsBookWithId("aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")).isTrue()
    }

    // alternatives would be:
    //   - disable async handling for tests (no longer "real" E2E test)
    //   - inject more components and use something like Awaitillity to check expected conditions for certain amount of time
    // none of which are ideal
    private fun waitForAsyncProcessingToBeDone() {
        Thread.sleep(1_000) // bad style and unreliable!
    }

    // vague assertion because order of tests is not deterministic and there might be more than one
    private fun repositoryContainsBookWithId(id: String) =
        repository.getAll().any { it is Book && it.id == fromString(id) }

}

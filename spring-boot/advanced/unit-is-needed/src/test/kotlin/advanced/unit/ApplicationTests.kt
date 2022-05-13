package advanced.unit

import advanced.unit.Examples.book_bobiverse1_registration_json
import advanced.unit.Examples.book_bobiverse2_registration_json
import advanced.unit.Examples.book_bobiverse3_id
import advanced.unit.Examples.book_bobiverse3_registration_json
import advanced.unit.Examples.game_eldenring_registration_json
import advanced.unit.Examples.registrationJson
import advanced.unit.Examples.stringOfLength
import advanced.unit.admin.AdminMediaRepository
import advanced.unit.api.MediaCollectionController
import advanced.unit.api.MediaCollectionController.MediaRegistration
import advanced.unit.domain.Book
import advanced.unit.domain.MediaCollection
import advanced.unit.domain.TypeOfMedia.BOOK
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
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.util.UUID
import java.util.UUID.randomUUID

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

    fun registerMediaItem(jsonBody: String, expectedStatus: Int = 202) {
        Given {
            contentType(JSON)
            body(jsonBody)
        }.When {
            post("/api/media")
        } Then {
            statusCode(expectedStatus)
        }
    }

    @Test
    fun `registering a book responds with success`() {
        registerMediaItem(book_bobiverse1_registration_json)
    }

    @Test
    fun `registering a game responds with success`() {
        registerMediaItem(game_eldenring_registration_json)
    }

    @Test
    @RecordLoggers(MediaCollectionController::class, MediaCollection::class)
    fun `registering a book creates correct log entries`(log: LogRecord) {
        registerMediaItem(book_bobiverse2_registration_json)

        waitForAsyncProcessingToBeDone()

        assertThat(log) containsExactly {
            any("received registration of new media item: MediaRegistration(type=BOOK, id=aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2, label=For We Are Many)")
            any("registering new Book: aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")
            any(matches("""registered Book aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2 with media ID [a-z0-9-]+?\.""")) // only pattern matching possible
        }
    }

    @Test
    fun `registering a book adds a new media item`() {
        assertThat(repositoryContainsBookWithId(book_bobiverse3_id)).isFalse()
        registerMediaItem(book_bobiverse3_registration_json)

        waitForAsyncProcessingToBeDone()

        assertThat(repositoryContainsBookWithId(book_bobiverse3_id)).isTrue()
    }

    @TestFactory
    fun `length of registration label is limited to 100`() =
        listOf(99 to true, 100 to true, 101 to false)
            .map { (length, valid) ->
                dynamicTest("label with $length characters is ${if (valid) "valid" else "invalid"}") {
                    val label = stringOfLength(length)
                    val registration = MediaRegistration(BOOK, randomUUID(), label)
                    val json = registrationJson(registration)
                    if (valid) {
                        registerMediaItem(json)
                    } else {
                        registerMediaItem(json, expectedStatus = 400)
                    }
                }
            }

    // alternatives would be:
    //   - disable async handling for tests (no longer "real" E2E test)
    //   - inject more components and use something like Awaitillity to check expected conditions for certain amount of time
    // none of which are ideal
    private fun waitForAsyncProcessingToBeDone() {
        Thread.sleep(1_000) // bad style and unreliable!
    }

    // vague assertion because order of tests is not deterministic and there might be more than one
    private fun repositoryContainsBookWithId(id: UUID) =
        repository.getAll().any { it is Book && it.id == id }

}

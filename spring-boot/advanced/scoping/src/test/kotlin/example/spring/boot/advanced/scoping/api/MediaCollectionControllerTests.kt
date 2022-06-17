package example.spring.boot.advanced.scoping.api

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.advanced.scoping.Examples
import example.spring.boot.advanced.scoping.Examples.book_bobiverse1
import example.spring.boot.advanced.scoping.Examples.book_bobiverse1_registration_json
import example.spring.boot.advanced.scoping.Examples.book_bobiverse2_registration_json
import example.spring.boot.advanced.scoping.Examples.gameRegistrationJson
import example.spring.boot.advanced.scoping.Examples.game_eldenring
import example.spring.boot.advanced.scoping.Examples.game_eldenring_registration_json
import example.spring.boot.advanced.scoping.domain.MediaCollection
import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsOnly
import io.github.logrecorder.logback.junit5.RecordLoggers
import io.mockk.verify
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(MediaCollectionController::class)
@MockkBean(MediaCollection::class, relaxUnitFun = true)
internal class MediaCollectionControllerTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val collection: MediaCollection
) {

    @Test
    fun `posting a registration creates the appropriate media item - book`() {
        mockMvc.post("/api/media") {
            contentType = APPLICATION_JSON
            content = book_bobiverse1_registration_json
        }.andExpect {
            status { isAccepted() }
            content { string("") }
        }

        verify { collection.register(book_bobiverse1) }
    }

    @Test
    fun `posting a registration creates the appropriate media item - game`() {
        mockMvc.post("/api/media") {
            contentType = APPLICATION_JSON
            content = game_eldenring_registration_json
        }.andExpect {
            status { isAccepted() }
            content { string("") }
        }

        verify { collection.register(game_eldenring) }
    }

    @Test
    @RecordLoggers(MediaCollectionController::class)
    fun `posting a registration logs`(log: LogRecord) {
        mockMvc.post("/api/media") {
            contentType = APPLICATION_JSON
            content = book_bobiverse2_registration_json
        }.andExpect { status { is2xxSuccessful() } }

        val expectedRegistrationLogRepresentation =
            "MediaRegistration(type=BOOK, id=aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2, label=For We Are Many)"

        assertThat(log) containsOnly {
            debug("received registration of new media item: $expectedRegistrationLogRepresentation")
        }
    }

    @TestFactory
    fun `length of registration label is limited to 100`() =
        listOf(99 to true, 100 to true, 101 to false)
            .map { (length, valid) ->
                dynamicTest("label with $length characters is ${if (valid) "valid" else "invalid"}") {
                    val label = Examples.stringOfLength(length)
                    mockMvc.post("/api/media") {
                        contentType = APPLICATION_JSON
                        content = gameRegistrationJson(label = label)
                    }.andExpect {
                        status { if (valid) is2xxSuccessful() else isBadRequest() }
                    }
                }
            }

}

package consumerone

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.hateoas.MediaTypes
import org.springframework.web.client.RestTemplate

@ExtendWith(PactConsumerTestExt::class)
internal class LibraryAccessorContractTest {

    val settings = LibraryAccessorSettings()
    val cut = LibraryAccessor(RestTemplate(), settings)

    @Pact(provider = "provider", consumer = "consumer-one")
    fun getExistingBookPact(pact: PactDslWithProvider) = pact
        .given("Getting book with any ID returns Clean Code")
        .uponReceiving("get single book")
        .path("/books/b3fc0be8-463e-4875-9629-67921a1e00f4")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(mapOf("Content-Type" to MediaTypes.HAL_JSON_VALUE))
        .body(
            PactDslJsonBody()
                .stringType("isbn", "9780132350884")
                .stringType("title", "Clean Code")
                .array("authors")
                .stringType("Robert C. Martin")
                .stringType("Dean Wampler")
                .closeArray()
        )
        .toPact()

    @Test
    @PactTestFor(pactMethod = "getExistingBookPact")
    fun `get single existing book interaction`(mockServer: MockServer) {
        settings.url = mockServer.getUrl()

        val book = cut.getBook("b3fc0be8-463e-4875-9629-67921a1e00f4")!!

        assertThat(book.isbn).isEqualTo("9780132350884")
        assertThat(book.title).isEqualTo("Clean Code")
        assertThat(book.authors).isEqualTo(listOf("Robert C. Martin", "Dean Wampler"))
    }

}

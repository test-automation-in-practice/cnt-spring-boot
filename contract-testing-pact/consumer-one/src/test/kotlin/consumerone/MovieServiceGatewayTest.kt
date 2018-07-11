package consumerone

import au.com.dius.pact.consumer.*
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.model.MockProviderConfig
import au.com.dius.pact.model.RequestResponsePact
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.hateoas.MediaTypes
import org.springframework.web.client.RestTemplate

internal class MovieServiceGatewayTest {

    val headers = mapOf("Content-Type" to MediaTypes.HAL_JSON_VALUE)

    val restTemplate = RestTemplate()
    val config = MoviesServiceSettings()
    val cut = MoviesServiceGateway(restTemplate, config)

    @Test fun `get single existing movie interaction`() {
        val pact = ConsumerPactBuilder
                .consumer("consumer-one")
                .hasPactWith("provider")
                .given("Getting movie with any ID returns Iron Man")
                .uponReceiving("get single movie")
                .path("/movies/b3fc0be8-463e-4875-9629-67921a1e00f4")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(PactDslJsonBody()
                        .stringType("title", "Iron Man")
                        .numberType("imdbScore", 7.9f))
                .toPact()

        executeWithPact(pact) { mockServer ->
            config.url = mockServer.getUrl()
            val movie = cut.getMovie("b3fc0be8-463e-4875-9629-67921a1e00f4")!!
            assertThat(movie.title).isEqualTo("Iron Man")
            assertThat(movie.imdbScore).isEqualTo(7.9f)
        }
    }

    private fun executeWithPact(pact: RequestResponsePact, body: (MockServer) -> Unit) {
        val config = MockProviderConfig.createDefault()
        val result = runConsumerTest(pact, config, test(body))
        if (result is PactVerificationResult.Error) {
            throw AssertionError(result.error)
        }
        assertThat(result).isEqualTo(PactVerificationResult.Ok)
    }

    private fun test(body: (MockServer) -> Unit) = object : PactTestRun {
        override fun run(mockServer: MockServer) = body(mockServer)
    }

}

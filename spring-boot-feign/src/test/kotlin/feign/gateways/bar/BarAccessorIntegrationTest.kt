package feign.gateways.bar

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.SimpleWireMockExtension

@SpringBootTest(classes = [BarClientConfiguration::class])
@ExtendWith(SimpleWireMockExtension::class, SpringExtension::class)
internal class WireMockExtensionBasedIntegrationTest(
        @Autowired val settings: BarSettings,
        @Autowired val cut: BarAccessor
) {

    @BeforeEach fun setDynamicUrl(wireMock: WireMockServer) {
        settings.url = "http://localhost:${wireMock.port()}"
    }

    @Test fun `if a server is available, the message of the day is returned`(wireMock: WireMockServer) {
        wireMock.givenThat(get(urlEqualTo("/messageOfTheDay"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"msg": "Hello WireMock!"}""")))

        val result = cut.getMessageOfTheDay()
        assertThat(result).isEqualTo("Hello WireMock!")
    }

    @Test fun `if a server response with error status, null is returned`(wireMock: WireMockServer) {
        wireMock.givenThat(get(urlEqualTo("/messageOfTheDay"))
                .willReturn(aResponse()
                        .withStatus(500)))

        val result = cut.getMessageOfTheDay()
        assertThat(result).isNull()
    }

}
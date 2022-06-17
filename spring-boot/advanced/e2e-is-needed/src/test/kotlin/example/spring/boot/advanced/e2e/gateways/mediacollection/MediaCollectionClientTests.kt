package example.spring.boot.advanced.e2e.gateways.mediacollection

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import example.spring.boot.advanced.e2e.domain.Examples.id_bobiverse1
import example.spring.boot.advanced.e2e.domain.Examples.record_bobiverse1
import example.spring.boot.advanced.e2e.domain.Examples.record_bobiverse2
import example.spring.boot.advanced.e2e.domain.Examples.title_bobiverse1
import example.spring.boot.advanced.e2e.security.TEST_TOKEN_4
import example.spring.boot.advanced.e2e.security.clearSecurityContext
import example.spring.boot.advanced.e2e.security.setSecurityContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.test.context.ActiveProfiles
import java.io.IOException

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(classes = [MediaCollectionClientTestConfiguration::class])
internal class MediaCollectionClientTests(
    @Autowired val cut: MediaCollectionClient,
    @Autowired val wireMock: WireMockServer
) {

    @AfterEach
    fun cleanup() {
        clearSecurityContext()
    }

    @ParameterizedTest
    @ValueSource(ints = [200, 201, 202, 204])
    fun `sends correct request and accepts successful responses`(status: Int) {
        setSecurityContext(TEST_TOKEN_4)

        stub {
            post("/api/media")
                .withHeader(AUTHORIZATION, equalTo("Bearer $TEST_TOKEN_4"))
                .withHeader(CONTENT_TYPE, equalTo("application/json; charset=UTF-8"))
                .withRequestBody(
                    equalToJson(
                        """{ "type": "BOOK", "id": "$id_bobiverse1", "label": "$title_bobiverse1" }"""
                    )
                )
                .willReturn(aResponse().withStatus(status))
        }

        cut.register(record_bobiverse1)
    }

    @ParameterizedTest
    @ValueSource(ints = [400, 401, 403, 404, 500, 504])
    fun `bad server responses throw exception`(status: Int) {
        stub { post("/api/media").willReturn(aResponse().withStatus(status).withBody("oops")) }

        val ex = assertThrows<IOException> { cut.register(record_bobiverse2) }
        assertThat(ex).hasMessage("Failed call [status=$status]: oops")
    }

    private fun stub(supplier: () -> MappingBuilder) {
        wireMock.givenThat(supplier())
    }
}

@Import(MediaCollectionClient::class)
@EnableConfigurationProperties(MediaCollectionServiceProperties::class)
private class MediaCollectionClientTestConfiguration

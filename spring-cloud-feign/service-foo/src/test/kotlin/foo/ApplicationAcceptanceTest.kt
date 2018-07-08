package foo

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.netflix.hystrix.Hystrix
import com.netflix.loadbalancer.Server
import com.netflix.loadbalancer.ServerList
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.cloud.netflix.ribbon.StaticServerList
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = ["eureka.client.enabled=false"]
)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ExtendWith(SpringExtension::class)
internal class ApplicationAcceptanceTest(
        @Autowired val mockMvc: MockMvc,
        @Autowired val wireMock: WireMockServer
) {

    @TestConfiguration
    class AdditionalConfiguration {

        @Bean fun ribbonServerList(@Value("\${wiremock.server.port}") wireMockPort: Int): ServerList<Server> {
            return StaticServerList<Server>(Server("localhost", wireMockPort))
        }

    }

    @BeforeEach fun resetHystrix(): Unit = Hystrix.reset()
    @BeforeEach fun resetWireMock(): Unit = wireMock.resetMappings()

    @Test fun `msg property of foo response is provided by bar service`() {
        wireMock.givenThat(WireMock.get(WireMock.urlEqualTo("/bar"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"msg": "Hello Bar!"}""")))

        val expectedResponse = """
            {
                "msg": "Hello Bar!",
                "answer": 42
            }
            """

        mockMvc.perform(get("/foo"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json(expectedResponse, true))
    }

}
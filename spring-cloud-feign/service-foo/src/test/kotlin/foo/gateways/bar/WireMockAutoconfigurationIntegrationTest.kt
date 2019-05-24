package foo.gateways.bar

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.netflix.hystrix.Hystrix
import com.netflix.loadbalancer.Server
import com.netflix.loadbalancer.ServerList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration
import org.springframework.cloud.netflix.ribbon.StaticServerList
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension

@ComponentScan
@ImportAutoConfiguration(
    FeignAutoConfiguration::class,
    FeignRibbonClientAutoConfiguration::class,
    RibbonAutoConfiguration::class,
    HttpMessageConvertersAutoConfiguration::class,
    JacksonAutoConfiguration::class
)
private class WireMockAutoconfigurationIntegrationTestConfiguration {

    @Bean fun ribbonServerList(@Value("\${wiremock.server.port}") wireMockPort: Int): ServerList<Server> {
        return StaticServerList<Server>(Server("localhost", wireMockPort))
    }

}

@SpringBootTest(
    classes = [WireMockAutoconfigurationIntegrationTestConfiguration::class],
    properties = ["eureka.client.enabled=false"]
)
@AutoConfigureWireMock(port = 0)
internal class WireMockAutoconfigurationIntegrationTest(
    @Autowired val cut: BarClient,
    @Autowired val wireMock: WireMockServer
) {

    @BeforeEach fun resetHystrix(): Unit = Hystrix.reset()
    @BeforeEach fun resetWireMock(): Unit = wireMock.resetMappings()

    @RepeatedTest(10)
    fun `if exception occurs, the fallback is invoked`() {
        val result = cut.get()
        assertThat(result["msg"]).isEqualTo("Hello Fallback!")
    }

    @RepeatedTest(10)
    fun `if server responds, it will be used`() {
        wireMock.givenThat(
            get(urlEqualTo("/bar"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"msg": "Hello WireMock!"}""")
                )
        )

        val result = cut.get()
        assertThat(result["msg"]).isEqualTo("Hello WireMock!")
    }

}
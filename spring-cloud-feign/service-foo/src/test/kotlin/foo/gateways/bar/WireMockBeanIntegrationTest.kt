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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.netflix.ribbon.StaticServerList
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.FeignClientTestConfiguration
import utils.WireMockServerBean

@ComponentScan
class WireMockBeanIntegrationTestConfiguration : FeignClientTestConfiguration() {

    @Bean fun wireMock(): WireMockServer = WireMockServerBean()

    @Bean fun ribbonServerList(server: WireMockServer): ServerList<Server> {
        return StaticServerList<Server>(Server("localhost", server.port()))
    }

}

@SpringBootTest(
        classes = [WireMockBeanIntegrationTestConfiguration::class],
        properties = ["eureka.client.enabled=false"]
)
@ExtendWith(SpringExtension::class)
internal class WireMockBeanIntegrationTest(
        @Autowired val cut: BarClient,
        @Autowired val wireMock: WireMockServer
) {

    @BeforeEach fun resetHystrix(): Unit = Hystrix.reset()
    @BeforeEach fun resetWireMock(): Unit = wireMock.resetMappings()

    @RepeatedTest(10)
    fun `if no server is available, the fallback is invoked`() {
        val result = cut.get()
        assertThat(result["msg"]).isEqualTo("Hello Fallback!")
    }

    @RepeatedTest(10)
    fun `if a server is available, it will be used`() {
        wireMock.givenThat(get(urlEqualTo("/bar"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"msg": "Hello WireMock!"}""")))

        val result = cut.get()
        assertThat(result["msg"]).isEqualTo("Hello WireMock!")
    }

}
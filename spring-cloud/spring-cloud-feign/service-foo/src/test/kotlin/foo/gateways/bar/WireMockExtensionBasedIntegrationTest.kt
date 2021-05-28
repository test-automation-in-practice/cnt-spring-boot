package foo.gateways.bar

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.netflix.hystrix.Hystrix
import com.netflix.loadbalancer.ILoadBalancer
import com.netflix.loadbalancer.Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import utils.SimpleWireMockExtension

@ComponentScan
@ImportAutoConfiguration(
    FeignAutoConfiguration::class,
    FeignRibbonClientAutoConfiguration::class,
    RibbonAutoConfiguration::class,
    HttpMessageConvertersAutoConfiguration::class,
    JacksonAutoConfiguration::class
)
class WireMockExtensionBasedIntegrationTestConfiguration

@ExtendWith(SimpleWireMockExtension::class)
@SpringBootTest(
    classes = [WireMockExtensionBasedIntegrationTestConfiguration::class],
    properties = ["eureka.client.enabled=false"]
)
internal class WireMockExtensionBasedIntegrationTest(
    @Autowired val cut: BarClient
) {

    @MockBean lateinit var loadBalancer: ILoadBalancer

    @BeforeEach fun resetHystrix(): Unit = Hystrix.reset()
    @BeforeEach fun directRibbonToWireMock(wireMock: WireMockServer) {
        given(loadBalancer.chooseServer(any())).willReturn(Server("localhost", wireMock.port()))
    }

    @RepeatedTest(10)
    fun `if exception occurs, the fallback is invoked`() {
        val result = cut.get()
        assertThat(result["msg"]).isEqualTo("Hello Fallback!")
    }

    @RepeatedTest(10)
    fun `if server responds, it will be used`(wireMock: WireMockServer) {
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
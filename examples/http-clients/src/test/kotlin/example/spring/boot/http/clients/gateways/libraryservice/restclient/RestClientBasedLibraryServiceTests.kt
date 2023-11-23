package example.spring.boot.http.clients.gateways.libraryservice.restclient

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceContract
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.zalando.logbook.Logbook

/**
 * Since this is a block-box integration test, it can be implemented using the [LibraryServiceContract].
 */
@WireMockTest
internal class RestClientBasedLibraryServiceTests(
    wireMockInfo: WireMockRuntimeInfo
) : LibraryServiceContract(wireMockInfo) {
    override fun createClassUnderTest(properties: LibraryServiceProperties) =
        RestClientConfiguration(properties).libraryService(Logbook.create())
}

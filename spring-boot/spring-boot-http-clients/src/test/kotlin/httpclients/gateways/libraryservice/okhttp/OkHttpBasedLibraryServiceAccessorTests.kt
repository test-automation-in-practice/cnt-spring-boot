package httpclients.gateways.libraryservice.okhttp

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import httpclients.gateways.libraryservice.LibraryServiceContract
import httpclients.gateways.libraryservice.LibraryServiceProperties
import okhttp3.OkHttpClient

@WireMockTest
internal class OkHttpBasedLibraryServiceAccessorTests(
    wireMockInfo: WireMockRuntimeInfo
) : LibraryServiceContract {
    override val wireMock = wireMockInfo.wireMock
    override val cut = OkHttpBasedLibraryServiceAccessor(
        httpClient = OkHttpClient(),
        properties = LibraryServiceProperties(baseUrl = wireMockInfo.httpBaseUrl)
    )
}

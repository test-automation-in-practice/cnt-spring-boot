package utils

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.jupiter.api.extension.*

class SimpleWireMockExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback, ParameterResolver {

    private companion object {
        const val WIRE_MOCK_SERVER = "WIRE_MOCK_SERVER"
    }

    override fun beforeAll(context: ExtensionContext) {
        val server = WireMockServer(0)
        server.start()
        context.getStore(ExtensionContext.Namespace.GLOBAL).put(WIRE_MOCK_SERVER, server)
    }

    override fun afterAll(context: ExtensionContext) {
        context.getStore(ExtensionContext.Namespace.GLOBAL).get(WIRE_MOCK_SERVER, WireMockServer::class.java).stop()
    }

    override fun beforeEach(context: ExtensionContext) {
        context.getStore(ExtensionContext.Namespace.GLOBAL).get(WIRE_MOCK_SERVER, WireMockServer::class.java).resetMappings()
    }

    override fun supportsParameter(parameterContext: ParameterContext, context: ExtensionContext): Boolean {
        return parameterContext.parameter.type == WireMockServer::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, context: ExtensionContext): Any {
        return context.getStore(ExtensionContext.Namespace.GLOBAL).get(WIRE_MOCK_SERVER, WireMockServer::class.java)
    }

}
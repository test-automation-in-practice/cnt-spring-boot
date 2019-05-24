package utils

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL

class SimpleWireMockExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback, ParameterResolver {

    override fun supportsParameter(parameterContext: ParameterContext, context: ExtensionContext): Boolean {
        return parameterContext.parameter.type == WireMockServer::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, context: ExtensionContext): Any {
        return context.server
    }

    override fun beforeAll(context: ExtensionContext) {
        if (isTopClassContext(context)) {
            context.server = WireMockServer(0).apply { start() }
        }
    }

    override fun afterAll(context: ExtensionContext) {
        if (isTopClassContext(context)) {
            context.server.stop()
        }
    }

    override fun beforeEach(context: ExtensionContext) {
        context.server.resetMappings()
    }

    private var ExtensionContext.server: WireMockServer
        get() = getStore(GLOBAL).get("WIRE_MOCK_SERVER", WireMockServer::class.java)
        set(value) = getStore(GLOBAL).put("WIRE_MOCK_SERVER", value)

    private fun isTopClassContext(context: ExtensionContext) = context.parent.orElse(null) == context.root

}
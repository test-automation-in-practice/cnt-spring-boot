package utils

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class SimpleWireMockExtension : BeforeAllCallback, BeforeEachCallback, ParameterResolver {

    override fun supportsParameter(parameterContext: ParameterContext, context: ExtensionContext): Boolean {
        return parameterContext.parameter.type == WireMockServer::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, context: ExtensionContext): Any {
        return context.server!!
    }

    override fun beforeAll(context: ExtensionContext) {
        if (context.server == null) {
            context.server = CloseableWireMockResource().apply { start() }
        }
    }

    override fun beforeEach(context: ExtensionContext) {
        context.server!!.resetMappings()
    }

    private var ExtensionContext.server: CloseableWireMockResource?
        get() = getStore(GLOBAL).get("WIRE_MOCK_SERVER", CloseableWireMockResource::class.java)
        set(value) = getStore(GLOBAL).put("WIRE_MOCK_SERVER", value)

}

private class CloseableWireMockResource : WireMockServer(0), CloseableResource {

    override fun start() {
        super.start()
        println("### Started WireMock Server ###")
    }

    override fun stop() {
        super.stop()
        println("### Stopped WireMock Server ###")
    }

    override fun close() = stop()
}

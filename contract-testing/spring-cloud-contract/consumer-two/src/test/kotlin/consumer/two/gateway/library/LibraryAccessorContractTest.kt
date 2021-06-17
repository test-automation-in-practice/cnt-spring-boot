package consumer.two.gateway.library

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode

@TestInstance(PER_CLASS)
internal class LibraryAccessorContractTest {

    val settings = LibraryAccessorSettings(url = "http://localhost")
    val cut = LibraryAccessor(settings)

    @JvmField
    @RegisterExtension
    val stubRunnerExtension: StubRunnerExtension = StubRunnerExtension()
        .downloadLatestStub("ws.cnt.ct.scc", "provider", "stubs")
        .withStubPerConsumer(true) // needed because both consumers use similar requests
        .withConsumerName("consumer-two")
        .stubsMode(StubsMode.LOCAL)

    @BeforeEach
    fun setup() {
        settings.url = "${stubRunnerExtension.findStubUrl("ws.cnt.ct.scc", "provider")}"
    }

    @Test
    fun `get single existing book interaction`() {
        val book = cut.getBook("b3fc0be8-463e-4875-9629-67921a1e00f4")!!

        assertThat(book.isbn).isEqualTo("9780132350884")
        assertThat(book.title).isEqualTo("Clean Code")
        assertThat(book.numberOfPages).isEqualTo(464)
    }

}

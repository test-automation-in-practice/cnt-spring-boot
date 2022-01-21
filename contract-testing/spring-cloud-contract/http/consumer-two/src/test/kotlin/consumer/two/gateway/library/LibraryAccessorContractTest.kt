package consumer.two.gateway.library

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

// These tests are executed against the stubs generated and published by the provider
// In this consumer:
//    - the stubs are initialized using annotations
//    - the tests is setup as a Spring Boot integration test

@TestInstance(PER_CLASS)
@AutoConfigureStubRunner(
    ids = ["ws.cnt.ct.scc:provider"],
    stubsPerConsumer = true,
    consumerName = "consumer-two",
    stubsMode = StubsMode.LOCAL
)
@ActiveProfiles("contract-test")
@SpringBootTest(classes = [LibraryAccessorContractTestConfiguration::class])
internal class LibraryAccessorContractTest(
    @Autowired private val cut: LibraryAccessor
) {

    @Test
    fun `get single existing book interaction`() {
        val book = cut.getBook("b3fc0be8-463e-4875-9629-67921a1e00f4")!!

        assertThat(book.isbn).isEqualTo("9780132350884")
        assertThat(book.title).isEqualTo("Clean Code")
        assertThat(book.numberOfPages).isEqualTo(464)
    }

}

@Import(LibraryAccessor::class)
@EnableConfigurationProperties(LibraryAccessorSettings::class)
private class LibraryAccessorContractTestConfiguration

package consumer.messaging

import consumer.books.Book
import consumer.books.Library
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertTimeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.cloud.contract.stubrunner.StubTrigger
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@ActiveProfiles("test")
@EmbeddedKafka(topics = ["book-created"])
@TestInstance(PER_CLASS)
@SpringBootTest(
    webEnvironment = NONE,
    classes = [ContractTestConfiguration::class]
)
@AutoConfigureStubRunner(
    ids = ["ws.cnt.ct.sccm:provider"],
    stubsPerConsumer = false,
    consumerName = "consumer",
    stubsMode = LOCAL
)
internal class EventHandlerMessagingContractTest {

    @Autowired
    private lateinit var trigger: StubTrigger

    @Autowired
    private lateinit var library: Library

    @Test
    fun `a create event for Clean Code is published`() {
        trigger.trigger("a_create_event_for_Clean_Code_is_published")

        assertTimeout(Duration.ofSeconds(3)) {
            verify { library.add(ContractTestData.cleanCode) }
        }
    }
}

@ComponentScan
@EnableAutoConfiguration
private class ContractTestConfiguration {

    @Bean
    fun mockLibrary(): Library = mockk()
}

object ContractTestData {
    val cleanCode = Book(isbn = "9780132350884", title = "Clean Code")
}

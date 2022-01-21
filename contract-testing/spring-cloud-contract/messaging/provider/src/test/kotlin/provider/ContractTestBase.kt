package provider

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import provider.books.Book
import provider.messaging.PublishMessageFunction

@ActiveProfiles("test")
@AutoConfigureMessageVerifier
@SpringBootTest(webEnvironment = NONE)
@EmbeddedKafka(partitions = 1, topics = ["book-created"])
abstract class ContractTestBase {

    @Autowired
    lateinit var publishMessage: PublishMessageFunction

    fun publishCleanCode() {
        publishMessage(Book(isbn = "9780132350884", title = "Clean Code"))
    }

}


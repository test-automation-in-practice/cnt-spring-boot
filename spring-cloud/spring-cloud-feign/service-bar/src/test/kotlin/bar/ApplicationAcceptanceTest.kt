package bar

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@DirtiesContext
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest {

    @Test fun contextLoads() = Unit

}

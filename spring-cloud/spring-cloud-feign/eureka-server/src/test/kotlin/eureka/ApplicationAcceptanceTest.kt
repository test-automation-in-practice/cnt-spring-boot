package eureka

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest {

    @Test fun contextLoads() = Unit

}

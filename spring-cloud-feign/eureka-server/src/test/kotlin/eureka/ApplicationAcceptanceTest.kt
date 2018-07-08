package eureka

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension::class)
internal class ApplicationAcceptanceTest {

    @Test fun contextLoads() = Unit

}

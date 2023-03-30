package example.spring.boot.scheduling

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.scheduling.services.SomeOtherService
import example.spring.boot.scheduling.services.SomeService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@MockkBean(SomeService::class, SomeOtherService::class)
class ApplicationTests {

    // @MockkBean is used because this example does not have an implementation for these services

    @Test
    fun `application can be started`() {
        // if it starts, everything is wired correctly
    }

}

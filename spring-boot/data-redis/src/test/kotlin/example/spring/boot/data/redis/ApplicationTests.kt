package example.spring.boot.data.redis

import example.spring.boot.data.redis.config.BookRecordToByteArrayConverter
import example.spring.boot.data.redis.config.ByteArrayToBookRecordConverter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApplicationTests {

    @Test
    fun `special converters are not part of the application context`(
        @Autowired converter1: BookRecordToByteArrayConverter?,
        @Autowired converter2: ByteArrayToBookRecordConverter?
    ) {
        assertThat(converter1).isNull()
        assertThat(converter2).isNull()
    }
}

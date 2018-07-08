package caching.calculation

import caching.CacheConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.Cache
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.stream.IntStream

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [CacheFibonacciCalculatorTest.TestConfiguration::class])
internal class CacheFibonacciCalculatorTest {

    @EnableCaching
    @ComponentScan
    @Import(CacheConfiguration::class)
    class TestConfiguration

    @Autowired lateinit var cut: CacheFibonacciCalculator
    @Autowired lateinit var caches: List<Cache>

    @BeforeEach fun clearCaches() = caches.forEach { it.clear() }

    @CsvSource("1,1", "2,1", "3,2", "4,3", "5,5", "6,8", "7,13", "8,21", "9,34", "10,55")
    @ParameterizedTest(name = "{0}. Fibonacci Number is {1}")
    fun `algorithm is correct`(number: Int, expectedValue: Long) {
        assertThat(cut.fibonacci(number)).isEqualTo(expectedValue)
    }

    @Test fun `45th fibonacci number is 1134903170`() {
        assertThat(cut.fibonacci(45)).isEqualTo(1134903170)
    }

    @Test fun `fibonacci calculation scales`() {
        IntStream.range(1, 10_000)
                .forEach { cut.fibonacci(it) }
    }

}
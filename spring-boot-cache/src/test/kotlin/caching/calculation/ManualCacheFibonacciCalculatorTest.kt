package caching.calculation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


internal class ManualCacheFibonacciCalculatorTest {

    val cut = ManualCacheFibonacciCalculator()

    @CsvSource("1,1", "2,1", "3,2", "4,3", "5,5", "6,8", "7,13", "8,21", "9,34", "10,55")
    @ParameterizedTest(name = "{0}. Fibonacci Number is {1}")
    fun `algorithm is correct`(number: Int, expectedValue: Long) {
        assertThat(cut.fibonacci(number)).isEqualTo(expectedValue)
    }

    @Test fun `45th fibonacci number is 1134903170 - and fast`() {
        assertThat(cut.fibonacci(45)).isEqualTo(1134903170)
    }

}
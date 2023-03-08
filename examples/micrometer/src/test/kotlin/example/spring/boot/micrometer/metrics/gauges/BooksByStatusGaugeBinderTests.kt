package example.spring.boot.micrometer.metrics.gauges

import example.spring.boot.micrometer.business.Status.*
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.scheduling.config.ScheduledTaskRegistrar

internal class BooksByStatusGaugeBinderTests {

    private val registry = SimpleMeterRegistry()
    private val getGaugeUpdateData: BooksByStatusGaugeDataSupplier = mockk()
    private val cut = BooksByStatusGaugeBinder(registry, getGaugeUpdateData)

    @Test
    fun `registers itself if scheduling is enabled`() {
        val registrar: ScheduledTaskRegistrar = mockk(relaxed = true)
        cut.configureTasks(registrar)
        verify { registrar.addFixedRateTask(any()) }
    }

    @Test
    fun `after updating there are no gauges if there is no data`() {
        stubUpdateAndRun()
        assertGaugeValues()
    }

    @Test
    fun `after updating there is a gauge if there was some data`() {
        stubUpdateAndRun(
            BooksByStatusGaugeKey(Available) to 42,
        )
        assertGaugeValues(available = 42)
    }

    @Test
    fun `after updating stale gauges are set to zero`() {
        stubUpdateAndRun(
            BooksByStatusGaugeKey(Available) to 39,
            BooksByStatusGaugeKey(Borrowed) to 2,
            BooksByStatusGaugeKey(Archived) to 1,
        )
        assertGaugeValues(available = 39, borrowed = 2, archived = 1)

        stubUpdateAndRun(
            BooksByStatusGaugeKey(Available) to 40,
            BooksByStatusGaugeKey(Archived) to 2,
        )
        assertGaugeValues(available = 40, borrowed = 0, archived = 2)
    }

    private fun stubUpdateAndRun(vararg data: Pair<BooksByStatusGaugeKey, Long>) {
        every { getGaugeUpdateData() } returns data.toMap()
        cut.run()
    }

    private fun assertGaugeValues(available: Long? = null, borrowed: Long? = null, archived: Long? = null) {
        assertThat(getGaugeValue("available")).isEqualTo(available)
        assertThat(getGaugeValue("borrowed")).isEqualTo(borrowed)
        assertThat(getGaugeValue("archived")).isEqualTo(archived)
    }

    private fun getGaugeValue(status: String): Long? =
        registry.find("books.by-status.gauge").tag("status", status).gauge()?.value()?.toLong()

}

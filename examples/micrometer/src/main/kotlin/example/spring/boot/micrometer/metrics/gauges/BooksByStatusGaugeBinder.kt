package example.spring.boot.micrometer.metrics.gauges

import example.spring.boot.micrometer.scheduling.FixedRateSelfSchedulingComponent
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Duration.ofMinutes
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.Map.Entry

@Component
class BooksByStatusGaugeBinder(
    private val registry: MeterRegistry,
    private val getGaugeUpdateData: BooksByStatusGaugeDataSupplier
) : FixedRateSelfSchedulingComponent {

    override val initialDelay: Duration = ofMinutes(1)
    override val interval: Duration = ofMinutes(5)

    private val cache = mutableMapOf<BooksByStatusGaugeKey, AtomicLong>()

    override fun run() = getGaugeUpdateData()
        .onEach(::setGaugeToValue)
        .let(::getStaleGaugeKeys)
        .forEach(::setGaugeToZero)

    private fun setGaugeToValue(entry: Entry<BooksByStatusGaugeKey, Long>) {
        cache.computeIfAbsent(entry.key, ::createAndBindGauge).set(entry.value)
    }

    private fun createAndBindGauge(key: BooksByStatusGaugeKey) = AtomicLong()
        .also { registry.gauge("books.by-status.gauge", key.toTags(), it) }

    private fun BooksByStatusGaugeKey.toTags(): List<Tag> =
        listOf(Tag.of("status", status.name.lowercase()))

    private fun getStaleGaugeKeys(update: Map<BooksByStatusGaugeKey, Long>) =
        (cache.keys - update.keys)

    private fun setGaugeToZero(it: BooksByStatusGaugeKey) {
        cache.getValue(it).set(0)
    }

}

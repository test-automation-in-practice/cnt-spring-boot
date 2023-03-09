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
class BooksGaugeBinder(
    private val registry: MeterRegistry,
    private val getGaugeUpdateData: BooksGaugeDataSupplier
) : FixedRateSelfSchedulingComponent {

    override val initialDelay: Duration = ofMinutes(1)
    override val interval: Duration = ofMinutes(5)

    private val cache = mutableMapOf<BooksGaugeKey, AtomicLong>()

    override fun run() = getGaugeUpdateData()
        .onEach(::setGaugeToValue)
        .let(::getStaleGaugeKeys)
        .forEach(::setGaugeToZero)

    private fun setGaugeToValue(entry: Entry<BooksGaugeKey, Long>) =
        cache.computeIfAbsent(entry.key, ::createAndBindGauge).set(entry.value)

    private fun createAndBindGauge(key: BooksGaugeKey): AtomicLong =
        registry.gauge("books.by-status.gauge", key.toTags(), AtomicLong())!!

    private fun BooksGaugeKey.toTags(): List<Tag> =
        listOf(Tag.of("status", status.name.lowercase()))

    private fun getStaleGaugeKeys(update: Map<BooksGaugeKey, Long>) =
        (cache.keys - update.keys)

    private fun setGaugeToZero(it: BooksGaugeKey) =
        cache.getValue(it).set(0)

}

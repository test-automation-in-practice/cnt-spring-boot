package example.spring.boot.micrometer.metrics.gauges

fun interface BooksByStatusGaugeDataSupplier : () -> Map<BooksByStatusGaugeKey, Long>

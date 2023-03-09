package example.spring.boot.micrometer.metrics.gauges

fun interface BooksGaugeDataSupplier : () -> Map<BooksGaugeKey, Long>

package example.spring.boot.micrometer.metrics.gauges

import example.spring.boot.micrometer.business.Status

data class BooksByStatusGaugeKey(val status: Status)

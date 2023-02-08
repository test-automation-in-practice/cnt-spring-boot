package example.spring.boot.jms.activemq

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("activemq")
data class ActiveMqProperties(
    val broker: Broker,
    val redelivery: Redelivery,
) {

    data class Broker(
        val url: String,
        val username: String?,
        val password: String?,
    )

    data class Redelivery(
        val maximumRedeliveries: Int = 6,
    )

}

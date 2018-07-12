package feign.gateways.bar

import feign.Headers
import feign.RequestLine


@Headers("Content-Type: application/json")
internal interface BarClient {

    @RequestLine("GET /messageOfTheDay")
    fun get(): MessageOfTheDayResponse

}

data class MessageOfTheDayResponse(val msg: String)
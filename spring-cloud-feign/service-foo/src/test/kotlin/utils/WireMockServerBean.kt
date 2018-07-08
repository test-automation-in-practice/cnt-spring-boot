package utils

import com.github.tomakehurst.wiremock.WireMockServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

class WireMockServerBean : WireMockServer(0) {

    @PostConstruct fun postConstruct() {
        start()
    }

    @PreDestroy fun preDestroy() {
        stop()
    }

}
package utils

import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration

@ImportAutoConfiguration(
        FeignAutoConfiguration::class,
        FeignRibbonClientAutoConfiguration::class,
        RibbonAutoConfiguration::class,
        HttpMessageConvertersAutoConfiguration::class,
        JacksonAutoConfiguration::class
)
open class FeignClientTestConfiguration
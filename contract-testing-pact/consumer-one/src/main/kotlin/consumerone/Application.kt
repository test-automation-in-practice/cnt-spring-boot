package consumerone

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
@EnableConfigurationProperties(MoviesServiceSettings::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Component
class ApplicationLogic(
        private val gateway: MoviesServiceGateway
) : CommandLineRunner {

    override fun run(vararg args: String) {
        println()
        println("Received the following movies from the producer:")
        gateway.getMovies().forEach { movie -> println(" - $movie") }
        println()
    }

}

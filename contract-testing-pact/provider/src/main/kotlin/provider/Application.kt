package provider

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import provider.core.Movie
import provider.core.MovieDataStore

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Component
class ApplicationSetup(
        private val dataStore: MovieDataStore
) : CommandLineRunner {

    override fun run(vararg args: String) {
        dataStore.create(Movie(
                title = "Batman Begins",
                description = "Lorem Ipsum ...",
                releaseYear = 2005,
                imdbScore = 8.3f,
                metacriticScore = 0.70f
        ))
        dataStore.create(Movie(
                title = "The Dark Knight",
                description = "Lorem Ipsum ...",
                releaseYear = 2008,
                imdbScore = 9.0f,
                metacriticScore = 0.82f
        ))
        dataStore.create(Movie(
                title = "The Dark Knight Rises",
                description = "Lorem Ipsum ...",
                releaseYear = 2012,
                imdbScore = 8.5f,
                metacriticScore = 0.78f
        ))
    }

}
